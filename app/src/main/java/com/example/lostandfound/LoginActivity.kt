package com.example.lostandfound

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.example.model.DatabaseHelper
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val mMaxWidth = (dm.widthPixels * 0.8).toInt()
        val mMaxHeight = (dm.heightPixels * 0.8).toInt()

        val styledAttributes = theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        styledAttributes.recycle()

        auth = FirebaseAuth.getInstance()
        window.setLayout(mMaxWidth, mMaxHeight)
    }


    fun signInOnClick(v: View){
        val emailText:AutoCompleteTextView = findViewById(R.id.email)
        val password:EditText = findViewById(R.id.password)

        auth.signInWithEmailAndPassword(emailText.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
//                                        updateUI(user)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                                        updateUI(null)
                }

                // ...
            }
    }

}
