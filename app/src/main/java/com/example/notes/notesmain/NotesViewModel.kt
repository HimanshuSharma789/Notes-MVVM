package com.example.notes.notesmain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.db.NotesDao

class NotesViewModel(private val database: NotesDao) : ViewModel() {

    val notes = database.getAllNotes()
    val search = MutableLiveData<String>()

    fun searchNotes(string: String) = database.searchNote(string)

    fun clearSearch() {
        search.value = ""
    }

}