package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_item, container, false)

        // Set a click listener for button widget
        view.setOnClickListener{
            val itemName = view.findViewById<TextView>(R.id.itemName)
            val i = Intent(context, ItemDetailsActivity::class.java)
            i.action = itemName.text.toString()
            startActivity(i)
        }

        return view
    }
}