package com.example.notes.notesmain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.ListItemNotesBinding
import com.example.notes.db.Notes
import com.example.notes.setBackColor

class NotesAdapter(val clickListener: NoteClickListener) : ListAdapter<Notes, NotesAdapter.NotesViewHolder>(NotesDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    class NotesViewHolder private constructor(val binding: ListItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: NoteClickListener, item: Notes) {
            binding.note = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): NotesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemNotesBinding.inflate(layoutInflater, parent, false)
                return NotesViewHolder(binding)
            }
        }
    }

}


class NotesDiffCallBack : DiffUtil.ItemCallback<Notes>() {
    override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
        return oldItem == newItem
    }
}

class NoteClickListener(val clickListener: (note: Notes) -> Unit) {
    fun onClick(note: Notes) = clickListener(note)
}