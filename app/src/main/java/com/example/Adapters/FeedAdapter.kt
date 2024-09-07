package com.example.recyclerview
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feeds.FeedDetailActivity
import com.example.models.Feeds
import com.example.healthsphere.R
import com.google.android.material.imageview.ShapeableImageView

 class FeedAdapterClass(private val dataList1: ArrayList<Feeds>):RecyclerView.Adapter<FeedAdapterClass.ViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.feed_list_item, parent, false)
        return ViewHolderClass(itemView)
    }
    override fun getItemCount(): Int {
        return dataList1.size
    }
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList1[position]
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .placeholder(R.drawable.baseline_feed_24) // Optional: add a placeholder image
            .into(holder.rvImage)

        holder.rvTitle.text = currentItem.heading
        holder.rvFeedDesc.text = currentItem.feedDesc.take(40) +"...Read More"

        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, FeedDetailActivity::class.java)
            intent.putExtra("heading", currentItem.heading)
            intent.putExtra("description", currentItem.feedDesc)
            context.startActivity(intent)
        }
    }

//    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
//        val currentItem = dataList1[position]
//        holder.rvImage.setImageResource(currentItem.titleImage)
//        holder.rvTitle.text = currentItem.heading
//        holder.rvFeedDesc.text = currentItem.feedDesc
//    }
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView) {
        val rvImage: ShapeableImageView = itemView.findViewById(R.id.title_image)
        val rvTitle: TextView = itemView.findViewById(R.id.feedHeading)
        val rvFeedDesc: TextView = itemView.findViewById(R.id.feedDesc)
    }
}