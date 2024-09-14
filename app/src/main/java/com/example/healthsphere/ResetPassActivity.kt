package com.example.healthsphere

import android.content.Intent
import android.os.BaseBundle
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ResetPassActivity : BazeActivity() {
    private lateinit var et_emailAdress: TextInputEditText
    private lateinit var resetLink: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_pass)
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        et_emailAdress = findViewById(R.id.et_emailAdress)
        resetLink = findViewById(R.id.resetLink)
        val goBack: LinearLayout = findViewById(R.id.backbtn)

        goBack.setOnClickListener{
            val gobackIntent : Intent= Intent( this, SignIn::class.java)
            startActivity(gobackIntent)
            finish()
        }

        resetLink.setOnClickListener {
            val email = et_emailAdress.text.toString().trim()
            if(email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.email_error), true)

            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if(task.isSuccessful){
                            Toast.makeText(this, resources.getString(R.string.email_sent), Toast.LENGTH_LONG
                            ).show()
                            //return back to login page
                            val backToLoginIntent = Intent(this,SignIn::class.java)
                            startActivity(backToLoginIntent)
                            finish()
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}