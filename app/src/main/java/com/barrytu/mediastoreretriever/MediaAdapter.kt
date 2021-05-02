package com.barrytu.mediastoreretriever

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MediaAdapter(val mediaItemInterface: MediaItemInterface) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    val mediaItems = mutableListOf<MediaEntity>()

    fun setDataSet(dataSet : List<MediaEntity>) {
        mediaItems.clear()
        mediaItems.addAll(dataSet)
        notifyDataSetChanged()
    }

    inner class MediaViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private var mediaImageView : ImageView = itemView.findViewById(R.id.itemMediaImageView)

        init {
            itemView.setOnClickListener {
                mediaItemInterface.onItemClick(it.tag as Int)
            }
        }

        fun bind(mediaEntity : MediaEntity) {
            Glide.with(itemView.context).load(mediaEntity.uri).into(mediaImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false))
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(mediaItems[position])
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        return mediaItems.size
    }

    interface MediaItemInterface {
        fun onItemClick(position: Int)
    }

}