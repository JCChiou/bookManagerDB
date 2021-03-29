package com.example.bookmanagerdb.viewModel.BookStoreAdapter

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.bookmanagerdb.R

//最大Fetch 筆數
const val COUNT = 100

class FetchCountAdapter(private val context: Context, myView: View){
    val item = mutableListOf<Int>()

    //給使用者選擇要獲取幾筆資料
    fun setAdapter(): ArrayAdapter<Int>{
        for (i in 1..COUNT){
            item.add(i)
        }
        return ArrayAdapter(context,R.layout.myspinner, item)
    }
}