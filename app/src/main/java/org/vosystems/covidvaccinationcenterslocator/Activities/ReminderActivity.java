package org.vosystems.covidvaccinationcenterslocator.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.vosystems.covidvaccinationcenterslocator.Adapters.ReminderAdapter;
import org.vosystems.covidvaccinationcenterslocator.Models.Reminder;
import org.vosystems.covidvaccinationcenterslocator.R;
import org.vosystems.covidvaccinationcenterslocator.Utils.DbManager;

import java.util.ArrayList;

public class ReminderActivity extends AppCompatActivity {
    FloatingActionButton btnCreateReminder;
    RecyclerView reminderViews;
    ArrayList<Reminder> dataholder = new ArrayList<Reminder>();
    ReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        initViews();
        initListeners();

    }

    public void initViews(){
        btnCreateReminder = findViewById(R.id.btnAddReminder);
        reminderViews = findViewById(R.id.reminderRecycler);
        reminderViews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    public void initListeners(){
        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderActivity.this, AddReminderActivity.class);
                startActivity(intent);
            }
        });

        Cursor cursor = new DbManager(getApplicationContext()).readallreminders();
        //Cursor to fetch data from db
        while (cursor.moveToNext()){
            Reminder reminder = new Reminder(cursor.getString(1),
                    cursor.getString(2), cursor.getString(3));
            dataholder.add(reminder);
        }

        reminderAdapter  = new ReminderAdapter(dataholder);
        reminderViews.setAdapter(reminderAdapter);

    }



    @Override
    public void onBackPressed() {
        finish();
        //makes user to exit the app
        super.onBackPressed();
    }
}