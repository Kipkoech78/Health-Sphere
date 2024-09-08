package com.example.healthsphere

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        setContentView(R.layout.activity_splash)


        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.postDelayed({
            if (firebaseUser != null){
                val homeIntent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(homeIntent)
                finish()
            }
            else{
                val intent = Intent(this, MainSlide::class.java)
                startActivity(intent)
                finish()
            }
            // Code to run on the main thread after a delay

        }, 2000) // Delay in milliseconds (2000ms = 2s)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}