package com.example.androidgooglebooksapihilt.models.bookList

import java.io.Serializable

//Books
data class Items (

	val kind : String,
	val id : String,
	val etag : String,
	val selfLink : String,
	val volumeInfo : VolumeInfo,
	val saleInfo : SaleInfo,
	val accessInfo : AccessInfo
) : Serializable, Comparable<Items> {
	override fun compareTo(other: Items): Int {
		if(saleInfo.saleability == "FREE"){
			return -1;
		}
		return 0;
	}
}