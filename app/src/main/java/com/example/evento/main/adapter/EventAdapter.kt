package com.example.evento.main.adapter

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.evento.R
import com.example.evento.data.model.Events
import com.example.evento.util.GlideApp
import com.example.evento.main.ThirdTabFragment
import kotlinx.android.synthetic.main.list_item_view.view.*

class EventAdapter() : ListAdapter<Events, EventAdapter.EventViewHolder>(
    DiffCallback
) {

    //private val fragment = fragment
    private lateinit var fragment: Fragment
    private var listener: OnEventClickListener? = null
    private var listener2: OnBtnClickListener? = null

    fun setFragment(fragment: Fragment) {
        this.fragment = fragment
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                listener?.let {
                    it.onItemClicked(getItem(adapterPosition))
                }
            }
            itemView.btn_favourite.setOnClickListener {
                listener2?.let {
                    it.onButtonClicked(getItem(adapterPosition))
                }
            }
        }

        //        fun bind(events: Events,listener: OnEventClickListener){
//            listener.onItemClicked(events,itemView)
//    println("On bind")
//        }
        val tv_title = itemView.tv_title
        val tv_date = itemView.tv_date
        val tv_description = itemView.tv_description
        val imageView_event = itemView.image_event
        val btn_favourite = itemView.btn_favourite
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        println("name = ${fragment::class.java.name}")
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = getItem(position)
        Log.d("currentEvent", "$currentEvent")
        holder.tv_title.text = currentEvent.title
        holder.tv_date.text = currentEvent.date
        holder.tv_description.text = currentEvent.description
        GlideApp.with(holder.itemView).load(currentEvent.imageUrl).centerCrop()
            .into(holder.imageView_event)

//        holder.bind(currentEvent,itemListener)
        if (fragment::class.java.name == ThirdTabFragment::class.java.name) {
            holder.btn_favourite.background =
                holder.itemView.resources.getDrawable(R.drawable.ic_delete_24dp)
            return
        }

        if (currentEvent.favourites) {
            holder.btn_favourite.backgroundTintList =
                ColorStateList.valueOf(holder.itemView.context.resources.getColor(R.color.colorMainDark2))
        } else holder.btn_favourite.backgroundTintList =
            ColorStateList.valueOf(holder.itemView.context.resources.getColor(R.color.white))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Events>() {

            override fun areItemsTheSame(oldItem: Events, newItem: Events): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Events, newItem: Events): Boolean {
                return oldItem.title == newItem.title && oldItem.description == newItem.description
                        && oldItem.favourites == newItem.favourites && oldItem.imageUrl == newItem.imageUrl
            }
        }
    }

    interface OnEventClickListener {
        fun onItemClicked(events: Events)
    }

    interface OnBtnClickListener {
        fun onButtonClicked(events: Events)
    }

    fun setOnClickListener(listener: OnEventClickListener) {
        this.listener = listener
    }

    //
    fun setOnBtnClickListener(listener: OnBtnClickListener) {
        this.listener2 = listener
    }
}