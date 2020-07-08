package com.example.evento.main


import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

import com.example.evento.R
import com.example.evento.data.model.Events
import kotlinx.android.synthetic.main.fragment_fragment_view_event.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentViewEvent.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentViewEvent : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: Events? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_fragment_view_event, container, false)
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window
        val size = Point()

        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)

        val width = size.x
        val height = size.y

        window.setLayout(width ,(height*0.85).toInt())
        window.setGravity(Gravity.TOP)

        dialog!!.setCanceledOnTouchOutside(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.container_toolbar)
        toolbar.apply {
            title = param1!!.title
            setTitleTextColor(resources.getColor(R.color.white))
        }
        Glide.with(view).load(param1!!.imageUrl).into(image_event)
        tv_title.text = param1!!.title
        tv_date.text = param1!!.date
        tv_description.text = param1!!.description
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentViewEvent.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(event: Events) =
            FragmentViewEvent().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, event)

                }
            }
    }
}
