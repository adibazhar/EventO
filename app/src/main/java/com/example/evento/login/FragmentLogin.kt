package com.example.evento.login


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.evento.MainActivity

import com.example.evento.R
import com.example.evento.data.viewmodel.FirebaseViewModel
import com.example.evento.data.viewmodel.UserViewModel
import com.example.evento.data.model.UserInfo
import com.example.evento.util.onEditTextClicked
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.text_input_email
import kotlinx.android.synthetic.main.fragment_login.text_input_password
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "FragmentLogin"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentLogin.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentLogin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private val userInfo: UserInfo by inject()
    private val firebaseViewModel: FirebaseViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    // private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        //  viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input_email.onEditTextClicked(text_input_email)
        text_input_password.onEditTextClicked(text_input_password)


        btn_signup.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left,
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right
            )
            fragmentTransaction.addToBackStack(FragmentLogin::class.java.name)
            fragmentTransaction.replace(R.id.fragment_container, FragmentSignUp.newInstance())
            fragmentTransaction.commit()
        }

        btn_login.setOnClickListener {

            val email = text_input_email.editText!!.text.toString()
            val password = text_input_password.editText!!.text.toString()

            if (email.isNullOrEmpty() or password.isNullOrEmpty()) {
                when {
                    email.isNullOrEmpty() -> text_input_email.error = "Field is required"
                    password.isNullOrEmpty() -> text_input_password.error = "Field is required"
                }
                return@setOnClickListener
            }

            loading.visibility = View.VISIBLE

            firebaseViewModel.signIn(email, password).observe(this, Observer {

                if (it.errorMessages != null) {
                    loading.visibility = View.GONE
                    Toast.makeText(activity, "${it.errorMessages.toString()}", Toast.LENGTH_SHORT)
                        .show()
                    return@Observer
                }
                val data = it.authResult!!

                userInfo.uid = data.user!!.uid
                userInfo.email = data.user!!.email!!
//                userInfo.username = data.additionalUserInfo!!.username!!

                userViewModel.saveUserData(userInfo)

                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                loading.visibility = View.GONE
            })
//            auth.signInWithEmailAndPassword(email,password)
//                .addOnSuccessListener {
//                val intent = Intent(activity,MainActivity::class.java)
//                intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                loading.visibility = View.GONE
//                }
//                .addOnFailureListener {
//                    Toast.makeText(activity,"${it.message.toString()}",Toast.LENGTH_SHORT).show()
//                    loading.visibility = View.GONE
//                }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentLogin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FragmentLogin().apply {}
    }
}
