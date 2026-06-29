package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapters.AdapterClass
import com.example.healthsphere.R
import com.example.healthsphere.WorkoutItemModel

class WorkoutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapterClass: AdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutArrayList: ArrayList<WorkoutItemModel>

    lateinit var ImageId : Array<Int>
    lateinit var heading : Array<String>
    lateinit var workouts : Array<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datainitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(false)
        adapterClass = AdapterClass(workoutArrayList)
        recyclerView.adapter = adapterClass
    }

    private fun datainitialize(){
        workoutArrayList = arrayListOf<WorkoutItemModel>()
        ImageId = arrayOf(
            R.drawable.workout1,
            R.drawable.workout2,
            R.drawable.workout1,
            R.drawable.workout2,
            R.drawable.workout1,
            R.drawable.workout2,
            R.drawable.workout1

        )
        heading = arrayOf(
            getString(R.string.workout1),
            getString(R.string.workout2),
            getString(R.string.workout3),
            getString(R.string.workout4),
            getString(R.string.workout5),
            getString(R.string.workout6),
            getString(R.string.workout7),
            getString(R.string.workout8)
            )
        for(i in ImageId.indices){
            val workouts = WorkoutItemModel(ImageId[i], heading[i])
            workoutArrayList.add(workouts)
        }
    }
}