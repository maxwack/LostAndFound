package com.example.lostandfound

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ItemRegisterActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_register)

        val timeNow = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH)
        val date = format.format(timeNow)

        val stringDate = findViewById<TextView>(R.id.string_date)
        stringDate.text = date


        if(intent.action == MainActivity.REGISTER_FOUND){
            val text = findViewById<TextView>(R.id.string_foundat)
            text.text= getString(R.string.foundAt)
            val rewardString = findViewById<TextView>(R.id.string_reward)
            rewardString.visibility = View.INVISIBLE
            val rewardCheck = findViewById<CheckBox>(R.id.item_reward)
            rewardCheck.visibility = View.INVISIBLE
        }else{
            val text = findViewById<TextView>(R.id.string_foundat)
            text.text= getString(R.string.lostAt)
            val rewardString = findViewById<TextView>(R.id.string_reward)
            rewardString.visibility = View.VISIBLE
            val rewardCheck = findViewById<CheckBox>(R.id.item_reward)
            rewardCheck.visibility = View.VISIBLE
        }

        val buttonAddCat = findViewById<Button>(R.id.button_select)
        buttonAddCat.setOnClickListener{
            showDialog()
        }


    }

    // Method to show an alert dialog with multiple choice list items
    private fun showDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize an array of colors
        val choiceArray = arrayOf("Clothes","Electronic Device","Accessories","Animal")

        val addCat = findViewById<TextView>(R.id.string_added_cat)
        // Initialize a boolean array of checked items
        val arrayChecked = booleanArrayOf(false,false,false,false)

        if(addCat.text != "") {
            for (l in addCat.text.lines()) {
                if(l == "") continue
                val ind = choiceArray.indexOf(l.trim())
                arrayChecked[ind] = true
            }
        }


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Choose categories.")

        /*
            **** reference source developer.android.com ***

            AlertDialog.Builder setMultiChoiceItems (CharSequence[] items,
                            boolean[] checkedItems,
                            DialogInterface.OnMultiChoiceClickListener listener)

            Set a list of items to be displayed in the dialog as the content, you will be notified
            of the selected item via the supplied listener. The list will have a check mark
            displayed to the right of the text for each checked item. Clicking on an item
            in the list will not dismiss the dialog. Clicking on a button will dismiss the dialog.

            Parameters
                items CharSequence : the text of the items to be displayed in the list.

                checkedItems boolean : specifies which items are checked. It should be null in which
                    case no items are checked. If non null it must be exactly the same length
                    as the array of items.

                listener DialogInterface.OnMultiChoiceClickListener : notified when an item on the
                    list is clicked. The dialog will not be dismissed when an item is clicked. It
                    will only be dismissed if clicked on a button, if no buttons are supplied
                    it's up to the user to dismiss the dialog.

            Returns
                AlertDialog.Builder This Builder object to allow for chaining of calls to set methods
        */

        // Define multiple choice items for alert dialog
        builder.setMultiChoiceItems(choiceArray, arrayChecked) { _, which, isChecked->
            // Update the clicked item checked status
            arrayChecked[which] = isChecked
        }


        // Set the positive/yes button click listener
        builder.setPositiveButton("OK") { _, _ ->
            addCat.text=""
            for (i in 0 until choiceArray.size) {
                val checked = arrayChecked[i]
                if (checked) {
                    addCat.text = "${addCat.text}  ${choiceArray[i]} \n"
                }
            }
        }


        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    fun addOnClick() {

        val editText = findViewById<EditText>(R.id.item_place)
        val newPlace = editText.text.toString()

        if(newPlace == ""){
            return
        }

        val addedPlace =  findViewById<TextView>(R.id.string_added_place)
        var currentPlace = addedPlace.text.toString()
        if(currentPlace == ""){
            addedPlace.text = newPlace
        }else{
            addedPlace.text = "$currentPlace, $newPlace"
        }

        editText.text.clear()
    }

    fun registerOnClick(v: View){
        finish()
    }

    fun cancelOnClick(v: View){
        finish()
    }

}