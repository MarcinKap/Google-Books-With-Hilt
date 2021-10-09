package com.example.androidgooglebooksapihilt.views.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ViewPagerViewModel @Inject constructor() : ViewModel() {


    private var bookPositionOnDownloadedList: MutableLiveData<Int> = MutableLiveData(0)
    private var bookPositionInRecyclerView: MutableLiveData<Int> = MutableLiveData(0)




    fun setBookPositionOnDownloadedList(value: Int){
        bookPositionOnDownloadedList.postValue(value)
    }
    fun getBookPositionOnDownloadedList() : LiveData<Int> {
        return bookPositionOnDownloadedList
    }

    fun setBookPositionInRecyclerView(value: Int){
        bookPositionInRecyclerView.postValue(value)
    }
    fun getBookPositionInRecyclerView() : LiveData<Int> {
        return bookPositionInRecyclerView
    }


}