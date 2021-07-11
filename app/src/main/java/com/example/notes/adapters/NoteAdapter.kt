package com.example.notes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.NoteItemBinding
import com.example.notes.db.models.Note
import com.example.notes.util.formatDate

class NoteAdapter: ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallBack()) {

    class NoteViewHolder(itemView: NoteItemBinding): RecyclerView.ViewHolder(itemView.root) {
        val tvTitle = itemView.tvTitle
        val tvDate = itemView.tvDate
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)

        holder.apply {
            tvTitle.text = note.title
            tvDate.text = formatDate(note.timestamp)

            itemView.setOnClickListener {
                onNoteClickListener?.let { click ->
                    click(note)
                }
            }
        }

    }


    private var onNoteClickListener: ((Note) -> Unit)? = null

    fun setOnNoteClickListener(listener: ((Note) -> Unit)) {
        onNoteClickListener = listener
    }


    class NoteDiffCallBack: DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

}