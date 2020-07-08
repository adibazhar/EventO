package com.example.evento.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.example.evento.MainActivity
import com.example.evento.R
import com.example.evento.data.FirebaseViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel

class ActivityLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val viewModel: FirebaseViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = FirebaseAuth.getInstance()
        val user = viewModel.getCurrentUser()

        if (user == null) {
            Log.d("Login", "User null")
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            val fragment = FragmentLogin.newInstance()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        } else {
            Log.d("Login", "User signed in")
            Log.d("Login", "${user.uid}")
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val googlePlayStore = GoogleApiAvailability.getInstance()
        val googleAvailable = googlePlayStore.isGooglePlayServicesAvailable(this,googlePlayStore.getApkVersion(application))

        if (googleAvailable != ConnectionResult.SUCCESS) {
            googlePlayStore.getErrorDialog(this, 0, 0) {

            }.show()
            finish()
        }
    }
}
