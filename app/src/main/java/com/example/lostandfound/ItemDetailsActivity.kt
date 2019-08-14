package com.example.lostandfound

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics

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

        window.setLayout(mMaxWidth, mMaxHeight)
    }
}