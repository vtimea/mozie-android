package com.mozie.ui.tabTickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mozie.R
import com.mozie.data.network.model.userTickets.UserTicket
import com.squareup.picasso.Picasso
import org.joda.time.DateTime

class AdapterTicketsList(
    private val mItems: Map<Int, UserTicket>
) : RecyclerView.Adapter<AdapterTicketsList.ItemViewHolder>() {
    private val mKeys: List<Int> = mItems.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mKeys.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.init(mItems[mKeys[position]]!!)
    }

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var mImage: ImageView = itemView.findViewById(R.id.header_image)
        private var mTitle: TextView = itemView.findViewById(R.id.item_title)
        private var mDate: TextView = itemView.findViewById(R.id.movie_date)
        private var mCinema: TextView = itemView.findViewById(R.id.cinema)
        private var mCount: TextView = itemView.findViewById(R.id.item_count)

        fun init(item: UserTicket) {
            Picasso.get().load(item.moviePosterUrl).into(mImage)
            mTitle.text = item.movieTitle

            val startTime = DateTime.parse(item.movieStartTime)
            mDate.text = itemView.resources.getString(
                R.string.summary_screening_date_format,
                startTime.year,
                startTime.monthOfYear,
                startTime.dayOfMonth,
                startTime.hourOfDay,
                startTime.minuteOfHour
            )
            mCinema.text = item.cinemaName
            mCount.text = itemView.resources.getString(R.string.quantity, item.tickets?.size)
            itemView.setOnClickListener {
                // TODO
            }
        }
    }
}