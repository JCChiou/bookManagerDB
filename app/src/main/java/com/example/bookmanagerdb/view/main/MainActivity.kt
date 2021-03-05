package com.example.bookmanagerdb.view.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagerdb.BuildConfig
import com.example.bookmanagerdb.R
import com.example.bookmanagerdb.database.*
import com.example.bookmanagerdb.databinding.ActivityMainBinding
import com.example.bookmanagerdb.viewModel.ApiBookViewModel
import com.example.bookmanagerdb.viewModel.BookStoreAdapter.BookStoreAdapter
import com.example.bookmanagerdb.viewModel.BookStoreAdapter.FetchCountAdapter
import com.example.bookmanagerdb.viewModel.BookStoreViewModel
import com.example.bookmanagerdb.viewModel.BookStoreViewModelFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var myRecyclerView: RecyclerView
    lateinit var bookStoreViewModel : BookStoreViewModel
    lateinit var apiBookViewModel : ApiBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // debug mode use Timber
        if(BuildConfig.DEBUG){
            Timber.plant(   Timber.DebugTree())
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** get database Instance & ViewModel instance */
        val application = requireNotNull(this.application)
        val dataSource = BookStoreDatabase.getInstance(application).bookStoreDao
        val viewModelFactory = BookStoreViewModelFactory(dataSource,application)
        bookStoreViewModel =
            ViewModelProvider(this, viewModelFactory).get(BookStoreViewModel::class.java)

        apiBookViewModel =
                ViewModelProvider(this).get(ApiBookViewModel::class.java)
        // data binding
        binding.bookStoreViewModle = bookStoreViewModel

        /** define RecyclerView setting */
        val adapter = BookStoreAdapter()
        myRecyclerView = binding.recdisp
        val linearLayoutManager = LinearLayoutManager(this)
        myRecyclerView.layoutManager = linearLayoutManager
        //使用預設分隔線
        myRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        myRecyclerView.adapter = adapter
        /** ================= */

        /** click event area */
        // click "新增"
        binding.btnAdd.setOnClickListener {
            //action to do
            //inPutData回傳List<BookStore> ,為配合透過Api取得資料型態可以做批次寫入資料庫
            val getEditInputData = inPutData()
            //欄位檢查
            if(getEditInputData[0].title.isNotEmpty() && getEditInputData[0].isbn.isNotEmpty()){
                bookStoreViewModel.btnadd(getEditInputData)
            }else {
                Toast.makeText(this,"你尚未輸入資料", Toast.LENGTH_SHORT).show()
            }
            clearInput()
        }

        // click "查詢"
        binding.btnQuery.setOnClickListener {
            //action to do
            val getInputdata = inPutData()[0]
            if (getInputdata.title.isEmpty() && getInputdata.isbn.isEmpty()){
                // if input is empty , 顯示所有資料
                bookStoreViewModel.requestBookList()
            } else{
                // if input not empty ,顯示查詢結果
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
            val modifyString = inPutData()[0]
            if (modifyString.title.isNotEmpty() && modifyString.isbn.isNotEmpty()) {
                bookStoreViewModel.btnModify(modifyString)
                clearInput()
            }else{
                Toast.makeText(this,"請重新選取欲修改資料", Toast.LENGTH_SHORT).show()
            }
        }

        //click RecyclerView Item
        binding.recdisp.addOnItemTouchListener(BookStoreAdapter.SingleItemClickListener(binding.recdisp, object :
                BookStoreAdapter.SingleItemClickListener.OnItemClickListener{
                    override fun onItemClick(view: View?, position: Int) {
                        bookStoreViewModel.onRecyclerItemClick(position)
                        bookStoreViewModel.setSelectLock()  //點選項目後.turn flag
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
//                Log.d("data=", "$it")
                adapter.data = it
            }
        })

        // action之後 變更flag 執行UI refresh
        bookStoreViewModel.actionFinished.observe(this, Observer { newFinish ->
            if (newFinish){
                bookStoreViewModel.requestBookList()
            }
        })

        // recycler com.example.bookmanagerdb.view item click後 onClickPositionData更新，執行data rebinding
        bookStoreViewModel.onClickPositionData.observe(this, Observer {
            binding.invalidateAll()
        })

        //faker Api return data
        apiBookViewModel.response.observe(this, Observer {
            Timber.d("觀察者資料 = ${it.data}")
        })

        apiBookViewModel.dataFlag.observe(this, Observer {
            //呼叫BookViewModel 設定DB寫如Flag打開
            if (apiBookViewModel.dataFlag.value == true){
                bookStoreViewModel.setDbInsertFlagOn()
            }
        })

        bookStoreViewModel.dbWriteFlag.observe(this, Observer { newFlag ->

            if (newFlag == true){
                val apiData = apiBookViewModel.response.value?.data
                if (apiData != null) {
                    bookStoreViewModel.btnadd(apiData)
                    apiBookViewModel.setDataFlagOff()
//                    apiBookViewModel.clearFetchData()
                }
            }
        })

    }

    // get EditText data
    private fun inPutData(): List<BookStore>{
        val getBookName: Editable? = binding.bookNameInput.text
        val getBookPrice: Editable? = binding.bookPriceInput.text
        val data = arrayListOf(BookStore(getBookName.toString(),getBookPrice.toString()))
        return data
//        return BookStore(getBookName.toString(),getBookPrice.toString())
    }

    private fun clearInput(){
        binding.bookNameInput.text = null
        binding.bookPriceInput.text = null
    }

    //產生action Bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //action Bar click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.item_fetch ->{
                // when click "item fetch do something"
                Timber.d("click event trigger")
                showDialogForFetch()
                true
            }
            R.id.drop_table ->{
                //drop table
                bookStoreViewModel.tableClear()
                true
            }
            else ->return super.onOptionsItemSelected(item)
        }

    }

    @SuppressLint("InflateParams")
    private fun showDialogForFetch() {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.fetch_data_count_dialog,null)
        dialog.setView(view)
        val newDialog = dialog.create()
        newDialog.show()

        view.findViewById<Button>(R.id.bt_fetch_confirm).setOnClickListener {
            Timber.d("confirm click")
            apiBookViewModel.sendApiRequest()
            newDialog.dismiss()

        }

        view.findViewById<Button>(R.id.bt_fetch_cancel).setOnClickListener {
            newDialog.dismiss()
        }
        setSpinnerAdapter(view)


    }

    private fun setSpinnerAdapter(myView: View){
        val spinner = myView.findViewById<Spinner>(R.id.spinner)
        Timber.d("$spinner")
        val spinnerAdapter = FetchCountAdapter(this, myView).setAdapter()
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                apiBookViewModel.sendFetchDataCount(position + 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}
