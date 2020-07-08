package com.example.evento.main


import android.app.Activity
import android.content.Intent
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
import com.example.evento.data.FirebaseViewModel
import com.example.evento.main.adapter.EventAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_third_tab.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */

class ThirdTabFragment : Fragment() {

    companion object{
        const val SEND_EVENT = "SEND_EVENT"
        const val EXTRA_CREATE_EVENT = "CREATE_EVENT"
        const val EXTRA_UPDATE_EVENT = "UPDATE_EVENT"

        const val EXTRA_MODEL_EVENT = "MODEL_EVENT"
    }

    private val eventViewModel : EventViewModel by sharedViewModel()
    private val firebaseViewModel: FirebaseViewModel by viewModel()
    private val eventAdapter : EventAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_add_event.setOnClickListener {
            val intent = Intent(activity,
                AddUpdateActivity::class.java)
            intent.putExtra(
                SEND_EVENT,
                EXTRA_CREATE_EVENT
            )
            startActivityForResult(intent,
                AddUpdateActivity.CREATE_EVENT_REQUEST_CODE)
        }
      //  eventViewModel= ViewModelProviders.of(this).get(EventViewModel::class.java)

        eventAdapter.setFragment(this)
       // eventViewModel.fetchEventsFromUser()
        eventViewModel.fetchEventsFromUser().observe(this,Observer{
            eventAdapter.submitList(it)
        })

        recycler_tab_three.apply {
            adapter = eventAdapter
          //  addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this.context)
        }
        eventAdapter.setOnClickListener(object :
            EventAdapter.OnEventClickListener{
            override fun onItemClicked(events: Events) {
                val intent = Intent(activity,
                    AddUpdateActivity::class.java)
                intent.putExtra(
                    SEND_EVENT,
                    EXTRA_UPDATE_EVENT
                )
                intent.putExtra(EXTRA_MODEL_EVENT,events)
                intent.putExtra("EXTRA_ID",events.id)
                startActivityForResult(intent, AddUpdateActivity.UPDATE_EVENT_REQUEST_CODE)
            }
        })

        eventAdapter.setOnBtnClickListener(object :
            EventAdapter.OnBtnClickListener{
            override fun onButtonClicked(events: Events) {
                CoroutineScope(Dispatchers.IO).launch {
                    eventViewModel.deleteEvent(events)
                    withContext(Dispatchers.Main){
                        Snackbar.make(view,"Event deleted",Snackbar.LENGTH_SHORT).show()
                        eventAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddUpdateActivity.CREATE_EVENT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val newEvent = data!!.getParcelableExtra<Events>(AddUpdateActivity.EXTRA_EVENT)

            CoroutineScope(Dispatchers.IO).launch {
                eventViewModel.insertEvent(newEvent)

                withContext(Dispatchers.Main){
                    loadingLayout.visibility = View.VISIBLE
                    button_add_event.visibility = View.GONE
                    val uploadResult =  firebaseViewModel.uploadEvents(newEvent)
                    if (!uploadResult){
                        Toast.makeText(activity,"Fail to create new event",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,"Successfully create new event",Toast.LENGTH_SHORT).show()
                    }
                    loadingLayout.visibility = View.GONE
                    button_add_event.visibility = View.VISIBLE

                    Log.d("ThirdFragment","${uploadResult}")
//                    firebaseViewModel.uploadUserEvents(newEvent)
//                    firebaseViewModel.uploadEvents(newEvent)

                }
            }
            return
        }

        else if (requestCode == AddUpdateActivity.UPDATE_EVENT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val newEvent = data!!.getParcelableExtra<Events>(AddUpdateActivity.EXTRA_EVENT)
            val id = data!!.getStringExtra("EXTRA_ID")

            if (id == "") {
                Toast.makeText(activity,"Error updating event",Toast.LENGTH_SHORT).show()
                return
            }
           // newEvent.id = id

            CoroutineScope(Dispatchers.IO).launch {
                eventViewModel.updateEvent(newEvent)
                withContext(Dispatchers.Main){

                    withContext(Dispatchers.Main){
                        loadingLayout.visibility = View.VISIBLE
                        button_add_event.visibility = View.GONE
                        val uploadResult =  firebaseViewModel.uploadEvents(newEvent)
                        if (!uploadResult){
                            Toast.makeText(activity,"Fail to update event",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(activity,"Successfully update event",Toast.LENGTH_SHORT).show()
                        }
                        loadingLayout.visibility = View.GONE
                        button_add_event.visibility = View.VISIBLE

                        Log.d("ThirdFragment","$uploadResult")
//                    firebaseViewModel.uploadUserEvents(newEvent)
//                    firebaseViewModel.uploadEvents(newEvent)

                    }
                }
            }
            return
        }
        else if (resultCode == Activity.RESULT_CANCELED){
            return
        }
        Toast.makeText(activity,"Failed to update event",Toast.LENGTH_SHORT).show()
       // Toast.makeText(activity,"Get Data :$data",Toast.LENGTH_SHORT).show()


    }


}
