package com.example.notes

import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.notes.db.Notes
import com.google.android.material.card.MaterialCardView

@BindingAdapter("noteTitle")
fun TextView.setNoteTitle(item: Notes?) {
    item?.let {
        text = item.noteTitle
    }
}

@BindingAdapter("noteText")
fun TextView.setNoteText(item: Notes?) {
    item?.let {
        text = item.noteText
    }
}

@BindingAdapter("noteColor")
fun MaterialCardView.setBackColor(item: Notes?) {
    item?.let {
        setCardBackgroundColor(item.noteColor)
    }
}
