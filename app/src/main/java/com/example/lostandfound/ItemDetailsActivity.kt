package com.example.lostandfound

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dto.ItemDTO
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text

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

        val itName = findViewById<TextView>(R.id.item_name)
        val itDate = findViewById<TextView>(R.id.item_reg_date)
        val itPlace = findViewById<TextView>(R.id.item_places)
        val itComment = findViewById<TextView>(R.id.item_comment)
        val itImg = findViewById<ImageView>(R.id.item_Img)

        val itemDto = intent!!.extras["itemDto"] as ItemDTO

        itName.text = itemDto.name
        itDate.text = itemDto.date
        itPlace.text = itemDto.place
        itComment.text = itemDto.comment
        if(itemDto.img != null){
            itImg.setImageBitmap(BitmapFactory.decodeByteArray(itemDto.img, 0, itemDto.img!!.size))
        }

        window.setLayout(mMaxWidth, mMaxHeight)
    }

    fun cancelOnClick(v: View){
        finish()
    }
}