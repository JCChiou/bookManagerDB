package com.example.bookmanagerdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.database.BookStoreDatabase
import com.example.bookmanagerdb.databinding.ActivityMainBinding
import com.example.bookmanagerdb.viewModel.BookStoreAdapter.BookStoreAdapter
import com.example.bookmanagerdb.viewModel.BookStoreViewModel
import com.example.bookmanagerdb.viewModel.BookStoreViewModelFactory
import java.security.acl.Owner

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var myRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** get database Instance & ViewModel instance */
        val application = requireNotNull(this.application)
        val dataSource = BookStoreDatabase.getInstance(application).bookStoreDao
        val viewModelFactory = BookStoreViewModelFactory(dataSource,application)
        val bookStoreViwlModel =
            ViewModelProvider(this, viewModelFactory).get(BookStoreViewModel::class.java)

        //get EditText value
        val getBookName: Editable? = binding.bookNameInput.text
        val getBookPrice: Editable? = binding.bookPriceInput.text

        /** define RecyclerView setting */
        val adapter = BookStoreAdapter()
        myRecyclerView = binding.recdisp
        val linearLayoutManager = LinearLayoutManager(this)
        myRecyclerView.layoutManager = linearLayoutManager
        myRecyclerView.adapter = adapter
        /** ================= */

        /** click event area */
        // click "新增"
        binding.btnAdd.setOnClickListener {
            //action to do
            Log.d("get data = ","$getBookName, $getBookPrice")
            bookStoreViwlModel.btnadd(getBookName,getBookPrice)
            Toast.makeText(this,"click", Toast.LENGTH_SHORT).show()
        }


        // click "查詢"
        binding.btnQuery.setOnClickListener {
            //action to do
            bookStoreViwlModel.showBooklist()
        }

        binding.btnDel.setOnClickListener {
            bookStoreViwlModel.btnDelete()
        }
        /** ===================== */

        /** 觀察者 區域 */
        bookStoreViwlModel.myBookList.observe(this, Observer { newList ->
            newList?.let {
                //更新給adapter的data
                adapter.data = it
            }
        })

    }
}