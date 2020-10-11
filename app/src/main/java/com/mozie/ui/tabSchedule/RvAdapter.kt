package com.mozie.ui.tabSchedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mozie.R
import com.mozie.ui.tabSchedule.models.ScheduleMovie
import com.mozie.ui.tabSchedule.models.ScheduleScreening
import com.squareup.picasso.Picasso

class RvAdapter(screenings: Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>) :
    RecyclerView.Adapter<RvAdapter.ItemViewHolder>() {
    private val mScreenings: List<Pair<ScheduleMovie, Map<String, List<ScheduleScreening>>>> =
        screenings.toList()

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        fun init(pair: Pair<ScheduleMovie, Map<String, List<ScheduleScreening>>>) {
            val movie = pair.first
            val url: String? = movie.posterUrl
            if (url != null && url.isNotBlank()) {
                Picasso.get().load(url).into(imageView)
            }
            itemView.findViewById<TextView>(R.id.title).text = movie.title
            itemView.findViewById<TextView>(R.id.genre).text = movie.genre
            itemView.findViewById<TextView>(R.id.length).text =
                itemView.resources.getString(R.string.length_text, movie.length)

            // TODO dates
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvAdapter.ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie_schedule, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvAdapter.ItemViewHolder, position: Int) {
        holder.init(mScreenings[position])
    }

    override fun getItemCount(): Int = mScreenings.size
}