package com.example.evento

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.evento.data.UserViewModel
import com.example.evento.login.ActivityLogin
import com.example.evento.data.FirebaseViewModel
import com.example.evento.main.adapter.ViewPagerAdapter
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

const val TAG = "ActivityMain"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firebaseViewModel: FirebaseViewModel by inject()
        val userViewModel: UserViewModel by inject()
        val toolbar = findViewById<Toolbar>(R.id.container_toolbar)
        toolbar.apply {
            title = "EventO"
            setTitleTextColor(resources.getColor(R.color.white))
        }

        val fragmentAdapter = ViewPagerAdapter(
            supportFragmentManager
        )
        viewpager_main.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpager_main, true)

        firebaseViewModel.getUserInfo().observe(this, Observer {
            if (it.error != null) {
                Toast.makeText(this, "${it.error}", Toast.LENGTH_SHORT).show()
                Log.d("ReadUser", "${it.error}")
                return@Observer
            }

            Log.d("ReadUser", "${it.userInfo!!.username}")
        })

        account_icon.setOnClickListener {
            firebaseViewModel.signOut()
            CoroutineScope(Dispatchers.IO).launch {
                val deleteResult = userViewModel.deleteUserData()
                Log.d(TAG, "Row Deleted = $deleteResult")

            }
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        userViewModel.getUserData().observe(this, Observer {
            if (it == null) return@Observer
            Toast.makeText(this, "Welcome ${it.email}", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        val playstoreAvail = GoogleApiAvailability.getInstance()
        println("playstore = $playstoreAvail")
    }
}
