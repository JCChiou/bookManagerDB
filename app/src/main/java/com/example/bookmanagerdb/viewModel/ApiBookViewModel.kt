package com.example.bookmanagerdb.viewModel


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookmanagerdb.api.BookApi
import com.example.bookmanagerdb.database.BookStore
import com.example.bookmanagerdb.database.Mydata
import com.example.bookmanagerdb.util.ImageLoader
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import timber.log.Timber

class ApiBookViewModel : ViewModel() {
    private val _response = MutableLiveData<Mydata>()
    val response: LiveData<Mydata>
        get() = _response

    private val _getApiCount = MutableLiveData<String>()
    val getApiCount: LiveData<String>
        get() = _getApiCount

    private val _dataFlag = MutableLiveData<Boolean>()
    val dataFlag: LiveData<Boolean>
        get() = _dataFlag

    //存放下載圖片
    private val _imgResult = MutableLiveData<HashMap<String, Bitmap>?>()
    val imgResult: LiveData<HashMap<String, Bitmap>?>
        get() = _imgResult

    init {
        _dataFlag.value = false  //資料庫寫入開關 =false(不可寫入)
        Timber.d("ApiViewModel初始化了")
    }

    //實例化 圖片快取
    val mLoader = ImageLoader()

    //接收spinner 選取的筆數
    fun sendFetchDataCount(count: Int) {
        setFetchDataCount(count)
    }

    //將筆數設定給Api網址當作request參數
    private fun setFetchDataCount(newCount: Int) {
        _getApiCount.value = newCount.toString()
    }

    fun setDataFlagOff() {
        _dataFlag.value = false
    }

    fun sendApiRequest() {
        getBookApiResponse()
    }

    //發ＡＰＩ請求
    private fun getBookApiResponse() {
        val count = getApiCount.value
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val data = count?.let {
                    async(Dispatchers.IO) {
                        BookApi.retrofitService.getAPIData(it)
                    }
                }
                _response.value = data?.await()
                _dataFlag.value = true //送出Api request flag 準備寫入ＤＢ
                Timber.d("try block execute")
            } catch (e: Exception) {
                Timber.d("錯誤訊息=  ${e}")
            }
        }
    }

    //把圖片API請求寫在這邊
    fun getImgFromUrl(source: List<BookStore>) {
        val newSource = source
        val result = HashMap<String, Bitmap>()
        for (i in newSource.indices) {
            val key: String = newSource[i]._id.toString()//for cache's key
            if (mLoader.getBitmapFromMemCache(key) == null) {
                //如果快取沒有對應_id的Bitmap就從網路下載
                viewModelScope.launch {
                    try {
                        val job: Deferred<ResponseBody> = async(Dispatchers.IO) {
                            BookApi.retrofitService.getImg()
                        }
                        job.await().let {
                            val bmp = bmpDecode(it) //decode to Bitmap
                            mLoader.addBitmapToMemoryCache(key, bmp)  // write in cache
                            result[key] = bmp  //put into hashMap
                            _imgResult.value = result  //set value to LiveData
                        }
                    } catch (e: Exception) {
                        Timber.d("發生錯誤，${e.message}")
                    }
                }

            } else {
                //快取有圖片的話 直接拿
                val bmp = mLoader.getBitmapFromMemCache(key)
                bmp?.let { result[key] = it }
                _imgResult.value = result
            }
        }
    }

    /**
     * remove cache
     */
    fun removeCache(key: String) {
        Timber.d("刪除$key 的快取資料")
        mLoader.removeBitmapFromMenCache(key)
    }

    /**
     * ResponseBody decode to Bitmap
     */
    private fun bmpDecode(source: ResponseBody): Bitmap {
        val option = BitmapFactory.Options().apply {
            inJustDecodeBounds = true  //設為true，不將圖片載入記憶體，但可以獲取圖片的寬高pixel
        }
        val data: ByteArray = source.bytes()
        BitmapFactory.decodeByteArray(data, 0, data.size, option)
        option.inSampleSize = calculateInSampleSize(option, 200, 200)
        option.inJustDecodeBounds = false //將圖片進行縮放之後，讀進記憶體內
        return BitmapFactory.decodeByteArray(data, 0, data.size, option)
    }

    /**
     * 根據輸入目標長寬計算出取樣大小
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        targetWidth: Int,
        targetHeigth: Int
    ): Int {
        val heigth: Int = options.outHeight  //640
        val width: Int = options.outWidth   // 480
        var inSampleSize = 1
        if (heigth > targetHeigth || width > targetWidth) {
            val halfHeigth = heigth / 2
            val halfWidth = width / 2
            while ((halfHeigth / inSampleSize) >= targetHeigth && (halfWidth / inSampleSize) >= targetWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}