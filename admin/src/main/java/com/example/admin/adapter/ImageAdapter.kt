package com.example.admin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.UpdateImageActivity
import com.example.admin.data.ImageClass

class ImageAdapter (private val context: Context, private var imageList: List<ImageClass>) : RecyclerView.Adapter<ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return ImageViewHolder(view)
    }



    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context).load(imageList[position].dataImageG).into(holder.recImageView)
        holder.recImageName.text = imageList[position].dataImageName

        holder.recGallery.setOnClickListener {
            val intent = Intent(context, UpdateImageActivity::class.java)

            intent.putExtra("Image", imageList[holder.adapterPosition].dataImageG)
            intent.putExtra("Name", imageList[holder.adapterPosition].dataImageName)


            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}



class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recImageView: ImageView
    var recImageName: TextView
    var recGallery: CardView

    init {
        recImageView = itemView.findViewById(R.id.recImageView)
        recImageName = itemView.findViewById(R.id.recImageName)
        recGallery = itemView.findViewById(R.id.recGallery)
    }
}