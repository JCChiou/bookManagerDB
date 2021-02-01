package com.example.bookmanagerdb.viewModel.BookStoreAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.R
import com.example.bookmanagerdb.database.BookStore
import org.w3c.dom.Text

class BookStoreAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = listOf<BookStore>()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val book_disp: TextView = itemView.findViewById(R.id.recycler_disp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recyclerview_disp,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


}



class TextItemViewHolder(val textview: TextView): RecyclerView.ViewHolder(textview)