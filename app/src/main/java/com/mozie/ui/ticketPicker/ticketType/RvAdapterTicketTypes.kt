package com.mozie.ui.ticketPicker.ticketType

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mozie.R
import com.mozie.data.network.model.tickets.TicketType

class RvAdapterTicketTypes(private val ticketTypes: List<TicketType>) :
    RecyclerView.Adapter<RvAdapterTicketTypes.TicketTypeHolder>() {

    private val chosenTickets: MutableMap<TicketType, Int> = mutableMapOf()

    inner class TicketTypeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val resources: Resources = itemView.resources
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinus)
        private val tvCounter: TextView = itemView.findViewById(R.id.tvCounter)
        private val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlus)

        fun init(item: TicketType) {
            tvName.text = item.name // todo
            tvPrice.text = resources.getString(R.string.text_price_ft, item.price)
            tvCounter.text = getTicketCount(item).toString()
            btnMinus.setOnClickListener {
                handleMinusClick(item)
            }
            btnPlus.setOnClickListener {
                handlePlusClick(item)
            }
        }

        private fun getTicketCount(ticketType: TicketType): Int {
            return chosenTickets[ticketType] ?: 0
        }

        private fun handleMinusClick(ticketType: TicketType) {
            val count = getTicketCount(ticketType)
            if (count > 0) {
                val newValue = count - 1
                chosenTickets[ticketType] = newValue
            }
            tvCounter.text = getTicketCount(ticketType).toString()
        }

        private fun handlePlusClick(ticketType: TicketType) {
            val count = getTicketCount(ticketType)
            if (count < 10) {
                val newValue = count + 1
                chosenTickets[ticketType] = newValue
            }
            tvCounter.text = getTicketCount(ticketType).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketTypeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket_type_row, parent, false)
        return TicketTypeHolder(view)
    }

    override fun onBindViewHolder(holder: TicketTypeHolder, position: Int) {
        holder.init(ticketTypes[position])
    }

    override fun getItemCount(): Int = ticketTypes.size
}