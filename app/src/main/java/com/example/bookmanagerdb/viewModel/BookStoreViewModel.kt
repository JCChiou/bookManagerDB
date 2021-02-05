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

    //儲存Query database的bookList資料
    private var _myBookList = MutableLiveData<List<BookStore>?>()
    val myBookList : LiveData<List<BookStore>?>
        get() = _myBookList

    //Recycler view click event flag
    private var _isClick = MutableLiveData<Boolean>()
    val isClick : LiveData<Boolean>
        get() = _isClick

    // 儲存click Recycler View的item目標資料
    private var _onClickPositionData = MutableLiveData<BookStore>()
    val onClickPositionData: LiveData<BookStore>
        get() = _onClickPositionData

    // Recycler View click item 資料 暫存於這邊作為"修改"的依據
    private var _readyToModifyData = MutableLiveData<BookStore>()
    val readyToModifyData : LiveData<BookStore>
        get() = _readyToModifyData

    // 完成資料庫動作要執行refreshUI 的flag
    private var _actionFinsihed = MutableLiveData<Boolean>()
    val actionFinished: LiveData<Boolean>
        get() = _actionFinsihed

    init {
        initializeBookList()
        _isClick.value = false
        _actionFinsihed.value = false
    }

    fun showBooklist(){
        viewModelScope.launch {
            // select * from  my_bookstore_table
            initializeBookList()
        }
    }

    fun btnadd(newbook: BookStore){
        viewModelScope.launch {
            addNewBookToDb(newbook)
        }
    }

    fun searchDataBase(data: BookStore){
        viewModelScope.launch {
            searchBookListFromDataBase(data)
        }
    }

    fun btnDelete(){
        viewModelScope.launch {
            deleteBookListFromDataBase()
        }
    }

    fun btnModify(data: BookStore){
        val getModifyRes = modifyDataTemp()
        if (getModifyRes != null) {
            data.bookName.let {
                getModifyRes.bookName = it
            }
            data.bookPrice.let {
                getModifyRes.bookPrice = it
            }
        }

        viewModelScope.launch {
            if (getModifyRes != null) {
                updateBookListToDataBase(getModifyRes)
            }
        }
    }

    fun refreshUI(){
        showBooklist()
    }

    /** RecyclerView Item click event */
    fun onRecyclerItemClick(cPosition: Int){
        _isClick.value = true
        _myBookList.value?.get(cPosition)?.let {
            _onClickPositionData.value = it
        }
        _isClick.value = false
    }

    fun dipClickItem(){
//        _isClick.value = false
    }

    fun dispClickItemFinish(){
        _isClick.value = false
    }

    //初始化->獲取資料庫所有資料
    private fun initializeBookList(){
        viewModelScope.launch {
            _myBookList.value = getBookListFromDatabase()
            _actionFinsihed.value = false
        }
    }

    private suspend fun getBookListFromDatabase(): List<BookStore>?{
        val booklist = database.getBookList()
        Log.d("我的資料庫列表= ", "$booklist")
        return booklist
    }

    private fun modifyDataTemp(): BookStore? {
        _readyToModifyData.value = onClickPositionData.value
        return _readyToModifyData.value
    }

    private suspend fun deleteBookListFromDataBase(){
        onClickPositionData.value?.let {
            database.delete(it)
        }
        _actionFinsihed.value = true
    }

    private suspend fun addNewBookToDb(data: BookStore){
        database.insert(data)
        _actionFinsihed.value = true
    }

    private suspend fun updateBookListToDataBase(data :BookStore){
        database.update(data)
        _actionFinsihed.value = true
    }

    private suspend fun searchBookListFromDataBase(data: BookStore){
        val namestring = database.search(data.bookName , data.bookPrice)
        _myBookList.value = namestring
        Log.d("return =", "$namestring")
    }
}