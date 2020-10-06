package com.mozie.ui.tabMovies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mozie.R
import com.mozie.data.network.model.movies.FeaturedMovie
import com.squareup.picasso.Picasso

class RvAdapter(
    private val items: List<FeaturedMovie>,
    private var itemClickListener: ItemClickListener<FeaturedMovie>? = null
) : RecyclerView.Adapter<RvAdapter.ItemViewHolder>() {

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        fun init(featuredMovie: FeaturedMovie) {
            val url: String? = featuredMovie.posterUrl
            if (url != null && url.isNotBlank()) {
                Picasso.get().load(url).into(imageView)
            }
            imageView.setOnClickListener {
                itemClickListener?.onItemClicked(featuredMovie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movies, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.init(items[position])
    }

    override fun getItemCount() = items.size


}