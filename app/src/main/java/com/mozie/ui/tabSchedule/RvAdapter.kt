package com.mozie.ui.tabSchedule

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.mozie.R
import com.mozie.ui.tabSchedule.models.ScheduleMovie
import com.mozie.ui.tabSchedule.models.ScheduleScreening
import com.squareup.picasso.Picasso
import java.util.*


class RvAdapter(screenings: Map<ScheduleMovie, Map<String, List<ScheduleScreening>>>) :
    RecyclerView.Adapter<RvAdapter.ItemViewHolder>() {
    private val mScreenings: List<Pair<ScheduleMovie, Map<String, List<ScheduleScreening>>>> =
        screenings.toList()

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val context: Context = itemView.context
        private val resources: Resources = itemView.resources
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val timesRootLayout: LinearLayout = itemView.findViewById(R.id.layoutDates)

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

            val inflater = LayoutInflater.from(context)
            val movieTypes = pair.second.toList()
            timesRootLayout.removeAllViews()
            for (value in movieTypes) {
                val inflatedLayout: View = inflater.inflate(R.layout.item_schedule_row, null)
                val tvType: TextView = inflatedLayout.findViewById(R.id.type)
                val tvVoice: TextView = inflatedLayout.findViewById(R.id.voice)
                val flexboxLayout: FlexboxLayout = inflatedLayout.findViewById(R.id.gridTimes)

                tvType.text = value.second[0].type?.toUpperCase(Locale.getDefault()) ?: ""
                tvVoice.text = resources.getString(R.string.screening_voice, value.second[0].voice)
                    .toUpperCase(
                        Locale.getDefault()
                    )
                for (s in value.second) {
                    val tv = getTimeTextView(s, inflater)
                    flexboxLayout.addView(tv)
                }
                timesRootLayout.addView(inflatedLayout)
            }
        }

        private fun getTimeTextView(screening: ScheduleScreening, inflater: LayoutInflater): View {
            val layout: View = inflater.inflate(R.layout.view_hour_minute, null)
            layout.findViewById<TextView>(R.id.tvHourMinute).text = resources.getString(
                R.string.hour_minute_format,
                screening.startTime?.hourOfDay,
                screening.startTime?.minuteOfHour
            )
            return layout
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RvAdapter.ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie_schedule, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvAdapter.ItemViewHolder, position: Int) {
        holder.init(mScreenings[position])
    }

    override fun getItemCount(): Int = mScreenings.size
}