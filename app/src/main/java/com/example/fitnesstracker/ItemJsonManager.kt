package com.example.fitnesstracker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object ItemJsonManager {
    private const val FILE_NAME = "item_data.json"

    fun saveItemListToJson(context: Context, itemList: List<Item>){
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(itemList)
        val file = File(context.filesDir, FILE_NAME)

        file.writeText(jsonString)
    }

    fun loadItemListFromJson(context: Context): List<Item>{
        val file = File(context.filesDir, FILE_NAME)
        return if(file.exists()){
            val jsonString = file.readText()
            Gson().fromJson(jsonString, Array<Item>::class.java).toList()
        }
        else{
            emptyList()
        }
    }
}