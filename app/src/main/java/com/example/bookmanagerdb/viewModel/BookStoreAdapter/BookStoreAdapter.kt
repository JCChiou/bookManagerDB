package com.example.bookmanagerdb.viewModel.BookStoreAdapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.*
import android.widget.ImageView
import android.widget.TextView

import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.R
import com.example.bookmanagerdb.database.BookStore
import com.example.bookmanagerdb.util.ImageLoader
import kotlinx.coroutines.*

import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

import kotlin.collections.HashMap

class BookStoreAdapter : RecyclerView.Adapter<BookStoreAdapter.ViewHolder>() {
    init {
    }

    val mLoader = ImageLoader()

    //存放img在recycler view的pos
    var mLoadingMap = HashMap<String, Bitmap>()

    var data = listOf<BookStore>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val book_disp: TextView = itemView.findViewById(R.id.recycler_disp_name)
        val price_disp: TextView = itemView.findViewById(R.id.recycler_disp_Price)
        val img_disp: ImageView = itemView.findViewById(R.id.api_Image)
        fun bindData(data: BookStore, pos: Int) {
            Timber.d("綁定data")
            book_disp.text = data.title
            price_disp.text = data.isbn
//            img_disp.setImageBitmap(mLoader.getBitmapFromMemCache(pos.toString()))
        }

        fun bindImg(Bitmap: Bitmap) {
            Timber.d("綁定IMG")
            img_disp.setImageBitmap(Bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recyclerview_disp, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /**
         * 在這邊處理圖片下載，用position對應bitmap的hashMap存放
         *
         * */
        Timber.d("呼叫onBindViewHolder")
        val key: String = "$position"
        val b: Bitmap? = mLoader.getBitmapFromMemCache(key)
        if (b == null) { //緩存沒有資料
            Timber.d("key = null，從網路解析圖片")
            //從網路下載
            getBitmap(data[position].image.toString(), position)
        } else { //緩存有資料
            Timber.d("有資料，從快取拿圖片")
            holder.bindImg(b)
        }
        val item = data[position]
        holder.bindData(item, position)
    }

    private fun getBitmap(url: String, pos: Int) {
        var result: Bitmap?
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val job: Deferred<Bitmap> = async(Dispatchers.IO) {
                    val myurl = URL(url)
                    val connection = myurl.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val ins = connection.inputStream
                    Timber.d(" 開始decode")
                    BitmapFactory.decodeStream(ins)
                }
                result = job.await()
                Timber.d("等到結果了，result = $result")
                mLoader.addBitmapToMemoryCache(pos.toString(), result!!)

            } catch (e: Exception) {
                Timber.d("error = ${e.message}")
            }
            notifyItemChanged(pos) //update UI in main Thread
        }
    }

    class SingleItemClickListener() : RecyclerView.SimpleOnItemTouchListener() {
        private var clickListener: OnItemClickListener? = null
        private var gestureDetector: GestureDetectorCompat? = null

        constructor(recyclerview: RecyclerView, listener: OnItemClickListener) : this() {
            this.clickListener = listener
            gestureDetector = GestureDetectorCompat(
                recyclerview.context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
//                    return super.onSingleTapUp(e)
                        Timber.d("手勢觸發事件,TapUp")
                        return true
                    }

                    override fun onLongPress(e: MotionEvent?) {
                        val childView: View? =
                            e?.x?.let { recyclerview.findChildViewUnder(it, e.y) }
                        if (childView != null && clickListener != null) {
                            clickListener!!.onItemLongClick(
                                childView,
                                recyclerview.getChildAdapterPosition(childView)
                            )
                        }
                        Timber.d("手勢觸發事件, LongPress")
                        super.onLongPress(e)
                    }

                    override fun onDown(e: MotionEvent?): Boolean {
                        //每次按下銀幕都會出現
                        Timber.d("手勢觸發事件, Down")
                        return super.onDown(e)
                    }
                })
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            Timber.d("手勢觸發事件 onTouchEvent")
            super.onTouchEvent(rv, e)
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val childView: View? = e.x.let { rv.findChildViewUnder(it, e.y) }
            if (childView != null && clickListener != null && gestureDetector?.onTouchEvent(e)!!) {
                clickListener!!.onItemClick(childView, rv.getChildAdapterPosition(childView))
            }
            return super.onInterceptTouchEvent(rv, e)
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            super.onRequestDisallowInterceptTouchEvent(disallowIntercept)
        }

        interface OnItemClickListener {
            fun onItemClick(view: View?, position: Int)
            fun onItemLongClick(view: View?, position: Int)
        }
    }
}

