package com.example.bookmanagerdb

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.database.BookStore
import com.example.bookmanagerdb.database.BookStoreDatabase
import com.example.bookmanagerdb.databinding.ActivityMainBinding
import com.example.bookmanagerdb.viewModel.BookStoreAdapter.BookStoreAdapter
import com.example.bookmanagerdb.viewModel.BookStoreViewModel
import com.example.bookmanagerdb.viewModel.BookStoreViewModelFactory
import java.security.acl.Owner

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var myRecyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** get database Instance & ViewModel instance */
        val application = requireNotNull(this.application)
        val dataSource = BookStoreDatabase.getInstance(application).bookStoreDao
        val viewModelFactory = BookStoreViewModelFactory(dataSource,application)
        val bookStoreViewModel =
            ViewModelProvider(this, viewModelFactory).get(BookStoreViewModel::class.java)

        binding.bookStoreViewModle = bookStoreViewModel

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
            val getEditInputData = inPutData()
            if(getEditInputData.bookName.isNotEmpty() && getEditInputData.bookPrice.isNotEmpty()){
                bookStoreViewModel.btnadd(getEditInputData)
                Toast.makeText(this,"你尚未輸入資料", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this,"click", Toast.LENGTH_SHORT).show()
            clearInput()
        }

        // click "查詢"
        binding.btnQuery.setOnClickListener {
            //action to do
            val getInputdata = inPutData()
            if (getInputdata.bookName.isEmpty() && getInputdata.bookPrice.isEmpty()){
                // if input is empty
                bookStoreViewModel.showBooklist()
            } else{
                // if input not empty
                bookStoreViewModel.searchDataBase(getInputdata)
            }
            clearInput()
        }

        // click "刪除"
        binding.btnDel.setOnClickListener {
            bookStoreViewModel.btnDelete()
            clearInput()
        }

        // click "修改"
        binding.btnModify.setOnClickListener {
            val modifyString = inPutData()
            bookStoreViewModel.btnModify(modifyString)

            clearInput()
        }

        //click RecyclerView Item
        binding.recdisp.addOnItemTouchListener(BookStoreAdapter.SingleItemClickListener(binding.recdisp, object :
                BookStoreAdapter.SingleItemClickListener.OnItemClickListener{
                    override fun onItemClick(view: View?, position: Int) {
                        bookStoreViewModel.onRecyclerItemClick(position)
//                        Toast.makeText(this@MainActivity,"${bookStoreViewModel.editTextBookNameContent.value}", Toast.LENGTH_SHORT ).show()
//                        Toast.makeText(this@MainActivity,"點擊事件觸發", Toast.LENGTH_SHORT ).show()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
//                        Toast.makeText(this@MainActivity,"長按擊事件觸發", Toast.LENGTH_SHORT ).show()
                    }

                }))
        /** ===================== */
        /** 觀察者 區域 */
        // 撈取資料庫內容
        bookStoreViewModel.myBookList.observe(this, Observer { newList ->
            newList?.let {
                //更新給adapter的data
                Log.d("data=", "$it")
                adapter.data = it
            }
        })


        // action之後 變更flag 執行UI refresh
        bookStoreViewModel.actionFinished.observe(this, Observer { newFinish ->
            if (newFinish){
                bookStoreViewModel.refreshUI()
            }
        })


        bookStoreViewModel.onClickPositionData.observe(this, Observer {
            binding.invalidateAll()
        })
    }

    // get EditText data
    private fun inPutData():BookStore{
        val getBookName: Editable? = binding.bookNameInput.text
        val getBookPrice: Editable? = binding.bookPriceInput.text
        return BookStore(getBookName.toString(),getBookPrice.toString())
    }

    private fun clearInput(){
        binding.bookNameInput.text = null
        binding.bookPriceInput.text = null
    }

}