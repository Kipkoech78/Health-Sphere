package com.example.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDelegate
import com.example.drugs.DrugCategoriesActivity
import com.example.healthsphere.FindDoctors
import com.example.healthsphere.LabTestActivity
import com.example.drugs.MedicineActivity
import com.example.drugs.MedicineDetailActivity
import com.example.drugs.MedicineHomeMainActivity
import com.example.healthsphere.OrderDetActivity
import com.example.healthsphere.R

class HomeFragment : Fragment() {
    private lateinit var doctorsCard: RelativeLayout
    private lateinit var labtest: RelativeLayout
    private lateinit var orderDetails: RelativeLayout
    private lateinit var medicine: RelativeLayout
    private lateinit var dummymed: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        doctorsCard = view.findViewById(R.id.doctorsCard)
        orderDetails = view.findViewById(R.id.orderDetails)
        labtest = view.findViewById(R.id.labtest)
        medicine = view.findViewById(R.id.medicine)
       // dummymed = view.findViewById(R.id.dummymed)

        labtest.setOnClickListener {
            val intentLab = Intent(activity, LabTestActivity::class.java)
            startActivity(intentLab)
        }
        medicine.setOnClickListener {
           //display medicine
            val intentMed = Intent(activity, MedicineHomeMainActivity::class.java)
            startActivity(intentMed)
        }
        orderDetails.setOnClickListener {
            val orderintent = Intent(activity, OrderDetActivity::class.java)
            startActivity(orderintent)
        }
        doctorsCard.setOnClickListener{
            val intent = Intent(activity, FindDoctors::class.java)
            startActivity(intent)
        }

//        dummymed.setOnClickListener{
//            val intent = Intent(activity, MedicineDetailActivity::class.java)
//            startActivity(intent)
//        }
        return view
    }
}