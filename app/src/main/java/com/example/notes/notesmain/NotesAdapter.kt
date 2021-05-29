package com.example.notes.notesmain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.ListItemNotesBinding
import com.example.notes.db.Notes


class NotesAdapter(private val clickListener: NoteClickListener) :
    ListAdapter<Notes, NotesAdapter.NotesViewHolder>(NotesDiffCallBack()) {

    private var noteItem: Notes? = null
    private var notePosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
        holder.itemView.setOnLongClickListener {
            noteItem = item
            notePosition = position
            false
        }
    }

    fun getItem(): Notes? {
        return noteItem
    }

    fun notifyNoteChanged() {
        notifyItemChanged(notePosition)
    }


    class NotesViewHolder private constructor(private val binding: ListItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: NoteClickListener, item: Notes) {
            binding.note = item
            binding.root.transitionName = "recycler item ${item.noteId}"
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

    interface NoteClickListener {
        fun onNoteClicked(view: View, note: Notes)
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
