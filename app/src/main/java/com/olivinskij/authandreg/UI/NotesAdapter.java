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

    public NotesAdapter(List<String> notes, OnNoteDeleteListener deleteListener) {
        this.notes = notes;
        this.deleteListener = deleteListener;
    }

    public void updateNotes(List<String> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String note = notes.get(position);
        holder.noteTextView.setText(note);

        holder.deleteTextView.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onNoteDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTextView;
        TextView deleteTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
            deleteTextView = itemView.findViewById(R.id.deleteTextView);
        }
    }

    public interface OnNoteDeleteListener {
        void onNoteDelete(int position);
    }
}
