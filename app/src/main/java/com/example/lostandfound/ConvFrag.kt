package com.example.lostandfound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ConvFrag : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_conv, container, false)

        val userName = this.arguments!!.getString("userName")!!
        val chatID :String = this.arguments!!.getString("chatID")
        val userText = view.findViewById<TextView>(R.id.DisplayName)
        userText.text = userName

        // Set a click listener for button widget
        view.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
//                        val frg = supportFragmentManager.findFragmentByTag("item_list")
            val messageFrag = ChatFragment()
            val args = Bundle()
            args.putString("chatID", chatID)
            args.putString("targetName", userName)
            messageFrag.arguments = args
            transaction.replace(R.id.frag_holder, messageFrag, "chat")
            transaction.commit()
        }

        return view
    }
}