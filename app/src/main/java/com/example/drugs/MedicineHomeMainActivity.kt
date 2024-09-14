package com.example.drugs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthsphere.R
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MedicineHomeMainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView
    private var content: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.medicine_main_home)
        // Initialize BottomNavigationView
        navigation = findViewById(R.id.navigation)

        // Set up the listener for BottomNavigationView
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_medicine -> {
                    // Launch an activity (MedicineDetailActivity)
                    val homeIntent = Intent(this@MedicineHomeMainActivity, DrugCategoriesActivity::class.java)
                    startActivity(homeIntent)
                    true
                }
                R.id.navigation_cart -> {
                    // Load the fragment (MedicineCart)
                    val fragment = MedicineCart() // Replace with your actual fragment
                    addFragment(fragment)
                    true
                }
                R.id.orders_nav -> {
                    // Load the fragment (MedicineCart)
                    val fragment = MedicineOrdersFragment() // Replace with your actual fragment
                    addFragment(fragment)
                    true
                }
                else -> false
            }
        }

        // Set the listener to the BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Initially load the fragment when the activity starts
//        val initialFragment = MedicineCart()  // Set this as your default fragment
//        addFragment(initialFragment)

        // Handle window insets (optional, if necessary for padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to add or replace a fragment
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}



//package com.example.drugs
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.healthsphere.R
//import android.widget.FrameLayout
//import androidx.fragment.app.Fragment
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class MedicineHomeMainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.medicine_main_home)
//        class MainActivity : AppCompatActivity() {
//            private var content: FrameLayout? = null
//
//            private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//                when (item.itemId) {
//                    R.id.navigation_call -> {
//                        val homeIntent = Intent(this@MedicineHomeMainActivity, MedicineDetailActivity::class.java)
//                        startActivity(homeIntent)
//                        return@OnNavigationItemSelectedListener true
////                        val fragment = CallFragment()
////                        addFragment(fragment)
////                        return@OnNavigationItemSelectedListener true
//                    }
//                    R.id.navigation_search -> {
//                        val fragment = MedicineCart()
//                        addFragment(fragment)
//                        return@OnNavigationItemSelectedListener true
//                    }
//                }
//                false
//            }
//
//            private fun addFragment(fragment: Fragment) {
//                supportFragmentManager
//                    .beginTransaction()
//                    .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
//                    .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
//                    .commit()
//            }
//                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//                val fragment = CallFragment()
//                addFragment(fragment)
//            }
//        }
//        view raw
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}