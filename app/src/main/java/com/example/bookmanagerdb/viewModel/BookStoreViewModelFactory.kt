package com.example.bookmanagerdb.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookmanagerdb.database.BookStoreDao
import java.lang.IllegalArgumentException


class BookStoreViewModelFactory(private val dataSource: BookStoreDao,
                                private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookStoreViewModel::class.java)){
            return BookStoreViewModel(dataSource,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}