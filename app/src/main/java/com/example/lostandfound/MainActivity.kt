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


class MainActivity : AppCompatActivity(){

    companion object {
        const val REGISTER_FOUND = "found"
        const val REGISTER_LOST = "lost"
        const val RC_SIGN_IN = 200
    }

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build())
//        AuthUI.IdpConfig.PhoneBuilder().build(),
//        AuthUI.IdpConfig.GoogleBuilder().build(),
//        AuthUI.IdpConfig.FacebookBuilder().build(),
//        AuthUI.IdpConfig.TwitterBuilder().build())

    private lateinit var auth: FirebaseAuth

    var choice = arrayOf("I found something !", "I lost something !")
    var choiceUser = arrayOf("Log In", "Register")
    lateinit var toolbar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frag_holder, ItemListFragment())
        transaction.commit()

        toolbar = supportActionBar!!
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        auth = FirebaseAuth.getInstance()
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
                            startActivity(intent)
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
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
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