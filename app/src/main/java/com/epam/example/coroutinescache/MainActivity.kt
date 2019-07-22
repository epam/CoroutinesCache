package com.epam.example.coroutinescache

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob()

    private val persistence by lazy { Repository(cacheDir) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pressMe.setOnClickListener { onClick() }
    }

    private fun onClick() {
        launch(Main) {
            val data = withContext(IO) { persistence.getParameterizedData("1234") }
            val result = data.toString()
            messageView.text = result
        }
    }
}