package com.example.bookmanagerdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** get database Instance & ViewModel instance */
        val application = requireNotNull(this.application)
        val dataSource = BookStoreDatabase.getInstance(application).bookStoreDao
        val viewModelFactory = BookStoreViewModelFactory(dataSource,application)
        val bookStoreViewModel =
            ViewModelProvider(this, viewModelFactory).get(BookStoreViewModel::class.java)




        /** define RecyclerView setting */
        val adapter = BookStoreAdapter()
        myRecyclerView = binding.recdisp
        val linearLayoutManager = LinearLayoutManager(this)
        myRecyclerView.layoutManager = linearLayoutManager
        myRecyclerView.adapter = adapter
        /** ================= */

        var templist = listOf<BookStore>()

        /** click event area */
        // click "新增"
        binding.btnAdd.setOnClickListener {
            //action to do
            val getContext = getInputData()
            bookStoreViewModel.btnadd(getContext[0], getContext[1])
            Toast.makeText(this,"click", Toast.LENGTH_SHORT).show()
        }


        // click "查詢"
        binding.btnQuery.setOnClickListener {
            //action to do
            bookStoreViewModel.showBooklist()
        }

        // click "刪除"
        binding.btnDel.setOnClickListener {
            bookStoreViewModel.btnDelete()
        }

        // click "修改"


        //click RecyclerView Item
        binding.recdisp.addOnItemTouchListener(BookStoreAdapter.SingleItemClickListener(binding.recdisp, object :
                BookStoreAdapter.SingleItemClickListener.OnItemClickListener{
                    override fun onItemClick(view: View?, position: Int) {
                        bookStoreViewModel.onRecyclerItemClick(position)
                        Toast.makeText(this@MainActivity,"點擊事件觸發", Toast.LENGTH_SHORT ).show()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        Toast.makeText(this@MainActivity,"長按擊事件觸發", Toast.LENGTH_SHORT ).show()
                    }

                }))
        /** ===================== */
        /** 觀察者 區域 */
        bookStoreViewModel.myBookList.observe(this, Observer { newList ->
            newList?.let {
                //更新給adapter的data
                adapter.data = it
            }
        })


        bookStoreViewModel.isClick.observe(this, Observer { newClickEvent ->
            if(newClickEvent) {
                binding.bookNameInput.isEnabled = false
                binding.bookPriceInput.isEnabled = false
            }else {
//                binding.bookNameInput.isEnabled = true
                binding.bookNameInput.apply {
                    isEnabled = true
                }
//                binding.bookPriceInput.isEnabled = true
                binding.bookPriceInput.apply {
                    isEnabled = true
                }
            }
        })

        bookStoreViewModel.onClickPositionData.observe(this, Observer { newPosData ->
            Log.d("click data = ", "$newPosData")
            binding.bookNameInput.setText(newPosData.bookName)
            binding.bookPriceInput.setText(newPosData.bookPrice)
            bookStoreViewModel.dispClickItemFinish()

        })
    }
    private fun getInputData(): List<String>{
        val getString = mutableListOf<String>()
        val getBookName: Editable? = binding.bookNameInput.text
        val getBookPrice: Editable? = binding.bookPriceInput.text
        getString.add(getBookName.toString())
        getString.add(getBookPrice.toString())
        return getString
    }
}