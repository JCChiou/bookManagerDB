package com.example.bookmanagerdb.viewModel.BookStoreAdapter

import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.R
import com.example.bookmanagerdb.database.BookStore

class BookStoreAdapter: RecyclerView.Adapter<BookStoreAdapter.ViewHolder>() {
    init {
        Log.d("recycler view初始化","初始化了")
    }

    var data = listOf<BookStore>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val book_disp: TextView = itemView.findViewById(R.id.recycler_disp_name)
        val price_disp : TextView = itemView.findViewById(R.id.recycler_disp_Price)

        fun bindData(data:BookStore){
            book_disp.text = data.bookName
            price_disp.text = data.bookPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recyclerview_disp,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bindData(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class SingleItemClickListener(): RecyclerView.SimpleOnItemTouchListener(){
        private var clickListener: OnItemClickListener? = null
        private var gestureDetector: GestureDetectorCompat? = null

        constructor(recyclerview: RecyclerView, listener: OnItemClickListener): this(){
            this.clickListener = listener
            gestureDetector = GestureDetectorCompat(recyclerview.context, object : GestureDetector.SimpleOnGestureListener(){
                override fun onSingleTapUp(e: MotionEvent?): Boolean {
//                    return super.onSingleTapUp(e)
                    Log.d("手勢觸發事件", "TapUp")
                    return true
                }

                override fun onLongPress(e: MotionEvent?) {
                    val childView : View? = e?.x?.let { recyclerview.findChildViewUnder(it, e.y) }
                    if(childView != null && clickListener != null){
                        clickListener!!.onItemLongClick(childView, recyclerview.getChildAdapterPosition(childView))
                    }
                    Log.d("手勢觸發事件", "LongPress")
                    super.onLongPress(e)
                }

                override fun onDown(e: MotionEvent?): Boolean {
                    //每次按下銀幕都會出現
                    Log.d("手勢觸發事件", "Down")
                    return super.onDown(e)
                }
            })
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            Log.d("手勢觸發事件", "onTouchEvent")
            super.onTouchEvent(rv, e)
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val childView : View? = e.x.let { rv.findChildViewUnder(it, e.y) }
            if(childView != null && clickListener != null && gestureDetector?.onTouchEvent(e)!!){
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