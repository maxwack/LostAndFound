package com.example.lostandfound

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.content.DialogInterface
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.res.ResourcesCompat
import com.example.service.UserSingleton
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity(){

    companion object {
        const val REGISTER_FOUND = "found"
        const val REGISTER_LOST = "lost"
        const val HISTORIC = "historic"
        const val SHOW_ALL = "show_all"
        const val RC_SIGN_IN = 200
        const val RC_LOG_IN = 300
    }

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build())
//        AuthUI.IdpConfig.PhoneBuilder().build(),
//        AuthUI.IdpConfig.GoogleBuilder().build(),
//        AuthUI.IdpConfig.FacebookBuilder().build(),
//        AuthUI.IdpConfig.TwitterBuilder().build())

    private lateinit var auth: FirebaseAuth

    private val choice = arrayOf("I found something !", "I lost something !")
    private val choiceUser = arrayOf("Log In", "Register")
    private lateinit var toolbar: ActionBar
    private lateinit var bottombar : BottomNavigationView

    private val fireStoreUsers by lazy {
        FirebaseFirestore.getInstance().collection("users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val transaction = supportFragmentManager.beginTransaction()
        val fragInstance = ItemListFragment()
        val args = Bundle()
        args.putString("action", SHOW_ALL)
        fragInstance.arguments = args
        transaction.add(R.id.frag_holder, fragInstance,"item_list")
        transaction.commit()

        toolbar = supportActionBar!!

        bottombar = findViewById(R.id.bottom_navigation)

        if(auth.currentUser != null){
            toolbar.title = auth.currentUser!!.displayName
            fireStoreUsers.whereEqualTo("mail", auth.currentUser!!.email).limit(1)
            .get()
                .addOnSuccessListener {
                    UserSingleton.getInstance(this).createSingleton(it.documents[0].id, auth.currentUser!!.displayName!!)
                }
                .addOnFailureListener {  e ->
                    Log.w("TAG", "Error adding document", e)
                }
            bottombar.menu.getItem(2).isEnabled = true
        }else{
            toolbar.title = "Anonymous"
            bottombar.menu.getItem(2).isEnabled = false
        }


        toolbar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(resources,android.R.drawable.ic_menu_revert,null))
        bottombar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topbar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.menu_add -> {
                if (auth.currentUser != null) {
                    // already signed in
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("What happened?")
                    builder.setItems(choice, DialogInterface.OnClickListener {_, which ->
                        if(which == 0) {
                            val i = Intent(this, ItemRegisterActivity::class.java)
                            i.action = REGISTER_FOUND
                            startActivity(i)
                        }else{
                            val i = Intent(this, ItemRegisterActivity::class.java)
                            i.action = REGISTER_LOST
                            startActivity(i)
                        }
                    })
                    builder.show()
                } else {
                    // not signed in
                    // Create and launch sign-in intent

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Only logged users can register a new Item.")
                    builder.setItems(choiceUser) { _, which ->
                        if(which == 0) {
                            val intent = Intent(this,LoginActivity::class.java)
                            startActivityForResult(intent, RC_LOG_IN)
                        }else{
                            startActivityForResult(
                                AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                                RC_SIGN_IN)
                        }
                    }
                    builder.show()

//                    firebase.auth.createUserWithEmailAndPassword(email, password)
//                        .then(function(result) {
//                            return result.user.updateProfile({
//                                displayName: document.getElementById("name").value
//                            })
//                        }).catch(function(error) {
//                            console.log(error);
//                        });`
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivityForResult(
//                        intent,
//                        RC_SIGN_IN)
                }




                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_search -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.menu_message -> {
                toolbar.setDisplayHomeAsUpEnabled(true)
                val transaction = supportFragmentManager.beginTransaction()
//                        val frg = supportFragmentManager.findFragmentByTag("item_list")
                val messageFrag = ChatListFragment()
                transaction.replace(R.id.frag_holder, messageFrag, "chat_list")
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                if(auth.currentUser != null){
                    toolbar.title = auth.currentUser!!.displayName
                    val newUser = mapOf(
                        "displayName" to auth.currentUser!!.displayName,
                        "mail" to auth.currentUser!!.email)
                    fireStoreUsers.add(newUser)
                        .addOnSuccessListener {
                            UserSingleton.getInstance(this).createSingleton(it.id, auth.currentUser!!.displayName!!)
                        }
                        .addOnFailureListener { e -> Log.e("ERROR", e.message) }

                    bottombar.menu.getItem(2).isEnabled = true
                }else{
                    toolbar.title = "Anonymous"
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }else if(requestCode == RC_LOG_IN){
            if(auth.currentUser != null){
                toolbar.title = auth.currentUser!!.displayName
                fireStoreUsers.whereEqualTo("mail", auth.currentUser!!.email).limit(1)
                    .get()
                    .addOnSuccessListener {
                        UserSingleton.getInstance(this).createSingleton(it.documents[0].id, auth.currentUser!!.displayName!!)
                    }
                    .addOnFailureListener {  e ->
                        Log.w("TAG", "Error adding document", e)
                    }

                bottombar.menu.getItem(2).isEnabled = true
            }else{
                toolbar.title = "Anonymous"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            // User chose the "Settings" item, show the app settings UI...
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Toast.makeText(baseContext, "user logout",
                        Toast.LENGTH_SHORT).show()
                        toolbar.title = "Anonymous"
                }
            bottombar.menu.getItem(2).isEnabled = false
            true
        }
        R.id.action_historic -> {
            if(auth.currentUser != null) {
                toolbar.title = "HISTORIC"
                toolbar.setDisplayHomeAsUpEnabled(true)
                val transaction = supportFragmentManager.beginTransaction()
                val frg = ItemListFragment()
                val args = Bundle()
                args.putString("action", HISTORIC)
                frg.arguments = args
                transaction.replace(R.id.frag_holder, frg, "historic_list")
                transaction.commit()
            }
            true
        }
        android.R.id.home ->{
            toolbar.title = auth.currentUser!!.displayName
            if(supportFragmentManager.findFragmentByTag("chat") != null){
                val transaction = supportFragmentManager.beginTransaction()
                val frg = ChatListFragment()
                transaction.replace(R.id.frag_holder, frg, "chat_list")
                transaction.commit()
            }else{
                toolbar.setDisplayHomeAsUpEnabled(false)
                val transaction = supportFragmentManager.beginTransaction()
                val frg = ItemListFragment()
                val args = Bundle()
                args.putString("action", SHOW_ALL)
                frg.arguments = args
                transaction.replace(R.id.frag_holder, frg, "item_list")
                transaction.commit()
            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}