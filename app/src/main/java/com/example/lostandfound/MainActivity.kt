package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity(){

    companion object {
        const val REGISTER_FOUND = "found"
        const val REGISTER_LOST = "lost"
    }

    var choice = arrayOf("I found something !", "I lost something !")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 1..10){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.scroll_Main, ItemFragment())
            transaction.commit()
        }

        val registerButton = findViewById<FloatingActionButton>(R.id.button_register_item)
        // Set a click listener for button widget
        registerButton.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            builder.setTitle("What happened?")
            builder.setItems(choice, DialogInterface.OnClickListener { dialog, which ->
                if(which == 0) {
                    val i = Intent(this, ItemRegisterActivity::class.java)
                    i.action = REGISTER_FOUND
                    startActivity(i)
                }else{
                    val i = Intent(this, ItemRegisterActivity::class.java)
                    i.action = REGISTER_LOST
                    startActivity(i)
                }

            })
            builder.show()
        }

    }
}