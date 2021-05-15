package com.example.notes.notescontent

import android.util.Log
import androidx.lifecycle.*
import com.example.notes.db.Notes
import com.example.notes.db.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesContentViewModel(
    notes: Notes,
    private val database: NotesDao
) : ViewModel() {

    val note = MutableLiveData<Notes>()
    var isNewNote : Boolean

    var color: LiveData<Int> = Transformations.map(note) {
        it.noteColor
    }

    init {
        note.value = notes
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
                database.deleteNote(note.value!!)
            }
        }
        _navigateToNotes.value = true
    }

    fun saveNote() {
        viewModelScope.launch {
            if (isNewNote) {
                insert(note.value!!)
            } else {
                update(note.value!!)
            }
        }
        _navigateToNotes.value = true

    }



}