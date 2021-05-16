package com.example.notes.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes_table")
data class Notes(
    @PrimaryKey(autoGenerate = true) var noteId: Int = 0,
    var noteTitle: String = "",
    var noteText: String = "",
    var noteColor: Int = -1,
) : Serializable