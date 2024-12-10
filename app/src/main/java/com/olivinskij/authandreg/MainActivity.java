package com.olivinskij.authandreg;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olivinskij.authandreg.UI.NotesAdapter;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private HashMap<String, List<String>> notesMap;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
//        findViewById(R.id.addNoteButton).setOnClickListener(v -> showAddNoteDialog());

        // Настройка RecyclerView
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(new ArrayList<>());
        notesRecyclerView.setAdapter(notesAdapter);

        // Хранение заметок
        notesMap = new HashMap<>();

        // Обработка выбора даты
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date.toString();
                loadNotesForSelectedDate();
            }
        });



        selectedDate = CalendarDay.today().toString();
        calendarView.setSelectedDate(CalendarDay.today());
    }

    private void loadNotesForSelectedDate() {
        List<String> notes = notesMap.getOrDefault(selectedDate, new ArrayList<>());
        notesAdapter.updateNotes(notes);
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        builder.setView(dialogView);

        EditText noteEditText = dialogView.findViewById(R.id.noteEditText);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String note = noteEditText.getText().toString().trim();
            if (!note.isEmpty()) {
                List<String> notes = notesMap.getOrDefault(selectedDate, new ArrayList<>());
                notes.add(note);
                notesMap.put(selectedDate, notes);
                loadNotesForSelectedDate();
                Toast.makeText(this, "Заметка добавлена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Введите текст заметки", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }
}
