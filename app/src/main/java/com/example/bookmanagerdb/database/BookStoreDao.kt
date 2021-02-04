package com.example.bookmanagerdb.database

import androidx.room.*

@Dao
interface BookStoreDao {

    @Insert
    suspend fun insert(item: BookStore)

    @Update
    suspend fun update(item: BookStore) : Int

    @Query("SELECT * from my_bookstore_table WHERE _id = :key")
    suspend fun get(key: Int): BookStore?

    @Query("DELETE FROM my_bookstore_table")
    suspend fun clear()

    @Query("SELECT * FROM my_bookstore_table")
    suspend fun getBookList() : List<BookStore>?

    @Delete
    suspend fun delete(item: BookStore)

    @Query("UPDATE my_bookstore_table SET book_name = :name , book_price = :price WHERE _id = :id")
    suspend fun modify(name:String, price:String,id:Int)

}