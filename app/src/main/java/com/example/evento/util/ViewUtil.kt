package com.example.evento.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

fun TextInputLayout.onEditTextClicked(textInputLayout: TextInputLayout){
    textInputLayout.editText!!.addTextChangedListener(object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            textInputLayout.error = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}

fun String.isEmailValid(email:String):Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun String.isPasswordValid(password:String):Boolean{
    val pattern = Regex("""(?=.*[a-z])(?=.*\d).{6,}""")
    return pattern.matches(password)
}

fun String.randomID(name:String):String{
    return "$name${UUID.randomUUID()}"
}