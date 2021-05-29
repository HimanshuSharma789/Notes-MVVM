package com.example.notes.notesmain

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialElevationScale

class NotesFragment : Fragment(), NotesAdapter.NoteClickListener {

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


        adapter = NotesAdapter(this)
        binding.recycleView.adapter = adapter
        registerForContextMenu(binding.recycleView)


        changeAppTheme()
        notesObserver()
        onSearchActionListener()
        clearTextPressed()
        searchQueryChanged()



        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                binding.recycleView.layoutManager?.scrollToPosition(0)
            }


            /*override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                Toast.makeText(context, "$fromPosition to $toPosition", Toast.LENGTH_SHORT).show())
                binding.recycleView.layoutManager?.scrollToPosition(toPosition)
            }*/
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
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        when (sharedPref.getBoolean("night_mode", false)) {
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding.darkThemeImageView.setOnClickListener {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    with(sharedPref.edit()) {
                        putBoolean("night_mode", false)
                        apply()
                    }
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    with(sharedPref.edit()) {
                        putBoolean("night_mode", true)
                        apply()
                    }
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

    override fun onNoteClicked(view: View, note: Notes) {
        hideKeyboard()
        navigateToNotesContentFragment(view, note)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.note_context_menu, menu)
        menu.findItem(R.id.pin_menu_item).title = when (adapter.getItem()!!.isNotePin) {
            true -> "Unpin"
            false -> "Pin to Top"
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val note = adapter.getItem() ?: return super.onContextItemSelected(item)
        return when (item.itemId) {
            R.id.pin_menu_item -> {
                // Pin to Top
                note.isNotePin = note.isNotePin.not()
                notesViewModel.updateNote(note)
                adapter.notifyNoteChanged()
                true
            }
            R.id.delete_menu_item -> {
                // Delete
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Note ?")
                    .setMessage("Deleted note cannot be recovered")
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        notesViewModel.deleteNote(note)
                    }
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

}