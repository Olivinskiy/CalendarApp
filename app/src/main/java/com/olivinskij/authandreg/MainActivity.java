package com.olivinskij.authandreg;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.olivinskij.authandreg.UI.NotesAdapter;
import com.olivinskij.authandreg.UI.NotificationReceiver;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private HashMap<String, List<String>> notesMap;
    private String selectedDate;
    private ImageView addNoteButton;
    public ImageView imageView6;
    public TextView textView6, textView7;
    private ImageView bgadddate;
    public TextView noteTextView2;
    private EditText editTextEventName;
    private EditText editTextNote;
    private EditText editTextDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadNotes();




        imageView6 = findViewById(R.id.imageView6);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);

        bgadddate = findViewById(R.id.bgadddate);
        editTextEventName = findViewById(R.id.editTextText);
        editTextNote = findViewById(R.id.editTextText2);
        editTextDate = findViewById(R.id.editTextDate);

        findViewById(R.id.addNoteButton).setOnClickListener(v -> {
            showWithAnimation(bgadddate);
        });

        calendarView = findViewById(R.id.calendarView);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        addNoteButton = findViewById(R.id.addNoteButton);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(new ArrayList<>(), position -> {
            List<String> notes = notesMap.getOrDefault(selectedDate, new ArrayList<>());
            if (position >= 0 && position < notes.size()) {
                notes.remove(position);
                notesMap.put(selectedDate, notes);
                saveNotes();
                loadNotesForSelectedDate();
                Toast.makeText(MainActivity.this, "Заметка удалена", Toast.LENGTH_SHORT).show();
            }
        });
        notesRecyclerView.setAdapter(notesAdapter);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date.toString();
                loadNotesForSelectedDate();
                if (selected) {
                    addNoteButton.setVisibility(View.VISIBLE);
                } else {
                    addNoteButton.setVisibility(View.GONE);
                }
            }
        });

        selectedDate = CalendarDay.today().toString();
        calendarView.setSelectedDate(CalendarDay.today());

        findViewById(R.id.bgadddate).setOnClickListener(v -> saveNoteFromInput());
    }

    private void loadNotesForSelectedDate() {
        List<String> notes = notesMap.getOrDefault(selectedDate, new ArrayList<>());
        notesAdapter.updateNotes(notes);
    }

    private void saveNoteFromInput() {
        String eventName = editTextEventName.getText().toString().trim();
        String noteContent = editTextNote.getText().toString().trim();
        String time = editTextDate.getText().toString().trim();

        if (eventName.isEmpty() || noteContent.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullNote = time + " - " + eventName + " - " + noteContent;
        List<String> notes = notesMap.getOrDefault(selectedDate, new ArrayList<>());
        notes.add(fullNote);
        notesMap.put(selectedDate, notes);
        saveNotes();
        loadNotesForSelectedDate();
        scheduleNotification(time, fullNote);

        Toast.makeText(this, "Заметка добавлена", Toast.LENGTH_SHORT).show();
        hideADDelements();
        clearInputs();
    }


    private void clearInputs() {
        editTextEventName.setText("");
        editTextNote.setText("");
        editTextDate.setText("");
    }

    public void showADDelements()
    {
        bgadddate.setVisibility(View.VISIBLE);
        editTextEventName.setVisibility(View.VISIBLE);
        editTextNote.setVisibility(View.VISIBLE);
        editTextDate.setVisibility(View.VISIBLE);
        imageView6.setVisibility(View.VISIBLE);
        textView6.setVisibility(View.VISIBLE);
        textView7.setVisibility(View.VISIBLE);
    }

    public void hideADDelements()
    {
        bgadddate.setVisibility(View.GONE);
        editTextEventName.setVisibility(View.GONE);
        editTextNote.setVisibility(View.GONE);
        editTextDate.setVisibility(View.GONE);
        imageView6.setVisibility(View.GONE);
        textView6.setVisibility(View.GONE);
        textView7.setVisibility(View.GONE);
    }

    private void scheduleNotification(String time, String note) {
        String[] timeParts = time.split(":");

        if (timeParts.length != 2) {
            Toast.makeText(this, "Неверный формат времени. Используйте HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("note", note);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка в формате времени. Используйте HH:mm", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotesApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notesMap);
        editor.putString("notesMap", json);
        editor.apply();
    }

    private void showWithAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(view.getHeight());
        view.setAlpha(0f);

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", 0f);
        slideUp.setDuration(600);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 1f);
        fadeIn.setDuration(600);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideUp, fadeIn);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();

        showADDelements();
    }

    private void loadNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotesApp", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notesMap", null);
        if (json != null) {
            notesMap = gson.fromJson(json, HashMap.class);
        } else {
            notesMap = new HashMap<>();
        }
    }
}
