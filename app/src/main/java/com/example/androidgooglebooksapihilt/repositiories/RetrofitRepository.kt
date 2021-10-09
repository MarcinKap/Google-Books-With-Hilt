package com.example.androidgooglebooksapihilt.repositiories

import com.example.androidgooglebooksapihilt.models.bookList.BookList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitRepository {

    @GET("books/v1/volumes?")
    fun getBookList(@Query("q") title : String): Call<BookList>

}