package com.example.notes.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.databinding.AddOrEditNoteFragmentBinding
import com.example.notes.db.models.Note
import com.example.notes.ui.viewmodels.AddOrEditViewModel
import com.example.notes.util.SaveNoteState
import com.example.notes.util.hideKeyboard
import com.example.notes.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*


@AndroidEntryPoint
class AddOrEditNoteFragment: Fragment(R.layout.add_or_edit_note_fragment) {

    private var _binding: AddOrEditNoteFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddOrEditViewModel by viewModels()
    private val args: AddOrEditNoteFragmentArgs by navArgs()

    private lateinit var getContent: ActivityResultLauncher<Array<out String>>
    private var currentUri: Uri? = null
    private var currentNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                viewModel.setImgUri(it)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddOrEditNoteFragmentBinding.bind(view)


        setTextFields(args.title, args.text)
        setCurrentNote()

        args.imgUri?.let {
            currentUri = Uri.parse(it)
            binding.ivNoteImage.setImageURI(currentUri)
        }

        collectSaveNoteStatus()

        binding.btnSave.isVisible = binding.etTitle.text.trim().toString().isNotEmpty()

        binding.ivNoteImage.isVisible = currentUri != null
        binding.btnRemoveImg.isVisible = currentUri != null


        binding.btnRemoveImg.setOnClickListener {
            currentUri = null
            binding.ivNoteImage.isVisible = false
            binding.btnRemoveImg.isVisible = false
        }

        binding.etTitle.addTextChangedListener {
            binding.btnSave.isVisible = binding.etTitle.text.trim().toString().isNotEmpty()
        }

        binding.btnSave.setOnClickListener {
            requireActivity().hideKeyboard(requireView())
            val title = binding.etTitle.text.trim().toString()
            val text = binding.etText.text.trim().toString()

            val timestamp = if(args.date != 0L) {
                args.date
            } else {
                System.currentTimeMillis()
            }

            val id = if(args.id.isNotEmpty()) {
                args.id
            } else {
                UUID.randomUUID().toString()
            }


            val note = Note(title, text, timestamp, currentUri?.toString(), id = id)
            viewModel.saveNote(title, note)
        }

        binding.btnChoosePic.setOnClickListener {
            getContent.launch(arrayOf("image/*"))
        }

        collectImgUri()

        binding.btnShare.setOnClickListener {
            shareNoteImage()
        }


        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_addOrEditNoteFragment_to_homeScreenFragment)
            }
        })

    }




    private fun shareNoteImage() {
        currentUri?.let { uri ->
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "image/*"
            }, null)
            startActivity(share)
        } ?: snackbar(R.string.no_image_msg)

    }

    private fun collectSaveNoteStatus() {
        lifecycleScope.launchWhenStarted {
            viewModel.saveNoteStatus.collect { state ->
                when(state) {
                    is SaveNoteState.Success -> {
                        findNavController().navigate(R.id.action_addOrEditNoteFragment_to_homeScreenFragment)
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

    private fun collectImgUri() {
        lifecycleScope.launchWhenStarted {
            viewModel.imgUri.collect {
                it?.let { uri ->
                    currentUri = uri
                    binding.ivNoteImage.apply {
                        isVisible = true
                        setImageURI(currentUri!!)
                    }
                    binding.btnRemoveImg.isVisible = true
                }
            }
        }
    }

    private fun setTextFields(title: String, text: String? = null) {
        binding.etTitle.setText(title)
        text?.let {
            binding.etText.setText(it)
        }
    }

    private fun setCurrentNote() {
        if(args.id.isNotEmpty()) {
            currentNote = Note(args.title, args.text ?: "", args.date, args.imgUri, args.id)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}