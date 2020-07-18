package com.example.evento.login


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer

import com.example.evento.R
import com.example.evento.data.viewmodel.FirebaseViewModel
import com.example.evento.data.model.UserInfo
import com.example.evento.util.isEmailValid
import com.example.evento.util.isPasswordValid
import com.example.evento.util.onEditTextClicked
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_fragment_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "FragmentSignUp"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSignUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSignUp : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private val viewModel: FirebaseViewModel by viewModel()
    private val userInfo : UserInfo by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
        auth = FirebaseAuth.getInstance()
        // viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input_username.onEditTextClicked(text_input_username)
        text_input_email.onEditTextClicked(text_input_email)
        text_input_password.onEditTextClicked(text_input_password)

        signup(view)
    }

    private fun signup(view: View) {
        btn_register.setOnClickListener {

            val username = text_input_username.editText!!.text.toString()
            val email = text_input_email.editText!!.text.toString()
            val password = text_input_password.editText!!.text.toString()
            val fragmentManager = activity!!.supportFragmentManager
            when {
                username.isNullOrEmpty() -> {
                    text_input_username.error = "Field required"
                    return@setOnClickListener
                }

                email.isNullOrEmpty() -> {
                    text_input_email.error = "Field required"
                    return@setOnClickListener
                }
                password.isNullOrEmpty() -> {
                    text_input_password.error = "Field required"
                    return@setOnClickListener
                }
            }
            Log.d("SignUp", "Username = $username ,Email = $email , Pass = $password")
            when {
                !email.isEmailValid(email) -> {
                    text_input_email.error = "Invalid email format"
                    return@setOnClickListener
                }
                !password.isPasswordValid(password) -> {
                    text_input_password.error =
                        "Password need to contain lowercase word and digit. Minimum 6 characters"
                    return@setOnClickListener
                }
            }

            btn_register.isEnabled = false
            loading_sign_up.visibility = View.VISIBLE
            viewModel.signUp(email, password).observe(this, Observer {
                Log.d("SignUpFragment", it.authResult.toString() + "" + it.errorMessages.toString())
                loading_sign_up.visibility = View.GONE
                btn_register.isEnabled = true
                if (it.errorMessages != null) {
                    Toast.makeText(activity, it.errorMessages, Toast.LENGTH_SHORT).show()
                    return@Observer
                }

                CoroutineScope(Dispatchers.IO).launch{
                    userInfo.uid = it.authResult!!.user!!.uid
                    userInfo.email = it.authResult!!.user!!.email!!
                    userInfo.username = username
                    viewModel.uploadUserInfo(userInfo)
                }
                Toast.makeText(
                    activity,
                    "Successfully create user with email ${it.authResult!!.user!!.email}",
                    Toast.LENGTH_SHORT
                ).show()
                fragmentManager.popBackStack()
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSignUp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FragmentSignUp().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
