package com.example.evento.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.evento.R
import com.example.evento.data.model.Events
import com.example.evento.main.adapter.EventAdapter
import kotlinx.android.synthetic.main.fragment_second_tab.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */

class SecondTabFragment : Fragment() {

    private val eventAdapter: EventAdapter by inject()
    private val eventViewModel : EventViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
       // val eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventAdapter.setFragment(this)

        eventViewModel.getFavouritesEvent().observe(this, Observer {
            eventAdapter.submitList(it)
        })

        recycler_tab_two.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        eventAdapter.setOnBtnClickListener(object :
            EventAdapter.OnBtnClickListener{
            override fun onButtonClicked(events: Events) {
                events.favourites = false
                CoroutineScope(Dispatchers.IO).launch {
                    eventViewModel.updateEvent(events)
                        withContext(Dispatchers.Main){
                            Toast.makeText(activity,"Remove event from favourite",Toast.LENGTH_SHORT).show()
                        }
                }
                eventAdapter.notifyDataSetChanged()
            }

        })
    }

}
