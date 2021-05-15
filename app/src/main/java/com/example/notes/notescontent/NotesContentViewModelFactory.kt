package com.example.notes.notescontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.db.Notes
import com.example.notes.db.NotesDao
import java.lang.IllegalArgumentException

class NotesContentViewModelFactory(
    private val note: Notes,
    private val dataSource: NotesDao): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NotesContentViewModel::class.java)) {
            return NotesContentViewModel(note, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}