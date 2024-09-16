package com.example.drugs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.healthsphere.BazeActivity
import com.example.healthsphere.MainActivity
import com.example.healthsphere.R
import com.example.models.Medicine
import com.example.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class medicineCategoryFragment : Fragment() {

    private lateinit var backbtn: ImageView
    private lateinit var cardMeasles: CardView
    private lateinit var cardHeartProblems : CardView
    private lateinit var CardAnti_tbs: CardView
    private lateinit var cardAntiemetics: CardView
    private lateinit var cardPainKillers: CardView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.drug_categories_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        cardMeasles = view.findViewById(R.id.cardMeasles)
        cardPainKillers = view.findViewById(R.id.cardOverTheCounter)
        cardAntiemetics = view.findViewById(R.id.cardMalaria)
        CardAnti_tbs = view.findViewById(R.id.cardCommonCold)
        cardHeartProblems = view.findViewById(R.id.cardheartProblems)


        backbtn = view.findViewById(R.id.backbtn)
        backbtn.setOnClickListener{
            val backIntent = Intent(activity, MainActivity::class.java)
            startActivity(backIntent)
        }

        cardHeartProblems.setOnClickListener {
            fetchMedicine("Antibiotics")
        }

        CardAnti_tbs.setOnClickListener {
            fetchMedicine("Anti Tbs")
        }

        cardAntiemetics.setOnClickListener {
            fetchMedicine("Antiemetics")
        }

        cardPainKillers.setOnClickListener {
            fetchMedicine("Pain Killers")
        }

        cardMeasles.setOnClickListener {
            fetchMedicine("Measles")
        }
    }

    private fun fetchMedicine(category: String) {
        (activity as? BazeActivity)?.showProgressDialog(getString(R.string.loading))

        db.collection(Constants.DRUG)
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                (activity as? BazeActivity)?.hideProgressDialog()

                val medicineDet = result.map { document ->
                    val medicine = document.toObject(Medicine::class.java)
                    arrayOf(
                        medicine.drugImg,
                        medicine.drugName,
                        medicine.price,
                        medicine.description
                    )
                }.toTypedArray()

                val intent = Intent(activity, MedicineActivity::class.java)
                intent.putExtra("title", category)
                intent.putExtra("medicine", medicineDet)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                (activity as? BazeActivity)?.hideProgressDialog()
            }
    }
}
