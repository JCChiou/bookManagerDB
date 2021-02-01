package com.example.bookmanagerdb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_bookstore_table")
data class BookStore (
//    @PrimaryKey(autoGenerate = true)
//    var _id: Int = 0,

    @ColumnInfo(name = "book_name")
    var bookName: String,

    @ColumnInfo(name = "book_price")
    var bookPrice: Int

    ){
    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id")
    var _id: Int? = null
}
