package com.example.notes.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.notes.R
import com.example.notes.databinding.HomeScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreenFragment: Fragment(R.layout.home_screen_fragment) {

    private var _binding: HomeScreenFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeScreenFragmentBinding.bind(view)




    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}