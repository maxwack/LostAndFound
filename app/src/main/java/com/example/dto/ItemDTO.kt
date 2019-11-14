package com.example.dto

import java.io.Serializable

data class ItemDTO(val user : String,
                   val displayName : String = "",
                   val name : String,
                   val place : String = "",
                   val comment : String = "",
                   val reward : Boolean = false,
                   val registerDate : String,
                   val date : String,
                   val category: String = "",
                   val state : String,
                   var img : ByteArray? = null) :Serializable