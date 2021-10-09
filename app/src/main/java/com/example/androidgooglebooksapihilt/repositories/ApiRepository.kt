package com.example.androidgooglebooksapihilt.repositories

import com.example.androidgooglebooksapihilt.models.bookList.BookList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRepository {
    @GET("books/v1/volumes?")
    fun getBookList(@Query("q") title : String): Call<BookList>

}