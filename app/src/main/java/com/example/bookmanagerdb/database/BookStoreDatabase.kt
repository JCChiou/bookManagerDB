package com.example.bookmanagerdb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookStore::class], version = 1, exportSchema = false)
abstract class BookStoreDatabase : RoomDatabase(){

    abstract val bookStoreDao: BookStoreDao

    companion object {
        private var INSTANCE : BookStoreDatabase? = null

        fun getInstance(context: Context): BookStoreDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BookStoreDatabase::class.java,
                        "my_bookstore_table")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}