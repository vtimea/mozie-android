package com.mozie.ui.tabTickets

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.mozie.R
import com.mozie.data.network.model.userTickets.TicketInfo
import com.mozie.data.network.model.userTickets.UserTicket
import com.squareup.picasso.Picasso
import org.joda.time.DateTime

class AdapterOpenTickets(private var mTicketGroup: UserTicket, var mFragment: OpenTicketFragment) :
    PagerAdapter() {
    private val mTickets: List<TicketInfo> = mTicketGroup.tickets!!

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mTickets.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = mFragment.layoutInflater
        val view = layoutInflater.inflate(R.layout.view_ticket, container, false)
        val item = mTickets[position]
        view.findViewById<TextView>(R.id.item_title).text = mTicketGroup.movieTitle
        view.findViewById<TextView>(R.id.cinema_name).text = mTicketGroup.cinemaName
        view.findViewById<TextView>(R.id.row).text = item.col.toString()
        view.findViewById<TextView>(R.id.col).text = item.row.toString()
        view.findViewById<TextView>(R.id.room).text = item.room.toString()

        val tvDate = view.findViewById<TextView>(R.id.date_info)
        val startTime = DateTime.parse(mTicketGroup.movieStartTime)
        tvDate.text = container.resources.getString(
            R.string.summary_screening_date_format,
            startTime.year,
            startTime.monthOfYear,
            startTime.dayOfMonth,
            startTime.hourOfDay,
            startTime.minuteOfHour
        )

        val tvPrice = view.findViewById<TextView>(R.id.type_info)
        tvPrice.text = mFragment.resources.getString(R.string.price_huf, item.price)

        Picasso.get().load(mTicketGroup.moviePosterUrl)
            .into(view.findViewById<ImageView>(R.id.header_image))

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}