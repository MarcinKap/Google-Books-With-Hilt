package com.example.androidgooglebooksapihilt.views.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidgooglebooksapihilt.models.bookList.BookList
import com.example.androidgooglebooksapihilt.services.RetrofitService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BooksListViewModel @Inject constructor(
    private val retrofitService: RetrofitService
) : ViewModel() {

    private var mutableLiveDataBookList: MutableLiveData<BookList> = MutableLiveData()
    private var mutableLiveDataFreeBookListSize: MutableLiveData<Int> = MutableLiveData()
    private var mutableLiveDataPaidBookListSize: MutableLiveData<Int> = MutableLiveData()


    fun getMutableLiveDataObserver(): LiveData<BookList> {
        return mutableLiveDataBookList
    }
    fun getMutableLiveDataFreeBookListSizeObserver(): LiveData<Int> {
        return mutableLiveDataFreeBookListSize
    }
    fun getMutableLiveDataPaidBookListSizeObserver(): LiveData<Int> {
        return mutableLiveDataPaidBookListSize
    }



    fun loadList(query: String) {
        retrofitService.getBookListFromServer(query, mutableLiveDataBookList)

        countFreeBookListSize()
        countPaidBookListSize()

    }

    private fun countFreeBookListSize() {
        val freeBooks = mutableLiveDataBookList.value?.items?.filter{ it.saleInfo.saleability == "FREE"}?.size ?: 0

        mutableLiveDataFreeBookListSize.postValue(
            freeBooks
        )
    }

    private fun countPaidBookListSize() {
        mutableLiveDataPaidBookListSize.postValue(mutableLiveDataFreeBookListSize.value?.let {
            (mutableLiveDataBookList.value?.items?.size)?.minus(
                it
            )
        })
    }




}