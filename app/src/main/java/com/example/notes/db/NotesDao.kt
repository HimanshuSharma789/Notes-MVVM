package com.example.notes.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNote(notes: Notes)

    @Update
    fun updateNote(note: Notes)

    @Query("SELECT * FROM NOTES_TABLE ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Notes>>

    @Query("SELECT * FROM NOTES_TABLE where noteTitle LIKE :query or noteText LIKE :query ORDER BY noteId DESC")
    fun searchNote(query: String): LiveData<List<Notes>>

    @Delete
    fun deleteNote(notes: Notes)
}