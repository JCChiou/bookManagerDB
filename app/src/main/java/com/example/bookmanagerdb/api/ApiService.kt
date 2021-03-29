package com.example.bookmanagerdb.api

import android.graphics.Bitmap
import android.text.TextUtils
import com.example.bookmanagerdb.database.Mydata
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.InputStream


private const val BASE_URL = "https://fakerapi.it/api/v1/"
private const val IMG_URL = "http://placeimg.com/480/640/any"

// setting HttpLoggingInterceptor
val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    this.level = HttpLoggingInterceptor.Level.BODY
}


private val okHttpClient = OkHttpClient.Builder().apply {
//    addInterceptor(MyInterceptor())
    addInterceptor(httpLoggingInterceptor)
}
    .build()

//val gson = GsonBuilder()
//    .setLenient()
//    .create()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

interface IApiService {
    @GET("books?")
    suspend fun getAPIData(@Query("_quantity") count: String): Mydata

//    @Headers("baseUrl:two")
    @GET("http://placeimg.com/480/640/any") //如果方法內部有完整的scheme,會替換掉原本的baseURL達到多個baseURL切換
    suspend fun getImg(): ResponseBody
}

object BookApi {

    val retrofitService: IApiService by lazy { retrofit.create(IApiService::class.java) }
}
//  要第二組ＵＲＬ有get方法。。。
//class MyInterceptor(): Interceptor {
//    val hashMap = HashMap<String, String>()
//    init {
//        hashMap.put("one", "https://fakerapi.it/api/v1/")
//        hashMap.put("two", "http://placeimg.com/480/640/any")
//    }

//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        Timber.d("攔截攔截攔截攔截攔截攔截攔截攔截攔截攔截")
//        val request: Request = chain.request()
//        val builder = request.newBuilder()
//        val list = request.headers("baseUrl")
//        Timber.d("headers = ${list}")
//        if (list.size > 0) {
//            builder.removeHeader("baseUrl")
//            val key: String = list.get(0)
//            if (!TextUtils.isEmpty(key) && hashMap.containsKey(key)){
//                val newBaseUrl = hashMap.get(key)?.toHttpUrl()
//                Timber.d("newBaseUrl = ${newBaseUrl}")
//                val oldBaseUrl = request.url
//                var newFullUrl :HttpUrl? = null
//                if (newBaseUrl != null) {
//                    newFullUrl = oldBaseUrl.newBuilder()
//                        .scheme(newBaseUrl.scheme)
//                        .host(newBaseUrl.host)
//                        .port(newBaseUrl.port)
//                        .build()
//                }
//                Timber.d("new URL = $newFullUrl")
//                val newRequest = newFullUrl?.let { builder.url(it).build() }
//                return chain.proceed(newRequest!!)
//            }
//
//        }
//        return chain.proceed(request)
//    }
//}