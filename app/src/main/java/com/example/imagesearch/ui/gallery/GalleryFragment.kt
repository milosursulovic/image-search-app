package com.example.imagesearch.ui.gallery

import android.os.Bundle
import android.view.View
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}