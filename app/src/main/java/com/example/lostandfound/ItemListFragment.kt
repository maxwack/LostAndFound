package com.example.lostandfound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ItemListFragment : Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_item_list, container, false)

        for (i in 1..10){
            val transaction = fragmentManager!!.beginTransaction()
            transaction.add(R.id.scroll_Main, ItemFragment())
            transaction.commit()
        }
        return view
    }
}