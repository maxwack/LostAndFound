package com.example.service

import com.google.firebase.auth.FirebaseAuth

class Util {

    companion object {
        fun isLogin(): Boolean
        {
            return FirebaseAuth.getInstance().currentUser != null
        }
    }
}