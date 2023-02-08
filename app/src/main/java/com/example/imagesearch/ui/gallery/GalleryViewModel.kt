package com.example.imagesearch.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.imagesearch.data.UnsplashPhoto
import com.example.imagesearch.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val repository: UnsplashRepository) :
    ViewModel() {

    companion object {
        private const val DEFAULT_QUERY = "cats"
    }

    private val currentQuery = MutableStateFlow(DEFAULT_QUERY)
    private var photos: PagingData<UnsplashPhoto>? = null
    private var state = PagingDataState()

    init {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            currentQuery.collect { query ->
                repository.getSearchResults(query).collect { pagingData ->
                    photos = pagingData
                    state = state.copy(
                        isLoading = false,
                        pagingData = photos
                    )
                }
            }
        }
    }

    fun subscribeToState(callback: (PagingDataState) -> Unit) {
        callback(state)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }
}

data class PagingDataState(
    val isLoading: Boolean = false,
    val pagingData: PagingData<UnsplashPhoto>? = null
)