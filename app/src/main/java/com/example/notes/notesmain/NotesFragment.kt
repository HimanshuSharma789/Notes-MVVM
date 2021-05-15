package com.example.notes.notesmain

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.R
import com.example.notes.databinding.FragmentNotesBinding
import com.example.notes.db.Notes
import com.example.notes.db.NotesDatabase
import com.example.notes.hideKeyboard

class NotesFragment : Fragment() {


    private lateinit var binding: FragmentNotesBinding
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_notes, container, false)

        val application = requireNotNull(this.activity).application
        val dbDao = NotesDatabase.getInstance(application).notesDao()
        val viewModelFactory = NotesViewModelFactory(dbDao)
        notesViewModel =
            ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)

        binding.notesViewModel = notesViewModel
        binding.lifecycleOwner = this

        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recycleView.layoutManager = manager
        adapter = NotesAdapter(NoteClickListener { note: Notes ->
            notesViewModel.onNoteClicked(note)
        })
        binding.recycleView.adapter = adapter


        notesObserver()
        onSearchActionListener()
        clearTextPressed()
        searchQueryChanged()



        // TODO combine nav controller
        binding.fab.setOnClickListener {
            this.findNavController()
                .navigate(NotesFragmentDirections.actionNotesFragmentToNotesContentFragment(Notes()))
            notesViewModel.onNoteContentNavigated()
        }


        notesViewModel.navigateToNotesContent.observe(viewLifecycleOwner, {
            it?.let {
                hideKeyboard()
                this.findNavController()
                    .navigate(NotesFragmentDirections.actionNotesFragmentToNotesContentFragment(it))
                notesViewModel.onNoteContentNavigated()
            }
        })

        return binding.root
    }


    private fun onSearchActionListener() {
        binding.searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                hideKeyboard()
            }
            return@setOnEditorActionListener true
        }
    }


    private fun searchQueryChanged() {
        // notesViewModel.search.observe(viewLifecycleOwner, Observer { text->
        binding.searchEditText.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty() || text.isBlank()) {
                notesObserver()
                binding.clearText.visibility = View.GONE
            } else {
                binding.clearText.visibility = View.VISIBLE
                notesViewModel.searchNotes("%$text%").observe(viewLifecycleOwner, {
                    adapter.submitList(it)
                })
            }
        }
    }


    private fun clearTextPressed() {
        binding.clearText.setOnClickListener {
            binding.searchEditText.apply {
                text.clear()
                clearFocus()
            }
            hideKeyboard()
        }
    }


    private fun notesObserver() {
        notesViewModel.notes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

}