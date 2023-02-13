@file:Suppress("DEPRECATION")

package com.example.imagesearch.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
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

        adapter.setOnItemClick { unsplashPhoto ->
            val action =
                GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(unsplashPhoto)
            findNavController().navigate(action)
        }

        binding.apply {
            rvGallery.setHasFixedSize(true)
            rvGallery.itemAnimator = null
            rvGallery.adapter = adapter.withLoadStateHeaderAndFooter(
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.subscribeToState { state ->
            if (!state.isLoading) {
                state.pagingData?.let {
                    adapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                pb.isVisible = loadState.source.refresh is LoadState.Loading
                rvGallery.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                tvError.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                    rvGallery.isVisible = false
                    tvEmpty.isVisible = true
                } else {
                    tvEmpty.isVisible = false
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