package com.example.bookmanagerdb.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookmanagerdb.database.BookStore
import com.example.bookmanagerdb.database.BookStoreDao
import kotlinx.coroutines.launch

class BookStoreViewModel (val database: BookStoreDao, application: Application): AndroidViewModel(application){

    //儲存所有book資料
    private var myBookList = MutableLiveData<BookStore?>()

    init {
        initializeBookList()
    }

    private fun initializeBookList(){
        viewModelScope.launch {
            myBookList.value = getBookListFromDatabase()
        }
    }

    private suspend fun getBookListFromDatabase(): BookStore?{
        val booklist = database.getBookList()
        return booklist
    }

    private fun showBooklist(){
        // select * from  my_bookstore_table

    }
    fun btnadd(){
        viewModelScope.launch {
            val newbook = BookStore("ffff", 300)
            addNewBook(newbook)
        }

    }
    suspend fun addNewBook(data: BookStore){
        database.insert(data)

    }
}