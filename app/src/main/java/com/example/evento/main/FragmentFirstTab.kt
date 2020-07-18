package com.example.evento.main


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.evento.R
import com.example.evento.data.model.Events
import com.example.evento.data.viewmodel.EventViewModel
import com.example.evento.main.adapter.EventAdapter
import kotlinx.android.synthetic.main.fragment_first_tab.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class FirstTabFragment : Fragment() {
    // TODO: Rename and change types of parameters
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val madapter : EventAdapter by inject()
    private val eventViewModel : EventViewModel by sharedViewModel()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


       // eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        madapter.setFragment(this)
//        eventViewModel.getAllEvents().observe(this,Observer<List<Events>>{
//            madapter.submitList(it)
//        })

        recycler_tab_one.apply {
            adapter = madapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }

        eventViewModel.fetchEvents().observe(this, Observer {
            Log.d("fetchevents2","$it")
            madapter.submitList(it)
        })

        madapter.setOnClickListener(object :
            EventAdapter.OnEventClickListener{
            override fun onItemClicked(events: Events) {
               // Toast.makeText(activity,"TITLE : ${events.title} || DATE : ${events.date}",Toast.LENGTH_SHORT).show()
                    val fm = childFragmentManager
                    val fragment =
                        FragmentViewEvent.newInstance(
                            events
                        )
                    fragment.show(fm,"Dialog")
            }

        })

        madapter.setOnBtnClickListener(object :
            EventAdapter.OnBtnClickListener{
            override fun onButtonClicked(events: Events) {
                events.favourites = if (events.favourites) false else true

               CoroutineScope(Dispatchers.IO).launch {
                   eventViewModel.updateEvent(events)
                   withContext(Dispatchers.Main){
                       if (events.favourites) Toast.makeText(activity,"Save event as favourite",Toast.LENGTH_SHORT).show()
                       else Toast.makeText(activity,"Remove event from favourite",Toast.LENGTH_SHORT).show()
                   }
               }
                madapter.notifyDataSetChanged()
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_tab, container, false)
    }

}
