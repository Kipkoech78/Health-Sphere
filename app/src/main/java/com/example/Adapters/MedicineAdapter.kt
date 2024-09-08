package com.example.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthsphere.R
import com.example.models.Medicine

class MedicineAdapter(private val medicines: List<Medicine>, private val onBuyButtonClick: (Medicine) -> Unit ):
RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {
    class MedicineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.medHeading)
        val priceTextView: TextView = itemView.findViewById(R.id.price)
        //description View
        val buyBtn: TextView = itemView.findViewById(R.id.buyBtn)
        val imageView: ImageView = itemView.findViewById(R.id.title_image)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medicine_list,parent, false)
        return MedicineViewHolder(view)
    }

    override fun getItemCount() = medicines.size

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.nameTextView.text = medicine.drugName
        holder.buyBtn.setOnClickListener{
            onBuyButtonClick(medicine)
        }
        holder.priceTextView.text = medicine.price
        Glide.with(holder.itemView.context).load(medicine.drugImg)
            .placeholder(R.drawable.vials_solid)
            .into(holder.imageView)
    }
}