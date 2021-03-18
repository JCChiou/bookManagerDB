package com.example.bookmanagerdb.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bookmanagerdb.viewModel.BookStoreViewModel

@Entity(tableName = "my_bookstore_table")
data class BookStore (

    @ColumnInfo(name = "book_name")
    var title: String,

    @ColumnInfo(name = "book_price")
    var isbn: String,

    @ColumnInfo(name = "image")
    var image: String?

    ){
    @PrimaryKey(autoGenerate = true)
    var _id: Int? = null
}


data class Mydata(
    val code: Int,
    val `data`: List<BookStore>,
    val status: String,
    val total: Int
)
