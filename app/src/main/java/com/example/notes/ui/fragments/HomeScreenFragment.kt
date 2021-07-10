package com.example.notes.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.R
import com.example.notes.adapters.NoteAdapter
import com.example.notes.databinding.HomeScreenFragmentBinding
import com.example.notes.ui.viewmodels.HomeScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreenFragment: Fragment(R.layout.home_screen_fragment) {

    private var _binding: HomeScreenFragmentBinding? = null
    private val binding get() = _binding!!

    private val noteAdapter = NoteAdapter()
    private val viewModel: HomeScreenViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeScreenFragmentBinding.bind(view)

        setupRecyclerView()
        subscribeToObservers()


    }





    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        viewModel.notes.observe(viewLifecycleOwner) { notesList ->
            if(notesList.isNotEmpty()) {
                binding.rvNotes.isVisible = true
                binding.tvNoNotes.isVisible = false
                noteAdapter.submitList(notesList)
            } else {
                binding.rvNotes.isVisible = false
                binding.tvNoNotes.isVisible = true
            }
            binding.tvAllNotes.text = "${notesList.size} Notes"
        }
    }

    private fun setupRecyclerView() {
        binding.rvNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}