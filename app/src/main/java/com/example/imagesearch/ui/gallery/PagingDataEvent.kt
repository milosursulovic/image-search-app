package com.example.imagesearch.ui.gallery

sealed class PagingDataEvent {
    object GetInitialPhotos : PagingDataEvent()
    data class SearchPhotos(val query: String) : PagingDataEvent()
}