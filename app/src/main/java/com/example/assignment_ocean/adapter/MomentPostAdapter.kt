package com.example.assignment_ocean

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment_ocean.databinding.MomentItemBinding
import com.example.assignment_ocean.models.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


interface MomentPostAdapterListener {
    fun onUpdatePostClicked(post: Post)
    fun onDeletePostClicked(post: Post)
}


class MomentPostAdapter(private val listener: MomentPostAdapterListener) : ListAdapter<Post, MomentPostAdapter.MomentViewHolder>(MomentDiffCallback()) {


    inner class MomentViewHolder(private val binding: MomentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.moreImage.setOnClickListener { showPopupMenu() }
        }

        fun bind(momentData: Post) {
            binding.captionText.text = momentData.caption
            // Load the image using Glide
            Glide.with(itemView.context)
                .load(momentData.photo) // Assuming `photo` is a valid URL
                .placeholder(R.drawable.image_placeholder)
                .into(binding.postImage)

            // Use the `timestamp` field directly as a Long
            val timestamp: Long = momentData.timestamp as? Long ?: 0L

            // Pass the converted timestamp to formatTimestamp
            binding.timestampText.text = formatTimestamp(timestamp)
        }

        private fun showPopupMenu() {
            val popupMenu = PopupMenu(itemView.context, binding.moreImage)
            popupMenu.inflate(R.menu.moment_item_menu) // Create a menu resource file

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_update_post -> {
                        val post = getItem(adapterPosition)
                        listener.onUpdatePostClicked(post) // Pass the selected post to the listener
                        true
                    }
                    R.id.menu_delete_post -> {
                        val post = getItem(adapterPosition)
                        listener.onDeletePostClicked(post)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MomentItemBinding.inflate(inflater, parent, false)
        return MomentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DateUtils {
        fun formatTimestamp(timestamp: Long): String {
            val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")

            // Create a SimpleDateFormat instance with the Malaysia time zone and 12-hour time format
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
            sdf.timeZone = malaysiaTimeZone

            // Convert the timestamp to Malaysia time zone
            val malaysiaTime = sdf.format(Date(timestamp))

            return malaysiaTime
        }
    }

    private class MomentDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }



}
