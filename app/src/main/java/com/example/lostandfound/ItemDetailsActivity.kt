package com.example.lostandfound

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ItemDetailsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val mMaxWidth = (dm.widthPixels * 0.8).toInt()
        val mMaxHeight = (dm.heightPixels * 0.8).toInt()

        val styledAttributes = theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        styledAttributes.recycle()
        supportActionBar!!.title = intent.action  // provide compatibility to all the versions

        window.setLayout(mMaxWidth, mMaxHeight)
    }

    fun cancelOnClick(v: View){
        finish()
    }
}