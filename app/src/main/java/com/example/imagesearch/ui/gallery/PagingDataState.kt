package com.example.imagesearch.ui.gallery

import androidx.paging.PagingData
import com.example.imagesearch.data.UnsplashPhoto

data class PagingDataState(
    val isLoading: Boolean = false,
    val pagingData: PagingData<UnsplashPhoto>? = null
)