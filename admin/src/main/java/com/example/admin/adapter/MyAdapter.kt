package com.example.admin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admin.R
import com.example.admin.UpdateActivity
import com.example.admin.data.DataClass

//to get data from firebase
class MyAdapter(private val context: Context, private var dataList: List<DataClass>) : RecyclerView.Adapter<MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].dataImage).into(holder.recImage)
        holder.recTitle.text = dataList[position].dataTitle
        holder.recDesc.text = dataList[position].dataDesc
        holder.recNum.text = dataList[position].dataNum
        holder.recPriority.text = dataList[position].dataPriority
        holder.recRange.text = dataList[position].dataRange
        holder.recSize.text = dataList[position].dataSize

// for details in RV
        holder.recCard.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)

            intent.putExtra("Image", dataList[holder.adapterPosition].dataImage)
            intent.putExtra("Description", dataList[holder.adapterPosition].dataDesc)
            intent.putExtra("Title", dataList[holder.adapterPosition].dataTitle)
            intent.putExtra("Priority", dataList[holder.adapterPosition].dataPriority)
            intent.putExtra("Range", dataList[holder.adapterPosition].dataRange)
            intent.putExtra("Size", dataList[holder.adapterPosition].dataSize)

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    fun searchDataList(searchList: List<DataClass>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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