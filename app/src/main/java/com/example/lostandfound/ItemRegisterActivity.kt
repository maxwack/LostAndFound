package com.example.lostandfound

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ItemRegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_register)

        val timeNow = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH)
        val date = format.format(timeNow)

        val stringDate = findViewById<TextView>(R.id.string_date)
        stringDate.text = date


        if(intent.action == MainActivity.REGISTER_FOUND){
            val text = findViewById<TextView>(R.id.string_foundat)
            text.text= getString(R.string.foundAt)
            val addButton = findViewById<FloatingActionButton>(R.id.button_add_place)
            addButton.hide()
            val rewardString = findViewById<TextView>(R.id.string_reward)
            rewardString.visibility = View.INVISIBLE
            val rewardCheck = findViewById<CheckBox>(R.id.item_reward)
            rewardCheck.visibility = View.INVISIBLE
        }else{
            val text = findViewById<TextView>(R.id.string_foundat)
            text.text= getString(R.string.lostAt)
            val addButton = findViewById<FloatingActionButton>(R.id.button_add_place)
            addButton.show()
            val rewardString = findViewById<TextView>(R.id.string_reward)
            rewardString.visibility = View.VISIBLE
            val rewardCheck = findViewById<CheckBox>(R.id.item_reward)
            rewardCheck.visibility = View.VISIBLE
        }



    }

    fun addOnClick(v: View) {

        val editText = findViewById<EditText>(R.id.item_place)
        val newPlace = editText.text.toString()

        if(newPlace == ""){
            return
        }

        val addedPlace =  findViewById<TextView>(R.id.string_added_place)
        var currentPlace = addedPlace.text.toString()
        if(currentPlace == ""){
            addedPlace.text = newPlace
        }else{
            addedPlace.text = "$currentPlace, $newPlace"
        }

        editText.text.clear()
    }

    fun registerOnClick(v: View){
        finish()
    }


}