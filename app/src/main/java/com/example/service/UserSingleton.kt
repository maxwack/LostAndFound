package com.example.service

import android.content.Context


class UserSingleton private constructor(context: Context) {

    private var id: String? = null
    private var displayName: String? = null


    init {
        // Init using context argument
    }

    fun getId() : String? {
        return id
    }

    fun getDisplayName(): String? {
        return displayName
    }

    fun createSingleton(id:String, displayName: String){
        this.id = id
        this.displayName = displayName
    }
    companion object : SingletonHolder<UserSingleton, Context>(::UserSingleton)
}