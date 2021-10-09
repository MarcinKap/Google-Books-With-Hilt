package com.example.androidgooglebooksapihilt.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.MutableLiveData

import com.example.androidgooglebooksapihilt.models.bookList.BookList
import com.example.androidgooglebooksapihilt.repositiories.RetrofitRepository
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.util.*

class RetrofitService @Inject constructor(
    private val retrofitRepository: RetrofitRepository
) {

    fun getBookListFromServer(query: String, mutableLiveDataBookList: MutableLiveData<BookList>){
        val call: Call<BookList> = retrofitRepository.getBookList(query)
        call.enqueue(object : Callback<BookList>{
            override fun onResponse(call: Call<BookList>, response: Response<BookList>) {

                var bookList = response.body()
//

                mutableLiveDataBookList.postValue(bookList)
            }

            override fun onFailure(call: Call<BookList>, t: Throwable) {
                mutableLiveDataBookList.postValue(null)

//                if (!isNetworkAvailable(view!!.context)) {
//                    Toast.makeText(
//                        context,
//                        resources.getString(R.string.not_connected),
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
//                } else {
//                    Toast.makeText(
//                        context,
//                        resources.getString(R.string.problem_with_download_data),
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
//                }


            }

        })
    }


    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }



}