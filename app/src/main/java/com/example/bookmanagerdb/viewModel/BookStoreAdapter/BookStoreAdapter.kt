package com.example.bookmanagerdb.viewModel.BookStoreAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.database.BookStore

class BookStoreAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = listOf<BookStore>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


}