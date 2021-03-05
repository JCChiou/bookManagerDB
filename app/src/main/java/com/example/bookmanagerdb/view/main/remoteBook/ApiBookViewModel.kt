package com.example.bookmanagerdb.view.main.remoteBook


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookmanagerdb.api.BookApi
import com.example.bookmanagerdb.database.Mydata
import kotlinx.coroutines.*
import timber.log.Timber

class ApiBookViewModel: ViewModel() {
    private val _response = MutableLiveData<Mydata>()
    val response: LiveData<Mydata>
        get() = _response

    private val _getApiCount = MutableLiveData<String>()
    val getApiCount: LiveData<String>
        get() = _getApiCount

    private val _dataFlag = MutableLiveData<Boolean>()
    val dataFlag: LiveData<Boolean>
        get() = _dataFlag

    init {
        _dataFlag.value = false  //資料庫寫入開關 =false(不可寫入)
        Timber.d("ApiViewModel初始化了")
    }

    //接收spinner 選取的筆數
    fun sendFetchDataCount(count: Int){
        setFetchDataCount(count)
    }
    //將筆數設定給Api網址當作request參數
    private fun setFetchDataCount(newCount: Int){
        _getApiCount.value = newCount.toString()
    }


    fun setDataFlagOff(){
        _dataFlag.value = false
    }

    fun sendApiRequest(){
        getBookApiResponse()
    }
    private fun getBookApiResponse(){
        val count = getApiCount.value
        viewModelScope.launch {
            try {
                val data = count?.let { BookApi.retrofitService.getAPIData(it) }
                _response.value = data
                _dataFlag.value = true //送出Api request flag
                Timber.d("try block execute")
            }catch (e: Exception){
                Timber.d("錯誤訊息=  ${e.message}")
            }
        }
    }

}