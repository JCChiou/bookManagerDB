package com.example.bookmanagerdb.util

import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.databinding.InverseMethod

object Converter {

    @InverseMethod("editableToString")
    @JvmStatic
    fun strToEditable(data: String):Editable?{
        return SpannableStringBuilder(data)
    }


    @JvmStatic
    fun editableToString(data: Editable?):String{
        return data.toString()
    }
}