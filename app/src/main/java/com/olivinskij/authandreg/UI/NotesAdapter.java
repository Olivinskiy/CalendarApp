package com.olivinskij.authandreg.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.olivinskij.authandreg.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<String> notes;
    private final OnNoteDeleteListener deleteListener;

    public interface OnNoteDeleteListener {
        void onDelete(int position);
    }

    public NotesAdapter(List<String> notes, OnNoteDeleteListener deleteListener) {
        this.notes = notes;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String note = notes.get(position);

        // Разделение заметки на части
        String[] parts = note.split(" - ", 3);
        if (parts.length == 3) {
            holder.timeTextView.setText(parts[0]); // Время
            holder.titleTextView.setText(parts[1]); // Название события
            holder.descriptionTextView.setText(parts[2]); // Описание
        } else {
            holder.timeTextView.setText("Неизвестно");
            holder.titleTextView.setText(note);
            holder.descriptionTextView.setText("");
        }

        // Установка слушателя для удаления
        holder.deleteTextView.setOnClickListener(v -> deleteListener.onDelete(position));

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateNotes(List<String> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView deleteTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.noteTextView2);
            titleTextView = itemView.findViewById(R.id.noteTextView);
            descriptionTextView = itemView.findViewById(R.id.noteTextView3);
            deleteTextView = itemView.findViewById(R.id.deleteTextView);
        }
    }
}
