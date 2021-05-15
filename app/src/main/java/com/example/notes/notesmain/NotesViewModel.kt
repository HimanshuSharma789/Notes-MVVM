package com.example.notes.notesmain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.notes.db.Notes
import com.example.notes.db.NotesDao

class NotesViewModel(private val database: NotesDao) : ViewModel() {

    val notes = database.getAllNotes()
    val search = MutableLiveData<String>()

    fun searchNotes(string: String) = database.searchNote(string)

    private val _navigateToNotesContent = MutableLiveData<Notes?>()
    val navigateToNotesContent
        get() = _navigateToNotesContent

    fun onNoteClicked(note: Notes) {
        _navigateToNotesContent.value = note
    }

    fun onNoteContentNavigated() {
        _navigateToNotesContent.value = null
    }

    fun clearSearch() {
        search.value = ""
    }

}