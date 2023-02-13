@file:Suppress("DEPRECATION")

package com.example.imagesearch.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.imagesearch.R
import com.example.imagesearch.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val viewModel: GalleryViewModel by viewModels()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter()

        binding.apply {
            rvGallery.setHasFixedSize(true)
            rvGallery.adapter = adapter.withLoadStateHeaderAndFooter(
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
        }

        viewModel.subscribeToState { state ->
            if (!state.isLoading) {
                state.pagingData?.let {
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }

        setHasOptionsMenu(true)

        viewModel.triggerEvent(PagingDataEvent.GetInitialPhotos)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.rvGallery.scrollToPosition(0)
                    viewModel.triggerEvent(PagingDataEvent.SearchPhotos(query))
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}