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
import com.example.assignment_ocean.DetailActivity
import com.example.assignment_ocean.R
import com.example.assignment_ocean.data.DataClass

import com.squareup.picasso.Picasso

class ImagesHomeAdapter(private val context: Context, private var imageHomeCatList: List<DataClass>) : RecyclerView.Adapter<MyImageHomeCatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImageHomeCatViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyImageHomeCatViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyImageHomeCatViewHolder, position: Int) {
        Glide.with(context).load(imageHomeCatList[position].dataImage).into(holder.recImage)
        holder.recTitle.text = imageHomeCatList[position].dataTitle
        holder.recDesc.text = imageHomeCatList[position].dataDesc
        holder.recNum.text = imageHomeCatList[position].dataNum
        holder.recPriority.text = imageHomeCatList[position].dataPriority
        holder.recRange.text = imageHomeCatList[position].dataRange
        holder.recSize.text = imageHomeCatList[position].dataSize

        // for details in RV
        holder.recCard.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)

            intent.putExtra("Image", imageHomeCatList[holder.adapterPosition].dataImage)
            intent.putExtra("Description", imageHomeCatList[holder.adapterPosition].dataDesc)
            intent.putExtra("Title", imageHomeCatList[holder.adapterPosition].dataTitle)
            intent.putExtra("Priority", imageHomeCatList[holder.adapterPosition].dataPriority)
            intent.putExtra("Range", imageHomeCatList[holder.adapterPosition].dataRange)
            intent.putExtra("Size", imageHomeCatList[holder.adapterPosition].dataSize)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return imageHomeCatList.size
    }
    fun searchDataList(searchList: List<DataClass>) {
        imageHomeCatList = searchList
        notifyDataSetChanged()
    }

}

class MyImageHomeCatViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    var recImage: ImageView
    var recTitle: TextView
    var recDesc: TextView
    var recNum: TextView
    var recPriority: TextView
    var recRange: TextView
    var recSize: TextView
    var recCard: CardView

    init {
        recImage = itemView.findViewById(R.id.recImage)
        recTitle = itemView.findViewById(R.id.recTitle)
        recDesc = itemView.findViewById(R.id.recDesc)
        recNum = itemView.findViewById(R.id.recNum)
        recPriority = itemView.findViewById(R.id.recPriority)
        recRange = itemView.findViewById(R.id.recRange)
        recSize = itemView.findViewById(R.id.recSize)
        recCard = itemView.findViewById(R.id.recCard)
    }
}
