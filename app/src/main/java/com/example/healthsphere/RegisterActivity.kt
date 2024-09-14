package com.example.healthsphere

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firestore.FirestoreClass
import com.example.healthsphere.databinding.ActivityRegisterBinding
import com.example.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BazeActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {
    private lateinit var et_username: TextInputEditText
    private lateinit var et_phoneNo: TextInputEditText
    private lateinit var et_emailAdress: TextInputEditText
    private lateinit var et_password: TextInputEditText
    private lateinit var et_confirmPass: TextInputEditText
    private lateinit var cb_checkbox: CheckBox
    private lateinit var sign_up: TextView
    private lateinit var mbinding: ActivityRegisterBinding
    private lateinit var phoneLayout: TextInputLayout
    private lateinit var nameLayout: TextInputLayout
    private lateinit var cpassLayout: TextInputLayout
    private lateinit var passLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mbinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mbinding.root)
        // Find views using mbinding
        nameLayout = mbinding.usernameLayout
        cpassLayout = mbinding.cPassLayout
        passLayout = mbinding.passLayout
        emailLayout = mbinding.emailLayout
        phoneLayout = mbinding.phoneNoLayout
        sign_up = mbinding.signUp
        et_username = mbinding.etUsername
        et_phoneNo = mbinding.etPhoneNo
        et_emailAdress = mbinding.etEmailAdress
        et_password = mbinding.etPassword
        et_confirmPass = mbinding.etConfirmPass
        cb_checkbox = mbinding.cbCheckbox

//        phoneLayout = findViewById(R.id.phoneNoLayout)
        mbinding.etUsername.onFocusChangeListener = this
        mbinding.etPhoneNo.onFocusChangeListener = this
        mbinding.etPassword.onFocusChangeListener = this
        mbinding.etEmailAdress.onFocusChangeListener = this
        mbinding.etConfirmPass.onFocusChangeListener = this

        sign_up.setOnClickListener {
            var signIntent = Intent(this, OTP_Activity::class.java)
            startActivity(signIntent)
        }
        sign_up.setOnClickListener{
            registerUser()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun validateUserName(): Boolean {
        var errorMessage: String? = null
        val value: String = mbinding.etUsername.text.toString()
        if (value.isEmpty()) {
            errorMessage = "full name is Required"
        }
        if (errorMessage != null) {
            mbinding.etUsername.apply {
                nameLayout.isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun validateEmail(): Boolean {
        var errormessage: String? = null
        val value: String = mbinding.etEmailAdress.text.toString()
        if (value.isEmpty()) {
            errormessage = "Please Enter your Address"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errormessage = "Invalid Email Address"
        }
        if (errormessage != null) {
            mbinding.etEmailAdress.apply {
                emailLayout.isErrorEnabled = true
                error = errormessage
            }
        }
        return errormessage == null
    }

    private fun validatePhoneNo(): Boolean {
        var errorMessage: String? = null
        val value: String = mbinding.etPhoneNo.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Please Enter phone number"
        }
        if (errorMessage != null) {
            mbinding.etPhoneNo.apply {
                phoneLayout.isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun validatePass(): Boolean {
        var errorMessage: String? = null
        val value: String = mbinding.etPassword.text.toString().trim()
        if (value.isEmpty()) {
            errorMessage = "Please input password"
        } else if (value.length < 8) {
            errorMessage = "password must be 8 characters and Above! "
        }
        if (errorMessage != null) {
            mbinding.etPassword.apply {
                passLayout.isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun confirmPass(): Boolean {
        var errorMessage: String? = null
        val value: String = mbinding.etConfirmPass.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Please Enter password"
        }
        if (errorMessage != null) {
            mbinding.etConfirmPass.apply {
                cpassLayout.isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun matchPass(): Boolean {
        var errorMessage: String? = null
        val pas1: String = mbinding.etPassword.text.toString().trim()
        val pass2: String = mbinding.etConfirmPass.text.toString().trim()
        if (pass2 != pas1) {
            errorMessage = " password should match"
        }
        if (errorMessage != null) {
            mbinding.etConfirmPass.apply {
                cpassLayout.isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }


    override fun onClick(view: View?) {
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.et_username -> {
                    if (hasFocus) {
                        if (mbinding.usernameLayout.isErrorEnabled) {

                            mbinding.usernameLayout.isErrorEnabled = false
                        }

                    } else {
                        validateUserName()
                    }
                }
                R.id.et_phoneNo -> {
                    if (hasFocus) {
                        if (mbinding.phoneNoLayout.isErrorEnabled) {
                            mbinding.phoneNoLayout.isErrorEnabled = false
                        }
                    } else {
                        validatePhoneNo()
                    }
                }
                R.id.cb_checkbox ->{
                    if(hasFocus){
                        matchPass()
                    }
                }
                R.id.et_emailAdress -> {
                    if (hasFocus) {
                        if (mbinding.emailLayout.isErrorEnabled) {
                            mbinding.emailLayout.isErrorEnabled = false
                        }

                    } else {
                        validateEmail()
                    }

                }

                R.id.et_password -> {
                    if (hasFocus) {
                        if (mbinding.passLayout.isErrorEnabled)
                            mbinding.passLayout.isErrorEnabled = false
                    } else {
                        if (validatePass() && mbinding.etConfirmPass.text!!.isNotEmpty() && confirmPass() &&
                            matchPass()
                        ) {
                            if (mbinding.passLayout.isErrorEnabled) {
                                mbinding.passLayout.isErrorEnabled = false
                            }

                        }
                    }
                }

                R.id.et_confirmPass -> {
                    if (hasFocus) {
                        if (mbinding.cPassLayout.isErrorEnabled)
                            mbinding.cPassLayout.isErrorEnabled = false
                    } else {

                        if (validatePass() && confirmPass() && matchPass()) {
                            if (mbinding.passLayout.isErrorEnabled) {
                                mbinding.passLayout.isErrorEnabled = false
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }
    private fun validateRegisterDetails(): Boolean{
        return when {
            TextUtils.isEmpty(et_username.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.error_fnameMissing), true)
                false
            }

            TextUtils.isEmpty(et_phoneNo.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.errorPhonoNo), true)
                false
            }
            TextUtils.isEmpty(et_emailAdress.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.email_error), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.errorPass), true)
                false
            }

            TextUtils.isEmpty(et_confirmPass.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.errorPass), true)
                false
            }
            (et_password.text.toString().trim() != et_confirmPass.text.toString().trim()) ->{
                showErrorSnackBar("password should match", true)
                false
            }

            !cb_checkbox.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.Checkbox_error),true)
                false
            }
            else -> {
//                showErrorSnackBar(resources.getString(R.string.sucessregistration), false)
                true
            }

        }
    }
    private fun registerUser(){
        if(validateRegisterDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_emailAdress.text.toString().trim()
            val password: String = et_password.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if(task.isSuccessful){
                            //firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            et_username.text.toString().trim(),
                            et_phoneNo.text.toString().trim(),
                            et_emailAdress.text.toString().trim(),


                            )
                        FirestoreClass().registerUser(this, user)
                            finish()
                            Toast.makeText(this, "Success You are being redirected to Login.",Toast.LENGTH_SHORT)
                                .show()
                            showErrorSnackBar(
                                "you are successfully registered. Your User ID is${firebaseUser.uid}",
                                false
                            )
//                            FirebaseAuth.getInstance().signOut()
//                            finish()
                        }
                        else{
                            // if the register is not successful
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)

                        }
                    }
                )
        }

    }
    fun userRegistrationSucceess(){
        hideProgressDialog()
        Toast.makeText(this, "You have successfully Registered to our Services ", Toast.LENGTH_SHORT).show()
    }
}