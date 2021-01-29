package com.example.bookmanagerdb.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bookmanagerdb.database.BookStoreDao

class BookStoreViewModel (val database: BookStoreDao, application: Application): AndroidViewModel(application){

}