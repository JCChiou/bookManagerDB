package com.example.bookmanagerdb.util

import android.graphics.Bitmap
import android.util.LruCache
import kotlin.properties.Delegates

class ImageLoader() {
    // use memory cache
    private var memoryCache: LruCache<String, Bitmap>
    private var cacheSize by Delegates.notNull<Int>()

    init {
        val maxMemoty = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        cacheSize = maxMemoty / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun addBitmapToMemoryCache(key: String, bitmap: Bitmap){
        if (getBitmapFromMemCache(key) == null){
            memoryCache.put(key,bitmap)
        }
    }

    fun getBitmapFromMemCache(key: String): Bitmap?{
        return memoryCache.get(key)
    }
}