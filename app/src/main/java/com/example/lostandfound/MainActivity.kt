package com.example.lostandfound

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 1..10){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.scroll_Main, ItemFragment())
            transaction.commit()
        }

    }
}