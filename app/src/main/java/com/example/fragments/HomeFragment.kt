package com.example.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.drugs.AddDrugsActivity
import com.example.healthsphere.FindDoctors
import com.example.healthsphere.LabTestActivity
import com.example.healthsphere.OrderDetActivity
import com.example.healthsphere.R
import com.example.utils.Constants

class HomeFragment : Fragment() {
    private lateinit var doctorsCard: RelativeLayout
    private lateinit var labtest: RelativeLayout
    private lateinit var orderDetails: RelativeLayout
    private lateinit var medicine: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        doctorsCard = view.findViewById(R.id.doctorsCard)
        orderDetails = view.findViewById(R.id.orderDetails)
        labtest = view.findViewById(R.id.labtest)
        medicine = view.findViewById(R.id.medicine)

        labtest.setOnClickListener {
            val intentLab = Intent(activity, LabTestActivity::class.java)
            startActivity(intentLab)
        }
        medicine.setOnClickListener {
           //display medicine
        }
        orderDetails.setOnClickListener {
            val orderintent = Intent(activity, OrderDetActivity::class.java)
            startActivity(orderintent)
        }
        doctorsCard.setOnClickListener{
            val intent = Intent(activity, FindDoctors::class.java)
            startActivity(intent)
        }
        return view
    }
}