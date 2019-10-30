package com.example.lostandfound

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.content.DialogInterface
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
import com.google.firebase.auth.UserProfileChangeRequest
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat


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
        if(auth.currentUser != null){
            toolbar.title = auth.currentUser!!.displayName
        }else{
            toolbar.title = "Anonymous"
        }


        toolbar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(resources,android.R.drawable.ic_menu_revert,null))

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


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
                    if(auth.currentUser != null){
                        toolbar.title = auth.currentUser!!.displayName
                    }else{
                        toolbar.title = "Anonymous"
                    }
                }
            true
        }
        R.id.action_historic -> {
            toolbar.title = "HISTORIC"
            toolbar.setDisplayHomeAsUpEnabled(true)
            val transaction = supportFragmentManager.beginTransaction()
            val frg = supportFragmentManager.findFragmentByTag("item_list")
            transaction.detach(frg!!)
            val args = Bundle()
            args.putString("action", HISTORIC)
            args.putString("user", auth.currentUser!!.uid)
            frg.arguments = args
            transaction.attach(frg)
            transaction.commit()
            true
        }
        android.R.id.home ->{
            toolbar.title = auth.currentUser!!.displayName
            toolbar.setDisplayHomeAsUpEnabled(false)
            val transaction = supportFragmentManager.beginTransaction()
            val frg = supportFragmentManager.findFragmentByTag("item_list")
            transaction.detach(frg!!)
            val args = Bundle()
            args.putString("action", SHOW_ALL)
            frg.arguments = args
            transaction.attach(frg)
            transaction.commit()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}