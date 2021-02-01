package com.example.bookmanagerdb

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bookmanagerdb.database.BookStoreDatabase
import com.example.bookmanagerdb.databinding.ActivityMainBinding
import com.example.bookmanagerdb.viewModel.BookStoreViewModel
import com.example.bookmanagerdb.viewModel.BookStoreViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val application = requireNotNull(this.application)
        val dataSource = BookStoreDatabase.getInstance(application).bookStoreDao
        val viewModelFactory = BookStoreViewModelFactory(dataSource,application)
        val bookStoreViwlModel =
            ViewModelProvider(this, viewModelFactory).get(BookStoreViewModel::class.java)


        binding.btnAdd.setOnClickListener {
            //action to do
            bookStoreViwlModel.btnadd()
            Toast.makeText(this,"click", Toast.LENGTH_SHORT).show()
        }

    }
}