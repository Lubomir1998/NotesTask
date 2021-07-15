package com.example.notes.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
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
import com.example.notes.util.SaveNoteState
import com.example.notes.util.snackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
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

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        setupRecyclerView()
        viewModel.getAllNotes()

        collectNotes()
        collectSaveNoteState()
        collectDeleteState()


        var searchJob: Job? = null
        binding.etSearchNote.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(SEARCH_DELAY)
                viewModel.searchNotes(it.toString())
            }
        }


        binding.btnAddNew.setOnClickListener {
            findNavController().navigate(
                HomeScreenFragmentDirections.actionHomeScreenFragmentToAddOrEditNoteFragment(
                    id = "",
                    text = null,
                    imgUri = null
                )
            )
        }


        noteAdapter.setOnNoteClickListener { note ->
            findNavController().navigate(
                HomeScreenFragmentDirections.actionHomeScreenFragmentToAddOrEditNoteFragment(
                    note.id,
                    note.title,
                    note.text,
                    note.timestamp,
                    note.imgUri
                )
            )
        }



        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

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
        }
    }


    @SuppressLint("SetTextI18n")
    private fun collectNotes() {
        lifecycleScope.launchWhenStarted {
            viewModel.notes.collect { state ->
                when(state) {
                    is HomeScreenViewModel.NotesState.Success -> {
                        binding.progressBar.isVisible = false

                        if (state.data.isNotEmpty()) {
                            binding.rvNotes.isVisible = true
                            binding.tvNoNotes.isVisible = false
                            noteAdapter.submitList(state.data)
                        } else {
                            binding.rvNotes.isVisible = false
                            binding.tvNoNotes.isVisible = true
                        }

                        binding.tvAllNotes.text = setBottomViewText(state.data.size)
                    }

                    is HomeScreenViewModel.NotesState.Loading -> {
                        binding.apply {
                            progressBar.isVisible = true
                            rvNotes.isVisible = false
                            tvNoNotes.isVisible = false
                            tvAllNotes.text = "${R.string.loading_notes}..."
                        }
                    }

                    else -> Unit
                }

            }
        }
    }

    private fun collectSaveNoteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.saveNoteStatus.collect { state ->
                when(state) {
                    is SaveNoteState.Success -> {
                        noteAdapter.submitList(state.notes)

                        binding.apply {
                            tvAllNotes.text = setBottomViewText(state.notes.size)
                            tvNoNotes.isVisible = state.notes.isEmpty()
                            rvNotes.isVisible = state.notes.isNotEmpty()
                        }
                        snackbar(R.string.note_saved)
                    }

                    is SaveNoteState.Error -> {
                        snackbar(state.message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun collectDeleteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.deleteNoteStatus.collect { state ->
                when(state) {
                    is HomeScreenViewModel.DeleteNoteState.Success -> {
                        noteAdapter.submitList(state.notes)

                        binding.apply {
                            tvAllNotes.text = setBottomViewText(state.notes.size)
                            tvNoNotes.isVisible = state.notes.isEmpty()
                            rvNotes.isVisible = state.notes.isNotEmpty()
                        }

                        Snackbar.make(requireView(), resources.getString(R.string.note_deleted), Snackbar.LENGTH_LONG)
                            .setAction(resources.getString(R.string.undo)) {
                                viewModel.saveNote(state.note)
                            }
                            .show()

                    }

                    is HomeScreenViewModel.DeleteNoteState.Error -> {
                        snackbar(state.message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setBottomViewText(listSize: Int): String {
        return when (listSize) {
            0 -> resources.getString(R.string.no_notes)
            1 -> "1 ${resources.getString(R.string.note)}"
            else -> "$listSize ${resources.getString(R.string.notes)}"
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