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
    private var stateCallback: ((PagingDataState) -> Unit)? = null

    fun subscribeToState(stateCallback: (PagingDataState) -> Unit) {
        this.stateCallback = stateCallback
    }

    fun triggerEvent(event: PagingDataEvent) {
        when (event) {
            is PagingDataEvent.GetInitialPhotos -> getPhotos()
            is PagingDataEvent.SearchPhotos -> searchPhotos(event.query)
        }
    }

    private fun getPhotos() {
        viewModelScope.launch {
            currentQuery.collect { query ->
                state = state.copy(isLoading = true)
                stateCallback?.let { it(state) }
                repository.getSearchResults(query).collect { pagingData ->
                    photos = pagingData
                    state = state.copy(
                        isLoading = false,
                        pagingData = photos
                    )
                    stateCallback?.let { it(state) }
                }
            }
        }
    }

    private fun searchPhotos(query: String) {
        currentQuery.value = query
        getPhotos()
    }
}