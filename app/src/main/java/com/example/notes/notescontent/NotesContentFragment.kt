package com.example.notes.notescontent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.convertTo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.BottomSheetDialogBinding
import com.example.notes.databinding.FragmentNotesContentBinding
import com.example.notes.db.NotesDatabase
import com.example.notes.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotesContentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentNotesContentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_notes_content, container, false
        )

        val application = requireNotNull(this.activity).application
        val arguments = NotesContentFragmentArgs.fromBundle(requireArguments())
        val dataSource = NotesDatabase.getInstance(application).notesDao()

        val viewModel: NotesContentViewModel by viewModels {
            NotesContentViewModelFactory(
                arguments.Note,
                dataSource
            )
        }
//        val viewModelFactory = NotesContentViewModelFactory(arguments.Note, dataSource)
//        val viewModel = ViewModelProvider(this, viewModelFactory).get(NotesContentViewModel::class.java)

        binding.lifecycleOwner = this
        binding.notesContentViewModel = viewModel

        binding.toolbarSaveNoteButton.setOnClickListener {

            val sheetDialog = BottomSheetDialog(requireContext())
            val sheetLayout = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            with(sheetDialog) {
                setContentView(sheetLayout)
                show()
            }
            val sheetBinding = BottomSheetDialogBinding.bind(sheetLayout)

            sheetBinding.deleteItemSheet.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Note ?")
                    .setMessage("Deleted note cannot be recovered")
                    .setPositiveButton("Delete") { dialog, which ->
                        dialog.dismiss()
                        viewModel.deleteNote()
                    }
                    .setNeutralButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
                sheetDialog.dismiss()
            }

            sheetBinding.shareItemSheet.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, viewModel.note.value!!.noteTitle +"\n"+viewModel.note.value!!.noteText)
                    type = "plain/text"
                }
                startActivity(Intent.createChooser(shareIntent, null))
                sheetDialog.dismiss()
            }

            sheetBinding.colorPicker.setOnColorSelectedListener {
                viewModel.note.value!!.noteColor = it
            }

            viewModel.color.observe(viewLifecycleOwner, {
                sheetBinding.colorPicker.setSelectedColor(it)
            })

        }

        binding.toolbarBackButton.setOnClickListener {
            viewModel.saveNote()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.saveNote()
        }

        viewModel.navigateToNotes.observe(viewLifecycleOwner, {
            if (it == true) {
                hideKeyboard()
                findNavController().navigateUp()
                viewModel.doneNavigation()
            }
        })

        return binding.root

    }


//    fun View.hideKeyboard() =
//        (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
//            .hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)


}