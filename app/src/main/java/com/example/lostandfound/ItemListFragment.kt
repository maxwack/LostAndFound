package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dto.ItemDTO
import com.example.service.UserSingleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class ItemListFragment : Fragment()  {

    private lateinit var db : FirebaseFirestore
    private lateinit var mFStorageRef: StorageReference
    private var action = ""
    private val map : HashMap<String, ItemDTO> = HashMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.frag_item_list, container, false)

        db = FirebaseFirestore.getInstance()
        mFStorageRef = FirebaseStorage.getInstance().reference
        action = arguments!!.getString("action")!!

        return view
    }

    override fun onResume() {
        super.onResume()

        if(action == MainActivity.SHOW_ALL) {
            refreshUIAll()
        }else if(action == MainActivity.HISTORIC){
            refreshUIUser()
        }
    }



    private fun refreshUIAll(){

        val items = db.collection("items")
        items.orderBy("date").limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(fragmentManager!!.findFragmentByTag(document.id) != null){
                        val frag = fragmentManager!!.findFragmentByTag(document.id)

                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.detach(frag!!)
                        transaction.attach(frag)
                        transaction.commit()
                        continue
                    }

                    mFStorageRef.child("images/${document.id}").getBytes(Long.MAX_VALUE).addOnSuccessListener {
                        // Use the bytes to display the image
                        val item = ItemFragment()

                        val date = document.getDate("date")
                        val format = SimpleDateFormat("dd/MM/yyy")
                        val strDate = format.format(date)
                        var displayName = if(document.getString("userName") != null) document.getString("userName") else ""

                        val itemDto = ItemDTO(document.getString("userId")!!,
                            displayName!!,
                            document.getString("name")!!,
                            document.getString("places")!!,
                            document.getString("comment")!!,
                            document.getBoolean("reward")!!,
                            strDate,
                            document.getString("category")!!,
                            document.getString("state")!!,
                            it)

                        map[document.id] = itemDto

                        val arg = Bundle()
                        arg.putSerializable("itemDto", itemDto)
                        item.arguments = arg
                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.add(R.id.scroll_Main,item, document.id)
                        transaction.commit()
                    }.addOnFailureListener {
                        // Handle any errors
                        val item = ItemFragment()
                        val date = document.getDate("date")
                        val format = SimpleDateFormat("dd/MM/yyy")
                        val strDate = format.format(date)

                        var displayName = if(document.getString("userName") != null) document.getString("userName") else ""

                        val itemDto = ItemDTO(document.getString("userId")!!,
                            displayName!!,
                            document.getString("name")!!,
                            document.getString("places")!!,
                            document.getString("comment")!!,
                            document.getBoolean("reward")!!,
                            strDate,
                            document.getString("category")!!,
                            document.getString("state")!!,
                            null)

                        map[document.id] = itemDto

                        val arg = Bundle()
                        arg.putSerializable("itemDto", itemDto)
                        item.arguments = arg
                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.add(R.id.scroll_Main,item, document.id)
                        transaction.commit()
                    }
                }
            }
    }

    private fun refreshUIUser(){

        val items = db.collection("items")
        items.whereEqualTo("userId", UserSingleton.getInstance(activity!!.applicationContext).getId()).orderBy("date").limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(fragmentManager!!.findFragmentByTag(document.id) != null){
                        val frag = fragmentManager!!.findFragmentByTag(document.id)

                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.detach(frag!!)
                        transaction.attach(frag)
                        transaction.commit()
                        continue
                    }

                    mFStorageRef.child("images/${document.id}").getBytes(Long.MAX_VALUE).addOnSuccessListener {
                        // Use the bytes to display the image
                        val item = ItemFragment()

                        val date = document.getDate("date")
                        val format = SimpleDateFormat("dd/MM/yyy")
                        val strDate = format.format(date)
                        var displayName = if(document.getString("userName") != null) document.getString("userName") else ""

                        val itemDto = ItemDTO(document.getString("userId")!!,
                            displayName!!,
                        document.getString("name")!!,
                        document.getString("places")!!,
                        document.getString("comment")!!,
                            document.getBoolean("reward")!!,
                            strDate,
                        document.getString("category")!!,
                        document.getString("state")!!,
                        it)

                        map[document.id] = itemDto

                        val arg = Bundle()
                        arg.putSerializable("itemDto", itemDto)
                        item.arguments = arg
                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.add(R.id.scroll_Main,item, document.id)
                        transaction.commit()
                    }.addOnFailureListener {
                        // Handle any errors
                        val item = ItemFragment()

                        val date = document.getDate("date")
                        val format = SimpleDateFormat("dd/MM/yyy")
                        val strDate = format.format(date)
                        var displayName = if(document.getString("userName") != null) document.getString("userName") else ""

                        val itemDto = ItemDTO(document.getString("userId")!!,
                            displayName!!,
                            document.getString("name")!!,
                            document.getString("places")!!,
                            document.getString("comment")!!,
                            document.getBoolean("reward")!!,
                            strDate,
                            document.getString("category")!!,
                            document.getString("state")!!,
                            null)

                        map[document.id] = itemDto

                        val arg = Bundle()
                        arg.putSerializable("itemDto", itemDto)
                        item.arguments = arg
                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.add(R.id.scroll_Main,item, document.id)
                        transaction.commit()
                    }
                }
            }
            .addOnFailureListener {  e ->
                Log.w("TAG", "Error adding document", e)

            }
    }


    fun getMap(): HashMap<String, ItemDTO> {
        return map
    }
}