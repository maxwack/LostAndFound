package com.example.lostandfound


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dto.ItemDTO
import com.example.service.UserSingleton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ItemDetailsActivity: AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private lateinit var itemDto: ItemDTO
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val mMaxWidth = (dm.widthPixels * 0.8).toInt()
        val mMaxHeight = (dm.heightPixels * 0.5).toInt()

        val styledAttributes = theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        styledAttributes.recycle()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar)

        val titleTxt = findViewById<TextView>(R.id.action_bar_title)
        titleTxt.text = intent.action


        db = FirebaseFirestore.getInstance()

//        val itName = findViewById<TextView>(R.id.item_name)
        val itDisplayName = findViewById<TextView>(R.id.item_reg_user)
        val itDate = findViewById<TextView>(R.id.item_when)
        val itRegisterDate = findViewById<TextView>(R.id.item_reg_date)
        val itPlace = findViewById<TextView>(R.id.item_places)
        val itComment = findViewById<TextView>(R.id.item_comment)
        val itImg = findViewById<ImageView>(R.id.item_Img)
        val itReward = findViewById<CheckBox>(R.id.item_reward)
        val butMessage = findViewById<Button>(R.id.button_message)

        itemDto = intent!!.extras["itemDto"] as ItemDTO

//        itName.text = itemDto.name
        itDate.text = itemDto.date
        itRegisterDate.text = itemDto.registerDate
        itPlace.text = itemDto.place
        itComment.text = itemDto.comment
        itReward.isChecked =itemDto.reward

        if(itemDto.displayName.isEmpty()){
            itDisplayName.text = "Anonymous"
        }else{
            itDisplayName.text = itemDto.displayName
        }

        if(UserSingleton.getInstance(this).getId() == null){
            butMessage.isEnabled = false
        }else{
            currentUser =  UserSingleton.getInstance(this).getId()!!
            butMessage.isEnabled = currentUser != itemDto.user
        }

        if(itemDto.state == MainActivity.REGISTER_LOST){
            val imgFound = findViewById<ImageView>(R.id.img_FoundLost)
            imgFound.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.question_mark))
        }else{
            val imgFound = findViewById<ImageView>(R.id.img_FoundLost)
            imgFound.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.exclamation_mark))
        }

        if(itemDto.img != null){
            itImg.setImageBitmap(BitmapFactory.decodeByteArray(itemDto.img, 0, itemDto.img!!.size))
        }

        window.setLayout(mMaxWidth, mMaxHeight)
    }

    fun messageOnClick(v: View){

        val chats = db.collection("chat")
        var senderID: String
        var targetID: String
        if(currentUser <= itemDto.name){
            senderID = currentUser
            targetID = itemDto.user
        }else{
            senderID = itemDto.user
            targetID = currentUser
        }

        chats.whereEqualTo("sender", senderID)
            .whereEqualTo("target", targetID)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size() > 0) {
                    for (document in documents) {
                        val returnIntent = Intent("MESSAGE")
                        returnIntent.putExtra("chatID", document.id)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }else{
                    val newChat = mapOf(
                        "sender" to senderID,
                        "target" to targetID,
                        "lastMessage" to FieldValue.serverTimestamp() )
                    chats.add(newChat)
                        .addOnSuccessListener {
                            Log.w("TAG", "Chat correctly added")
                            val returnIntent = Intent("MESSAGE")
                            returnIntent.putExtra("chatID", it.id)
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        }
                }
            }
            .addOnFailureListener {  e ->
                Log.w("TAG", "Error retrieving chat", e)
            }

    }


    fun closeOnClick(v: View){
        finish()
    }
}