package com.example.dto

data class MessageDTO(val uid : String,
                      val sender : String,
                      val time : Long,
                      val text : String){

    constructor() :this("test","Me",0,"tetete")

}
