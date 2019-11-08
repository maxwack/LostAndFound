package com.example.lostandfound

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.service.UserSingleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query


class ChatListFragment: Fragment()   {

    private lateinit var db : FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_chat_list, container, false)

        db = FirebaseFirestore.getInstance()

        populateChat()

        return view
    }

    private fun populateChat(){
        val chats = db.collection("chat")

        val queries = arrayOfNulls<Query>(2)
        val tasks = arrayOfNulls<Task<*>>(2)

        queries[0] = chats.whereEqualTo("sender", UserSingleton.getInstance(this.context!!).getId()).orderBy("lastMessage")
        tasks[0] = queries[0]!!.get()

        queries[1] = chats.whereEqualTo("target", UserSingleton.getInstance(this.context!!).getId()).orderBy("lastMessage")
        tasks[1] = queries[1]!!.get()

        Tasks.whenAllSuccess<Any>(*tasks).addOnSuccessListener { list ->

            for (`object` in list) {
                val querySnapshot = `object` as QuerySnapshot
                if (querySnapshot != null || !querySnapshot.isEmpty) {
                    for (snapshot in querySnapshot.documents) {
                        val user = db.collection("users")
                        val targetID : String? = if(snapshot["target"] == UserSingleton.getInstance(this.context!!).getId()) snapshot.getString("sender") else snapshot.getString("target")
                        user.document(targetID!!).get()
                            .addOnSuccessListener {
                                val conv = ConvFrag()
                                val arg = Bundle()
                                arg.putSerializable("userName", it.getString("displayName"))
                                arg.putSerializable("chatID", it.id)
                                conv.arguments = arg
                                val transaction = fragmentManager!!.beginTransaction()
                                transaction.add(R.id.scroll_Chats,conv)
                                transaction.commit()
                            }
                            .addOnFailureListener {

                            }
                    }
                }
            }

            //You can do whatever you want with your list
        }.addOnFailureListener {
                e ->
            Log.w("TAG", "Error adding document", e)
        }
    }
}