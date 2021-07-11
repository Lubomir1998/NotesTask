package com.example.notes.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.adapters.NoteAdapter
import com.example.notes.databinding.HomeScreenFragmentBinding
import com.example.notes.ui.viewmodels.HomeScreenViewModel
import com.example.notes.util.Constants.SEARCH_DELAY
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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


        var searchJob: Job? = null
        binding.etSearchNote.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(SEARCH_DELAY)
                viewModel.searchNotes(it.toString())
                subscribeToObservers()
            }
        }


        binding.btnAddNew.setOnClickListener {
            findNavController().navigate(
                HomeScreenFragmentDirections.actionHomeScreenFragmentToAddOrEditNoteFragment(
                    id = "",
                    text = null
                )
            )
        }


        noteAdapter.setOnNoteClickListener { note ->
            findNavController().navigate(
                HomeScreenFragmentDirections.actionHomeScreenFragmentToAddOrEditNoteFragment(
                    note.id,
                    note.title,
                    note.text,
                    note.timestamp
                )
            )
        }




    }



    private val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.currentList[position]
            viewModel.deleteNote(note)

            Snackbar.make(requireView(), "Note deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    viewModel.saveNote(note)
                }
                .show()
        }
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

            binding.tvAllNotes.text = when (notesList.size) {
                0 -> "No notes"
                1 -> "1 Note"
                else -> "${notesList.size} Notes"
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvNotes.apply {
            adapter = noteAdapter
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}