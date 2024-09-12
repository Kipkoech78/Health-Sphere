package com.example.healthsphere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.drugs.AddDrugsActivity
import com.example.fragments.CommunityFragment
import com.example.fragments.ContactFragment
import com.example.fragments.FeedFragment
import com.example.fragments.HomeFragment
import com.example.fragments.LogoutFragment
import com.example.fragments.ProfileFragment
import com.example.fragments.PrimeFragment
import com.example.fragments.WorkoutFragment
import com.example.healthsphere.databinding.ActivityMainBinding
import com.example.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.StartupTime
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var fragmentmanager : FragmentManager
    private lateinit var binding : ActivityMainBinding
    private lateinit var toolbar_title: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        toolbar_title = binding.toolbarTitle
        //set full screen

        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!


        toolbar_title.text = "Welcome $username."
        // Apply window insets to toolbar to ensure it doesn't overlap with the system UI
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(view.paddingLeft, systemBarsInsets.top, view.paddingRight, view.paddingBottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        binding.BottomNavigation.background = null
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set default selected item as Home
        binding.BottomNavigation.selectedItemId = R.id.bottom_home

        binding.BottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.workouts -> openFragment(WorkoutFragment())
                R.id.feed -> openFragment(FeedFragment())
                R.id.Community ->{
                    val intent = Intent(this,DoctorDashboardActivity::class.java)
                    startActivity(intent)
                }
//
            }
            true
        }
        // Ensure no item is selected initially
        fragmentmanager = supportFragmentManager
        openFragment(HomeFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_admin -> openFragment(PrimeFragment())
            R.id.nav_profile -> {
                // Start the ProfileActivity instead of a fragment
                val intent = Intent(this, EditProfile::class.java)
                startActivity(intent)
            }
            R.id.nav_contact -> openFragment(ContactFragment())
            R.id.nav_logout -> {
                val logout_intent= Intent(this@MainActivity, SignIn::class.java)
                FirebaseAuth.getInstance().signOut()
                logout_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                logout_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(logout_intent)
            }
            R.id.nav_health -> Toast.makeText(this,"Your Health Priority",Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }

    }
    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentmanager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
