package com.example.lostandfound

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.dto.ItemDTO


class ItemFragment : Fragment() {

    companion object {
        const val RES_DETAIL = 200
    }

    private lateinit var itemDto : ItemDTO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_item, container, false)

        itemDto = this.arguments!!.getSerializable("itemDto")!! as ItemDTO

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

        val itState = view.findViewById<ImageView>(R.id.img_state)
        if(itemDto.state == MainActivity.REGISTER_FOUND){
            itState.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.exclamation_mark, null))
        }else if(itemDto.state == MainActivity.REGISTER_LOST){
            itState.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.question_mark, null))
        }

        // Set a click listener for button widget
        view.setOnClickListener{
            val itemName = view.findViewById<TextView>(R.id.itemName)
            val i = Intent(context, ItemDetailsActivity::class.java)
            i.action = itemName.text.toString()
            i.putExtra("itemDto", itemDto)
            startActivityForResult(i, RES_DETAIL)
        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == RES_DETAIL) {
            if(data!!.action == "MESSAGE") {
                (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                val transaction = fragmentManager!!.beginTransaction()
                val messageFrag = ChatFragment()
                val args = Bundle()
                args.putString("chatID", data.extras.getString("chatID"))
                args.putString("targetName",itemDto.displayName)
                messageFrag.arguments = args
                transaction.replace(R.id.frag_holder, messageFrag, "chat")
                transaction.commit()
            }
        }
    }
}