package com.example.androidgooglebooksapihilt.models.bookList

import java.io.Serializable

data class BookList(

    val kind : String,
    val totalItems : Int,
    var items : ArrayList<Items>
) : Serializable