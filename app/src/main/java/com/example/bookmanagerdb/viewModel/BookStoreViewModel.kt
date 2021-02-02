package com.example.bookmanagerdb.viewModel

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookmanagerdb.database.BookStore
import com.example.bookmanagerdb.database.BookStoreDao
import kotlinx.coroutines.launch

class BookStoreViewModel (val database: BookStoreDao, application: Application): AndroidViewModel(application){

    //儲存所有book資料
    private var _myBookList = MutableLiveData<List<BookStore>?>()
    val myBookList : LiveData<List<BookStore>?>
        get() = _myBookList


    init {
        initializeBookList()
    }

    private fun initializeBookList(){
        viewModelScope.launch {
            _myBookList.value = getBookListFromDatabase()
        }
    }

    private suspend fun getBookListFromDatabase(): List<BookStore>?{
        val booklist = database.getBookList()
        Log.d("我的資料庫列表= ", "$booklist")
        return booklist
    }

    fun showBooklist(){
        viewModelScope.launch {
            // select * from  my_bookstore_table
            initializeBookList()
        }
    }

    fun btnadd(name: Editable?, price: Editable?){
        viewModelScope.launch {
            val newbook = BookStore(name.toString(),price.toString())
            Log.d("data = ", "$newbook")
            addNewBookToDb(newbook)
        }
    }

    fun btnDelete(){
        viewModelScope.launch {
            deleteBookListFromDataBase()
        }
    }

    private suspend fun deleteBookListFromDataBase(){
        database.clear()
    }

    private suspend fun addNewBookToDb(data: BookStore){
        database.insert(data)
    }

}