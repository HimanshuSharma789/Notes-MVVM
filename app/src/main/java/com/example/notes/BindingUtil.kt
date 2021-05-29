package com.example.notes

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.notes.db.Notes
import com.google.android.material.card.MaterialCardView

@BindingAdapter("noteTitle")
fun TextView.setNoteTitle(item: Notes?) {
    item?.let {
        when (it.noteTitle.isEmpty()) {
            true -> {
                visibility = View.GONE
            }
            false -> {
                text = item.noteTitle
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("noteText")
fun TextView.setNoteText(item: Notes?) {
    item?.let {
        when (it.noteText.isEmpty()) {
            true -> visibility = View.GONE
            false -> {
                text = item.noteText
                visibility = View.VISIBLE
            }
        }
    }
}

@BindingAdapter("setNoteColor")
fun View.setNoteColor(color: Int?) {
    color?.let {
        setBackgroundColor(color)
    }
}

@BindingAdapter("noteColor")
fun MaterialCardView.setBackColor(item: Notes?) {
    item?.let {
        setCardBackgroundColor(item.noteColor)
    }
}

@BindingAdapter("isNotePin")
fun ImageView.isNotePin(item: Notes?) {
    item?.let {
        visibility = when (it.isNotePin) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }
}
