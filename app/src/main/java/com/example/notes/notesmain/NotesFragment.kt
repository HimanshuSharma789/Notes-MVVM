package com.example.notes.notesmain

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.databinding.FragmentNotesBinding
import com.example.notes.db.Notes
import com.example.notes.db.NotesDatabase
import com.example.notes.hideKeyboard
import com.google.android.material.transition.MaterialElevationScale

class NotesFragment : Fragment() {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_notes, container, false)

        val application = requireNotNull(this.activity).application
        val dbDao = NotesDatabase.getInstance(application).notesDao()
        val viewModelFactory = NotesViewModelFactory(dbDao)
        notesViewModel =
            ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)

        binding.notesViewModel = notesViewModel
        binding.lifecycleOwner = this


        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)


        adapter = NotesAdapter(NoteClickListener { view: View, note: Notes ->
            hideKeyboard()
            navigateToNotesContentFragment(view, note)
        })
        binding.recycleView.adapter = adapter


        changeAppTheme()
        notesObserver()
        onSearchActionListener()
        clearTextPressed()
        searchQueryChanged()

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.recycleView.smoothScrollToPosition(0)
            }
        })


        binding.fab.setOnClickListener {
            navigateToNotesContentFragment(it, Notes())
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }


    private fun navigateToNotesContentFragment(it: View, note: Notes) {
        val extras = FragmentNavigatorExtras(it to "shared_note_element_container")
        this.findNavController()
            .navigate(NotesFragmentDirections.actionNotesFragmentToNotesContentFragment(note),
                extras)
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
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty() || text.isBlank()) {
                notesObserver()
                binding.clearText.visibility = View.GONE
            } else {
                binding.clearText.visibility = View.VISIBLE
                notesViewModel.searchNotes("%$text%").observe(viewLifecycleOwner, {
                    if (it.isEmpty()) {
                        binding.emptyList.visibility = View.VISIBLE
                    } else {
                        binding.emptyList.visibility = View.GONE
                    }
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
            if (it.isEmpty()) {
                binding.emptyList.visibility = View.VISIBLE
            } else {
                binding.emptyList.visibility = View.GONE
            }
            adapter.submitList(it)
        })
    }

    private fun changeAppTheme() {
        binding.darkThemeImageView.setOnClickListener {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.darkThemeImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24)

                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.darkThemeImageView.setImageResource(R.drawable.ic_baseline_light_mode_24)
                }
            }
        }

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.darkThemeImageView.setImageResource(R.drawable.ic_baseline_light_mode_24)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.darkThemeImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24)
            }
        }

    }

}