package com.example.bookmanagerdb.viewModel.BookStoreAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.R
import com.example.bookmanagerdb.database.BookStore

class BookStoreAdapter: RecyclerView.Adapter<BookStoreAdapter.ViewHolder>() {
    init {
        Log.d("recycler view初始化","初始化了")
    }

    var data = listOf<BookStore>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val book_disp: TextView = itemView.findViewById(R.id.recycler_disp_name)
        val price_disp : TextView = itemView.findViewById(R.id.recycler_disp_Price)

        fun bindData(data:BookStore){
            book_disp.text = data.bookName
            price_disp.text = data.bookPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recyclerview_disp,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bindData(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}