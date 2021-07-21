package com.example.notes.ui.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.BuildConfig
import com.example.notes.R
import com.example.notes.databinding.AddOrEditNoteFragmentBinding
import com.example.notes.db.models.Note
import com.example.notes.ui.viewmodels.AddOrEditViewModel
import com.example.notes.util.Constants.PERMISSION_REQUEST_CODE
import com.example.notes.util.SaveNoteState
import com.example.notes.util.hideKeyboard
import com.example.notes.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
                val contentResolver = requireActivity().contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                contentResolver.takePersistableUriPermission(it, takeFlags)
                viewModel.setImgUri(it)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddOrEditNoteFragmentBinding.bind(view)


        setCurrentNote()
        setTextFields(args.title, args.text)
        setImageVisibility(args.imgUri)

        collectImgUri()
        collectSaveNoteStatus()
        collectUpdateNoteImageStatus()


        binding.btnSave.isVisible = binding.etTitle.text.trim().toString().isNotEmpty()


        binding.btnRemoveImg.setOnClickListener {
            viewModel.setImgUri("".toUri())
            binding.ivNoteImage.isVisible = false
            binding.btnRemoveImg.isVisible = false
        }

        binding.etTitle.addTextChangedListener {
            binding.btnSave.isVisible = binding.etTitle.text.trim().toString().isNotEmpty()
        }

        binding.btnSave.setOnClickListener {
            saveNote()
        }

        binding.btnChoosePic.setOnClickListener {
            getContent.launch(arrayOf("image/*"))
        }

        if(!checkPermission()) {
            requestPermission()
        }

        binding.btnShare.setOnClickListener {
            if (checkPermission()) {
                shareNoteImage()
            } else {
                requestPermission()
            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    // the callback is alive as long as the fragment is alive
    private val callback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            saveNote()
            findNavController().navigate(R.id.action_addOrEditNoteFragment_to_homeScreenFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        callback.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        callback.isEnabled = true
    }



    private fun saveNote() {
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

        val imgUri = if(currentUri != null && currentUri.toString().isNotEmpty()) {
            currentUri.toString()
        } else {
            null
        }

        val note = Note(title, text, timestamp, imgUri, id = id)
        viewModel.saveNote(title, note)
    }

    private fun uriFromFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun shareNoteImage() {
        currentNote?.let { note ->

            val pdfDocument = PdfDocument()
            val paint = Paint()
            val title = Paint()

            val myPageInfo = PageInfo.Builder(792, 1120, 1).create()
            val myPage = pdfDocument.startPage(myPageInfo)
            val canvas: Canvas = myPage.canvas

            title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            title.textSize = 15f

            title.color = ContextCompat.getColor(requireContext(), R.color.note_text)
            canvas.drawText(note.title, 209F, 100f, title)
            canvas.drawText(note.text, 209f, 100f, title)


            note.imgUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.parse(it))
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 140, 140, false)
                canvas.drawBitmap(scaledBitmap, 56f, 40f, paint)
            }


            pdfDocument.finishPage(myPage)

            val file = File(Environment.getExternalStorageDirectory(), "${note.title}.pdf")

            try {
                pdfDocument.writeTo(FileOutputStream(file))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pdfDocument.close()


            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uriFromFile(requireContext(), file))
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "application/pdf"
            }, null)
            startActivity(share)
        } ?: snackbar(R.string.no_note)

    }


    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {

                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    snackbar(R.string.permission_granted)
                } else {
                    snackbar(R.string.permission_denied)
                    requireActivity().finish()
                }
            }
        }
    }









    private fun collectSaveNoteStatus() {
        lifecycleScope.launchWhenStarted {
            viewModel.saveNoteStatus.collect { state ->
                when(state) {
                    is SaveNoteState.Success -> {
                        currentNote = state.note
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
        viewModel.imgUri.observe(viewLifecycleOwner) { uri ->
            currentUri = if(uri.toString().isNotEmpty()) {
                uri
            } else {
                null
            }

            setImageVisibility(currentUri?.toString())

            // when the image is changed or removed
            // it automatically updates the Note object
            currentNote?.let { note ->
                note.imgUri = if(currentUri == null) {
                    null
                } else {
                    currentUri.toString()
                }
                if(binding.etTitle.text.toString().isNotEmpty()) {
                    viewModel.updateNotesImage(note)
                }
            }


        }

    }

    private fun collectUpdateNoteImageStatus() {
        lifecycleScope.launchWhenStarted {
            viewModel.updateNoteImageStatus.collect { state ->
                when(state) {
                    is AddOrEditViewModel.UpdateNoteImageState.Success -> {
                        currentNote = state.note
                    }

                    is AddOrEditViewModel.UpdateNoteImageState.Error -> {
                        snackbar(state.message)
                    }

                    else -> Unit
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

    private fun setImageVisibility(uri: String?) {
        binding.apply {
            ivNoteImage.isVisible = uri != null && uri.isNotEmpty()
            btnRemoveImg.isVisible = uri != null && uri.isNotEmpty()
        }
        uri?.let {
            binding.ivNoteImage.setImageURI(Uri.parse(it))
        }
    }

    private fun setCurrentNote() {
        if(args.id.isNotEmpty()) {
            currentNote = Note(args.title, args.text ?: "", args.date, args.imgUri, args.id)
            args.imgUri?.let {
                currentUri = Uri.parse(it)
                setImageVisibility(it)
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}