package com.example.bookmanagerdb

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
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

//            bookStoreViewModel.btnadd()
            bookStoreViewModel.btnadd(getEditInputData)
            Toast.makeText(this,"click", Toast.LENGTH_SHORT).show()
            binding.bookNameInput.text = null
            binding.bookPriceInput.text = null
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
            binding.bookNameInput.text = null
            binding.bookPriceInput.text = null
        }

        // click "刪除"
        binding.btnDel.setOnClickListener {
            bookStoreViewModel.btnDelete()
            binding.bookNameInput.text = null
            binding.bookPriceInput.text = null
        }

        // click "修改"
        binding.btnModify.setOnClickListener {
            val modifyString = inPutData()
            bookStoreViewModel.btnModify(modifyString)

            binding.bookNameInput.text = null
            binding.bookPriceInput.text = null
        }

        //click RecyclerView Item
        binding.recdisp.addOnItemTouchListener(BookStoreAdapter.SingleItemClickListener(binding.recdisp, object :
                BookStoreAdapter.SingleItemClickListener.OnItemClickListener{
                    override fun onItemClick(view: View?, position: Int) {
                        bookStoreViewModel.onRecyclerItemClick(position)
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

        // 當點選recyclerView item時, 在被點選item的data更新到EditText前, Enable turn off
//        bookStoreViewModel.isClick.observe(this, Observer { newClickEvent ->
//            if(newClickEvent) {
//                binding.bookNameInput.isFocusable = false
//                binding.bookPriceInput.isFocusable = false
//            }else {
////                binding.bookNameInput.isFocusable = true
//                binding.bookNameInput.apply {
//                    isFocusable = true
//                }
////                binding.bookPriceInput.isFocusable = true
//                binding.bookPriceInput.apply {
//                    isFocusable = true
//                }
//            }
//        })

        // when RecyclerView item點擊後 更新資料於EditText
        bookStoreViewModel.onClickPositionData.observe(this, Observer { newPosData ->
            Log.d("click data = ", "$newPosData")
            binding.bookNameInput.setText(newPosData.bookName)
            binding.bookPriceInput.setText(newPosData.bookPrice)
//            bookStoreViewModel.dispClickItemFinish()

        })
        // when RecyclerView item點擊後 更新資料於EditText
        bookStoreViewModel.displayBookEditTextVContent.observe(this, Observer {
            binding.bookNameInput.setText(it.bookName)
            binding.bookPriceInput.setText(it.bookPrice)

        })

        // action之後 變更flag 執行UI refresh
        bookStoreViewModel.actionFinished.observe(this, Observer { newFinish ->
            if (newFinish){
                bookStoreViewModel.refreshUI()
            }
        })

        // editText Name Observer
        bookStoreViewModel.editTextBookNameContent.observe(this,Observer{
            Toast.makeText(this@MainActivity,it,Toast.LENGTH_SHORT).show()
            Log.d("觀察edit", "Okokokok")
        })

        // editText Price Observer
        bookStoreViewModel.editTextBookPriceContent.observe(this, Observer{
            Toast.makeText(this@MainActivity,it,Toast.LENGTH_SHORT).show()
        })
    }

    // get EditText data
    private fun inPutData():BookStore{
        val getBookName: Editable? = binding.bookNameInput.text
        val getBookPrice: Editable? = binding.bookPriceInput.text
        return BookStore(getBookName.toString(),getBookPrice.toString())
    }
}