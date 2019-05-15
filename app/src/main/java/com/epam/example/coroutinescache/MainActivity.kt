package com.epam.example.coroutinescache

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val persistence by lazy { Repository(cacheDir) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pressMe.setOnClickListener { onClick() }
    }

    private fun onClick() {
        GlobalScope.launch (Dispatchers.Main) {
            val data = persistence.getData()
            messageView.text = data.toString()
        }
    }

}
