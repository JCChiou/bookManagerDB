package com.example.bookmanagerdb.viewModel

import android.app.Application
import android.text.Editable
import android.util.Log
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
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

    private var _isClick = MutableLiveData<Boolean>()
    val isClick : LiveData<Boolean>
        get() = _isClick

    private var _onClickPositionData = MutableLiveData<BookStore>()
    val onClickPositionData: LiveData<BookStore>
        get() = _onClickPositionData


    init {
        initializeBookList()
        _isClick.value = false

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

    fun btnadd(name: String?, price: String?){
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

    /** RecyclerView Item click event */
    fun onRecyclerItemClick(cPosition: Int){
        _isClick.value = true
        _myBookList.value?.get(cPosition)?.let {
            _onClickPositionData.value = it
        }
    }

    fun dipClickItem(){
//        _isClick.value = false
    }

    fun dispClickItemFinish(){
        _isClick.value = false
    }


    private suspend fun deleteBookListFromDataBase(){
//        database.clear()
        onClickPositionData.value?.let {
            database.delete(it)
        }
    }

    private suspend fun addNewBookToDb(data: BookStore){
        database.insert(data)
    }

}