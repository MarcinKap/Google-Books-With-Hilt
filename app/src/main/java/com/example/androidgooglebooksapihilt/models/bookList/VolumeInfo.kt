package com.example.androidgooglebooksapihilt.models.bookList

data class VolumeInfo (

    val title : String,
    val subtitle : String,
    val authors : List<String>,
    val publisher : String,
    val publishedDate : String,
    val description : String,
    val industryIdentifiers : List<IndustryIdentifiers>,
    val readingModes : ReadingModes,
    val pageCount : Int,
    val printType : String,
    val categories : List<String>,
    val maturityRating : String,
    val allowAnonLogging : Boolean,
    val contentVersion : String,
    val panelizationSummary : PanelizationSummary,
    val imageLinks : ImageLinks,
    val language : String,
    val previewLink : String,
    val infoLink : String,
    val canonicalVolumeLink : String
)