package com.example.bookmanagerdb.viewModel

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookmanagerdb.database.BookStore
import com.example.bookmanagerdb.database.BookStoreDao
import kotlinx.coroutines.launch
import timber.log.Timber

class BookStoreViewModel(val database: BookStoreDao, application: Application) :
    AndroidViewModel(application) {


    /**====*/
    //儲存Query database的bookList資料
    private var _myBookList = MutableLiveData<List<BookStore>?>()
    val myBookList: LiveData<List<BookStore>?>
        get() = _myBookList

    // 儲存click Recycler View的item目標資料
    private var _onClickPositionData = MutableLiveData<BookStore>()
    val onClickPositionData: LiveData<BookStore>
        get() = _onClickPositionData

    // Recycler View click item 資料 暫存於這邊作為"修改"的依據
    private var _readyToModifyData = MutableLiveData<BookStore>()
    val readyToModifyData: LiveData<BookStore>
        get() = _readyToModifyData

    // 完成資料庫動作要執行refreshUI 的flag
    private var _actionFinsihed = MutableLiveData<Boolean>()
    val actionFinished: LiveData<Boolean>
        get() = _actionFinsihed

    //資料庫寫入開關
    private val _dbWriteFlag = MutableLiveData<Boolean>()
    val dbWriteFlag: LiveData<Boolean>
        get() = _dbWriteFlag


    private val _delCache = MutableLiveData<Boolean>()
    val delCache: LiveData<Boolean>
        get() = _delCache

    //item select lock flag
    var selectLock: Boolean = false


    /** ===  */
    init {
        Timber.d("BookViewModel 初始化")
        requestBookList()
        _actionFinsihed.value = false
        _dbWriteFlag.value = false  // database writable flag
    }

    fun setSelectLock(){
        selectLock = true
    }

    fun requestBookList() {
        viewModelScope.launch {
            // select * from  my_bookstore_table
            getBookListFromDatabase()
        }
    }

    fun btnadd(newbook: List<BookStore>) {
        _dbWriteFlag.value = false  //關閉寫入flag
        viewModelScope.launch {
//            for (i in newbook) {
            addNewBookToDb(newbook)
//            }
        }
    }

    fun searchDataBase(data: BookStore) {
        viewModelScope.launch {
            searchBookListFromDataBase(data)
        }
    }

    fun btnDelete() {
        viewModelScope.launch {
            if (selectLock) {
                deleteBookListFromDataBase()
            }
        }
    }

    fun btnModify(data: BookStore) {
        if (selectLock) {
            val getModifyRes = modifyDataTemp()

            if (getModifyRes != null) {
                data.title.let {
                    getModifyRes.title = it

                }
                data.isbn.let {
                    getModifyRes.isbn = it
                }
            }

            viewModelScope.launch {
                if (getModifyRes != null) {
                    updateBookListToDataBase(getModifyRes)
                }
            }
        }
    }

//    fun refreshUI(){
//        requestBookList()
//    }

    fun tableClear() {
        viewModelScope.launch {
            dropTable()
        }
    }

    fun setDbInsertFlagOn() {
        _dbWriteFlag.value = true
    }

    fun setDelCacheFlagOff(){
        _delCache.value = false
    }

    /** RecyclerView Item click event */

    fun onRecyclerItemClick(cPosition: Int) {
        _myBookList.value?.get(cPosition)?.let {
            _onClickPositionData.value = it
        }
    }

    //初始化->獲取資料庫所有資料
    private suspend fun getBookListFromDatabase(): List<BookStore>? {
        viewModelScope.launch {
            _myBookList.value = database.getBookList()
            _actionFinsihed.value = false

        }
        selectLock = false
        return myBookList.value
    }

    private fun modifyDataTemp(): BookStore? {
            _readyToModifyData.value = onClickPositionData.value
            return _readyToModifyData.value
    }

    private suspend fun deleteBookListFromDataBase() {
        onClickPositionData.value?.let {
            database.delete(it)
            _delCache.value = true // 刪除點選資料後，開啟刪除快取的旗標
        }
        _actionFinsihed.value = true
        selectLock = false
    }

    private suspend fun addNewBookToDb(data: List<BookStore>) {
        database.insert(data)
        _actionFinsihed.value = true
    }

    private suspend fun updateBookListToDataBase(data: BookStore) {
        database.update(data)
        _actionFinsihed.value = true
        selectLock = false
    }

    private suspend fun searchBookListFromDataBase(data: BookStore) {
        val namestring = database.search(data.title, data.isbn)
        _myBookList.value = namestring
        Timber.d("return =, $namestring")
        selectLock = false
    }

    private suspend fun dropTable() {
        database.clear()
        _actionFinsihed.value = true
    }
}