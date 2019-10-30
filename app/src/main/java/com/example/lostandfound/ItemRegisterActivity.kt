package com.example.lostandfound

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat


class ItemRegisterActivity: AppCompatActivity() {

    private lateinit var action: String
    private var mFFirestore: FirebaseFirestore? = null
    private var mFStorageRef: StorageReference? = null

    var choice = arrayOf("Take a picture", "Search for Image")

    private var itemName :EditText? = null
    private var itemPlaces :TextView? = null
    private var itemCategory :TextView? = null
    private var itemComment :EditText? = null
    private var itemReward  :CheckBox? = null
    private var itemImg: ImageView? = null

    private var mCurrentPhotoPath: String? = null;
    @ServerTimestamp
    var time: Date? = null

    companion object{
        const val PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 10
        const val PERMISSIONS_REQUEST_CAMERA = 20
        const val GALLERY_REQUEST_CODE = 500
        const val CAMERA_REQUEST_CODE = 600
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_register)

        val timeNow = LocalDateTime.now()
        val format = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH)
        val date = format.format(timeNow)

        val stringDate = findViewById<TextView>(R.id.string_date)
        stringDate.text = date

        action = intent.action
        mFFirestore = FirebaseFirestore.getInstance()
        mFStorageRef = FirebaseStorage.getInstance().reference


        itemName = findViewById(R.id.item_Name)
        itemPlaces = findViewById(R.id.string_added_place)
        itemCategory = findViewById(R.id.string_added_cat)
        itemComment = findViewById(R.id.item_comment)
        itemReward = findViewById(R.id.item_reward)
        itemImg = findViewById(R.id.item_Img)

        val itemUser = findViewById<TextView>(R.id.item_reg_user)
        itemUser.text = FirebaseAuth.getInstance().currentUser!!.displayName

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

    fun addOnClick(v: View) {

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


        val data = HashMap<String, Any>()
        data["userId"] =  FirebaseAuth.getInstance().currentUser!!.uid
        data["item_name"] =  itemName?.text.toString()
        data["item_places"] = itemPlaces?.text.toString()
        data["item_category"] = itemCategory?.text.toString()
        data["comment"] = itemComment?.text.toString()
        data["date"] = FieldValue.serverTimestamp()
        //            val item = ItemDTO("me", item_name,list_place )
        if (action == MainActivity.REGISTER_LOST) {
            data["reward"] = itemReward?.isChecked.toString()
            data["state"] = MainActivity.REGISTER_LOST
        }else if (action == MainActivity.REGISTER_FOUND){
            data["state"] = MainActivity.REGISTER_FOUND
        }

        mFFirestore!!.collection("items").add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
                // File or Blob
                val file = Uri.fromFile(File(mCurrentPhotoPath))

                // Create the file metadata
                val metadata = StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build()

                // Upload file and metadata to the path 'images/mountains.jpg'
                val uploadTask = mFStorageRef!!.child("images/${documentReference.id}").putFile(file, metadata)

                // Listen for state changes, errors, and completion of the upload.
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    println("Upload is $progress% done")
                }.addOnPausedListener {
                    println("Upload is paused")
                }.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    // Handle successful uploads on complete
                    // ...
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
            }

    fun cancelOnClick(v: View){
        finish()
    }

    fun imageOnClick(v: View){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("What do you want to do ?")
        builder.setItems(choice) { _, which ->
            if(which == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    // Permission has already been granted
                    pickFromCamera()
                } else {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)  && ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                            PERMISSIONS_REQUEST_CAMERA)
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                            PERMISSIONS_REQUEST_CAMERA)

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }else{
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSIONS_REQUEST_EXTERNAL_STORAGE)
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSIONS_REQUEST_EXTERNAL_STORAGE)

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    pickFromGallery()
                }
            }
        }
        builder.show()

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    pickFromGallery()
                }
//                else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
                return
            }
            PERMISSIONS_REQUEST_CAMERA ->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    pickFromCamera()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        val mimeTypes= arrayOf("image/jpeg", "image/png" )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun pickFromCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

    }

    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImage: Uri? = data!!.data
                    mCurrentPhotoPath = selectedImage!!.path
                    itemImg!!.setImageURI(selectedImage)
                }
                CAMERA_REQUEST_CODE->{
                    //To get the File for further usage
                    var bitmap : Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
                    itemImg!!.setImageBitmap(bitmap)
                }
            }
        }
    }

    }