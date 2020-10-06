package com.mozie.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mozie.R
import com.mozie.data.network.model.movies.Actor
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ActorsRvAdapter(private val actors: List<Actor>) :
    RecyclerView.Adapter<ActorsRvAdapter.ActorViewHolder>() {
    inner class ActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun init(actor: Actor) {
            val iv = itemView.findViewById<CircleImageView>(R.id.image)
            if (!actor.pictueUrl.isNullOrBlank()) {
                Picasso.get().load(actor.pictueUrl).fit().centerCrop().into(iv)
            }

            val tv = itemView.findViewById<TextView>(R.id.text)
            tv.text = actor.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actors, parent, false)
        return ActorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.init(actors[position])
    }

    override fun getItemCount() = actors.size
}