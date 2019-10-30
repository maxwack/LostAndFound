package com.example.lostandfound

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dto.ItemDTO


class ItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_item, container, false)

        val itemDto = this.arguments!!.getSerializable("itemDto")!! as ItemDTO

        val itName = view.findViewById<TextView>(R.id.itemName)
        itName.text = itemDto.name
        val itDate = view.findViewById<TextView>(R.id.item_reg_date)
        itDate.text = itemDto.date
        val itPlace = view.findViewById<TextView>(R.id.item_place)
        itPlace.text = itemDto.place
        val itComment = view.findViewById<TextView>(R.id.string_shortDesc)
        itComment.text = itemDto.comment

        val itImage = view.findViewById<ImageView>(R.id.img_item)
        if(itemDto.img != null) {
            itImage.setImageBitmap(BitmapFactory.decodeByteArray(itemDto.img, 0, itemDto.img!!.size))
        }

        // Set a click listener for button widget
        view.setOnClickListener{
            val itemName = view.findViewById<TextView>(R.id.itemName)
            val i = Intent(context, ItemDetailsActivity::class.java)
            i.action = itemName.text.toString()
            i.putExtra("itemDto", itemDto)
            startActivity(i)
        }

        return view
    }
}