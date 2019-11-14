package com.example.lostandfound

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.EditText
import com.example.dto.MessageDTO
import com.firebase.ui.firestore.FirestoreRecyclerOptions.Builder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.service.UserSingleton
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.firestore.FirebaseFirestoreException
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lostandfound.ChatFragment
import com.example.lostandfound.ChatFragment.ChatMessage
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


class ChatFragment : Fragment() {

    private var senderID: String = ""
    private var chatID: String = ""
    private var targetName : String = ""
    private var editMessage : EditText? = null
    private var recyclerView : RecyclerView? = null

    companion object {
        const val COLLECTION_KEY = "message"
        const val CHAT_ID = "uid"
        const val SENDER_FIELD = "sender"
        const val TIME_FIELD = "time"
        const val TEXT_FIELD = "text"
    }

    private val fireStoreChat by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_KEY)
    }

    private var adapter: FirestoreRecyclerAdapter<MessageDTO, ChatMessage>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view =  inflater.inflate(R.layout.frag_chat, container, false)
//        realtimeUpdateListener()
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_send)

        recyclerView = view.findViewById(R.id.list_of_messages)

        senderID = UserSingleton.getInstance(activity!!.applicationContext).getId()!!
        chatID = this.arguments!!.getString("chatID")!!
        targetName = this.arguments!!.getString("targetName")


        (activity as AppCompatActivity).supportActionBar!!.title = targetName

        editMessage = view.findViewById(R.id.input)

        fab.setOnClickListener {
            sendMessage()
        }
        init()
        realtimeUpdateListener()
        return view
    }

    private fun init() {
        val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun sendMessage() {
        val newMessage = mapOf(
            SENDER_FIELD to senderID,
            CHAT_ID to chatID,
            TIME_FIELD to System.currentTimeMillis(),
            TEXT_FIELD to editMessage!!.text.toString())
        fireStoreChat.add(newMessage)
            .addOnSuccessListener {
                Toast.makeText(activity, "Message Sent", Toast.LENGTH_SHORT).show()
                FirebaseFirestore.getInstance().collection("chat").document(chatID).update("lastMessage",
                    FieldValue.serverTimestamp())
            }
            .addOnFailureListener { e -> Log.e("ERROR", e.message) }
        editMessage!!.text.clear()
    }

    private fun realtimeUpdateListener() {

        var response = Builder<MessageDTO>()
                .setQuery(fireStoreChat.orderBy(TIME_FIELD), MessageDTO::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<MessageDTO, ChatMessage>(response) {
            override fun onBindViewHolder(
                holder: ChatMessage,
                position: Int,
                model: MessageDTO
            ) {

                if(model.sender == UserSingleton.getInstance(context!!).getId()){
                    holder.textName!!.text = UserSingleton.getInstance(context!!).getDisplayName()
                }else{
                    holder.textName!!.text = targetName
                }


                holder.textTime!!.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.time)
                holder.textMessage!!.text = model.text
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): ChatMessage {
                val view = LayoutInflater.from(group.context)
                    .inflate(R.layout.chat_message, group, false)
                return ChatMessage(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("error", e.message)
            }
        }

        adapter!!.notifyDataSetChanged()
        recyclerView!!.adapter = adapter
    }

    inner class ChatMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textName: TextView? = null
        internal var textTime: TextView? = null
        internal var textMessage: TextView? = null

        init {
            textName = itemView.findViewById(R.id.message_user)
            textTime = itemView.findViewById(R.id.message_time)
            textMessage = itemView.findViewById(R.id.message_text)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }
}