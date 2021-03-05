package com.example.bookmanagerdb.api

import androidx.lifecycle.LiveData
import com.example.bookmanagerdb.database.Mydata
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL ="https://fakerapi.it/api/v1/"


// setting HttpLoggingInterceptor
val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    this.level = HttpLoggingInterceptor.Level.BODY

}

private val okHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(httpLoggingInterceptor)
}
    .build()


private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()


interface IApiService {
    @GET("books?")
    suspend fun getAPIData (@Query("_quantity") count: String): Mydata
}

object BookApi{
    val retrofitService: IApiService by lazy { retrofit.create(IApiService::class.java) }
}