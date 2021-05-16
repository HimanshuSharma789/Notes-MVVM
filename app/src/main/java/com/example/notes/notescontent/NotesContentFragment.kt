package com.example.notes.notescontent

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.BottomSheetDialogBinding
import com.example.notes.databinding.FragmentNotesContentBinding
import com.example.notes.db.NotesDatabase
import com.example.notes.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform

class NotesContentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val binding: FragmentNotesContentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_notes_content, container, false
        )
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            scrimColor = Color.TRANSPARENT
        }

        val application = requireNotNull(this.activity).application
        val arguments = NotesContentFragmentArgs.fromBundle(requireArguments())
        val dataSource = NotesDatabase.getInstance(application).notesDao()

        val viewModel: NotesContentViewModel by viewModels {
            NotesContentViewModelFactory(
                arguments.note,
                dataSource
            )
        }

        binding.lifecycleOwner = this
        binding.notesContentViewModel = viewModel

        binding.toolbarNoteOptionButton.setOnClickListener {

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
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        viewModel.deleteNote()
                    }
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                sheetDialog.dismiss()
            }

            sheetBinding.shareItemSheet.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,
                        viewModel.note.value!!.noteTitle + "\n" + viewModel.note.value!!.noteText)
                    type = "plain/text"
                }
                startActivity(Intent.createChooser(shareIntent, null))
                sheetDialog.dismiss()
            }

            sheetBinding.colorPicker.setOnColorSelectedListener {
                viewModel.changeNoteColor(it)
                binding.noteContentLinearlayout.setBackgroundColor(it)
            }

            viewModel.note.observe(viewLifecycleOwner, {
                sheetBinding.colorPicker.setSelectedColor(it.noteColor)
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


}