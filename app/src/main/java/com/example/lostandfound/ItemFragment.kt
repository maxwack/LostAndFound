package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_item, container, false)

        // Set a click listener for button widget
        view.setOnClickListener{
            val i = Intent(context, ItemDetailsActivity::class.java)
            startActivity(i)
        }

        return view
    }
}