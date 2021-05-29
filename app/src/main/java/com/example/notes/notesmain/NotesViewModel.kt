package com.example.notes.notesmain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.Notes
import com.example.notes.db.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesViewModel(private val database: NotesDao) : ViewModel() {

    val notes = database.getAllNotes()
    val search = MutableLiveData<String>()

    fun searchNotes(string: String) = database.searchNote(string)

    fun clearSearch() {
        search.value = ""
    }

    fun deleteNote(note: Notes) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.deleteNote(note)
            }
        }
    }

    fun updateNote(note: Notes) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.updateNote(note)
            }
        }
    }
}