package com.example.bookmanagerdb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookStoreDao {

    @Insert
    fun insert(bookName: BookStore)

    @Update
    fun update(bookName: BookStore)

    @Query("SELECT * from my_bookstore_table WHERE _id = :key")
    fun get(key: Int): BookStore?

    @Query("DELETE FROM my_bookstore_table")
    fun clear()


}