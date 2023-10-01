package com.example.assignment_ocean.adapter

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
import com.example.assignment_ocean.MainActivity
import com.example.assignment_ocean.R
import com.example.assignment_ocean.data.ImageClass

class ImagesGalleryAdapter(private val context: Context, private var imagesGalleryAdapterList: List<ImageClass>) : RecyclerView.Adapter<ImageGalleryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return ImageGalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageGalleryViewHolder, position: Int) {
        Glide.with(context).load(imagesGalleryAdapterList[position].dataImageG).into(holder.recImageView)
        holder.recImageName.text = imagesGalleryAdapterList[position].dataImageName

        holder.recGallery.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)



            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return imagesGalleryAdapterList.size
    }

    fun searchDataList(searchList: List<ImageClass>) {
        imagesGalleryAdapterList = searchList
        notifyDataSetChanged()
    }
}

class ImageGalleryViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    var recImageView: ImageView
    var recImageName: TextView
    var recGallery: CardView

    init {
        recImageView = itemView.findViewById(R.id.recImageView)
        recImageName = itemView.findViewById(R.id.recImageName)
        recGallery = itemView.findViewById(R.id.recGallery)
    }
}

