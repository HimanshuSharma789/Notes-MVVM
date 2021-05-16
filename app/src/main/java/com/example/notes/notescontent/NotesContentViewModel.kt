package com.example.notes.notescontent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.Notes
import com.example.notes.db.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesContentViewModel(
    notes: Notes,
    private val database: NotesDao,
) : ViewModel() {

    private val _note = MutableLiveData<Notes>()
    val note: LiveData<Notes>
        get() = _note

    private var isNewNote: Boolean


    init {
        _note.value = notes
        isNewNote = (notes.noteId == 0)
    }

    private val _navigateToNotes = MutableLiveData<Boolean?>()
    val navigateToNotes: LiveData<Boolean?>
        get() = _navigateToNotes

    fun doneNavigation() {
        _navigateToNotes.value = null
    }

    private suspend fun insert(note: Notes) {
        withContext(Dispatchers.IO) {
            database.addNote(note)
        }
    }

    private suspend fun update(note: Notes) {
        withContext(Dispatchers.IO) {
            database.updateNote(note)
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.deleteNote(_note.value!!)
            }
        }
        _navigateToNotes.value = true
    }

    fun saveNote() {
        if (!(_note.value!!.noteTitle.trim().isEmpty() && _note.value!!.noteText.trim()
                .isEmpty())
        ) {
            viewModelScope.launch {
                if (isNewNote) {
                    insert(_note.value!!)
                } else {
                    update(_note.value!!)
                }
            }
        }
        _navigateToNotes.value = true
    }

    fun changeNoteColor(newColor: Int) {
        _note.value!!.noteColor = newColor
    }


}