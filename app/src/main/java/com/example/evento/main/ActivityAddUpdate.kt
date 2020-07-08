package com.example.evento.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.evento.R
import com.example.evento.data.model.Events
import com.example.evento.util.onEditTextClicked
import com.example.evento.util.randomID
import kotlinx.android.synthetic.main.activity_addupdate_event.*
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.get
import java.text.SimpleDateFormat
import java.util.*

class AddUpdateActivity : AppCompatActivity() {

    companion object{
        const val CREATE_EVENT_REQUEST_CODE = 1
        const val UPDATE_EVENT_REQUEST_CODE = 2
        const val EXTRA_EVENT = "EVENT"

        const val CHOOSE_IMAGE_CODE = 9
    }

    private lateinit var event:Events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addupdate_event)

        val requestEvent = intent.getStringExtra(ThirdTabFragment.SEND_EVENT)
        if (requestEvent == ThirdTabFragment.EXTRA_CREATE_EVENT) {
            initToolbar("Create Event")
            event = get()
        }
        else{
            initToolbar("Update Event")
            val extraEvent = intent.getParcelableExtra<Events>(ThirdTabFragment.EXTRA_MODEL_EVENT)
           // val id = intent.getIntExtra("EXTRA_ID",-1)
            event = extraEvent
           // event.id = id
            setupData()
        }


        button_image.setOnClickListener {
            openImageChooser()
        }

        initDateChooser()


        button_submit.setOnClickListener {
            addEvent()
        }

        editText_title.onEditTextClicked(editText_title)
        editText_date.onEditTextClicked(editText_date)
        editText_desc.onEditTextClicked(editText_desc)
    }

    private fun initToolbar(toolbarTitle: String){
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)


        toolbar.title = toolbarTitle

        toolbar.apply {
            setTitleTextColor(resources.getColor(R.color.white))
            navigationIcon = getDrawable(R.drawable.ic_back_arrow_white)
        }
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() }
    }

    private fun initDateChooser(){
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.US)

        val datePicker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            print("DATE $year / $month / $dayOfMonth")
            calendar.set(year,month,dayOfMonth)
            editText_date.editText!!.setText(simpleDateFormat.format(calendar.timeInMillis))
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate= calendar.timeInMillis - 1000
        editText_date.editText!!.setOnClickListener {
            datePicker.show()
        }
    }
    private fun openImageChooser() {
        var intentOpenImageChooser = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            intentOpenImageChooser = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intentOpenImageChooser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intentOpenImageChooser.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        } else{
            intentOpenImageChooser.action = (Intent.ACTION_GET_CONTENT)
        }
        intentOpenImageChooser.putExtra(Intent.EXTRA_LOCAL_ONLY,true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentOpenImageChooser.type = "image/*"

        startActivityForResult(Intent.createChooser(intentOpenImageChooser,"Select image"),
            CHOOSE_IMAGE_CODE
        )
    }

    private fun setupData(){
        editText_title.editText!!.setText(event.title)
        editText_date.editText!!.setText(event.date)
        editText_desc.editText!!.setText(event.description)
        Glide.with(this).load(Uri.parse(event.imageUrl)).into(image_event)
    }

    @SuppressLint("NewApi")
    private fun addEvent() {

        val eventTitle = editText_title.editText!!.text.toString()
        val eventDate = editText_date.editText!!.text.toString()
        val eventDesc = editText_desc.editText!!.text.toString()

        if (eventTitle.isNullOrEmpty()) {
            editText_title.error = "Field is required"
            return
        }
        if (eventDate.isNullOrEmpty()) {
            editText_date.error = "Field is required"
            return
        }
        if (eventDesc.isNullOrEmpty()) {
            editText_desc.error = "Field is required"
            return
        }
        if (event.imageUrl == ""){
           Toast.makeText(this,"Please select image",Toast.LENGTH_SHORT).show()
            return
        }

        event.title = eventTitle
        if (event.id == ""){
            event.id = event.id.randomID("event")
        }
        event.date = eventDate
        event.description = eventDesc

        val intentToThirdFragment = Intent()
        intentToThirdFragment.putExtra(EXTRA_EVENT,event)
        if (event.id != "") intentToThirdFragment.putExtra("EXTRA_ID",event.id)
        setResult(Activity.RESULT_OK,intentToThirdFragment)
        finish()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE_CODE){
            if (resultCode != Activity.RESULT_OK)
            {
                Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show()
                return
            }
            val imageUri = data!!.data

            Toast.makeText(this,"Image selected}",Toast.LENGTH_SHORT).show()

            image_event.setImageURI(imageUri)


            event.imageUrl = imageUri.toString()
        }


    }

}
