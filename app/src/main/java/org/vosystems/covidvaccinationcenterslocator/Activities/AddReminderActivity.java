package org.vosystems.covidvaccinationcenterslocator.Activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.vosystems.covidvaccinationcenterslocator.R;
import org.vosystems.covidvaccinationcenterslocator.Utils.AlarmBroadcast;
import org.vosystems.covidvaccinationcenterslocator.Utils.DbManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddReminderActivity extends AppCompatActivity {
    Button btnSubmit, btnDate, btnTime;
    EditText txtTitle;
    String notifyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        initViews();
        initListeners();
        

    }


    public void initViews(){
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        txtTitle = findViewById(R.id.editTitle);
    }

    public void initListeners(){
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //access the data from the input field
                String title = txtTitle.getText().toString().trim();
                String date = btnDate.getText().toString().trim();
                String time = btnTime.getText().toString().trim();

                if (title.isEmpty()){
                    Toast.makeText(AddReminderActivity.this, "Reminder title cannot be blank", Toast.LENGTH_SHORT).show();
                }else {
                    if (time.equals("time") || date.equals("date")){
                        Toast.makeText(AddReminderActivity.this, "Date and time fields must be filled", Toast.LENGTH_SHORT).show();
                    }else {
                        processInsert(title, date, time);
                    }
                }
            }
        });
    }

    private void processInsert(String title, String date, String time) {
        String result = new DbManager(this).addreminder(title, date, time);
        setAlarm(title, date, time);
        txtTitle.setText("");
        Toast.makeText(AddReminderActivity.this, result, Toast.LENGTH_SHORT).show();
    }
    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //temporary variable to store the alarm time
                notifyTime = i + ":" + i1;
                //set the button text as selected time
                btnTime.setText(FormatTime(i, i1));

            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        btnDate.setText(day + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    //converts the time into 12hr format and assigns am or pm
    public String FormatTime(int hour, int minute){
        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0){
            formattedMinute = "0" + minute;
        }else{
            formattedMinute = "" + minute;
        }

        if (hour == 0){
            time = "12" + ":" + formattedMinute + " AM";
        }else if (hour < 12){
            time = hour + ":" +  formattedMinute + " AM";
        }else if (hour == 12){
            time = "12" + ":" + formattedMinute + " PM";
        }else {
            int temp = hour -12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }

    private void setAlarm(String title, String date, String time) {
        //assign alarm manager object to set alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(AddReminderActivity.this, AlarmBroadcast.class);
        //send data to alarm class to create channel and notification
        intent.putExtra("Vaccine Reminder", title);
        intent.putExtra("time", date);
        intent.putExtra("date",time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = date + " " + notifyTime;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyyhh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            alarmManager.set(AlarmManager.RTC_WAKEUP, date1.getTime(),
                    pendingIntent);
            Toast.makeText(AddReminderActivity.this, "Alarm", Toast.LENGTH_SHORT).show();
        }catch (ParseException e){
            e.printStackTrace();
        }
        //called once setting alarm is complete
        Intent intentBack = new Intent(AddReminderActivity.this, Reminders.class);
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //proceed to reminders activity
        startActivity(intentBack);
    }



}