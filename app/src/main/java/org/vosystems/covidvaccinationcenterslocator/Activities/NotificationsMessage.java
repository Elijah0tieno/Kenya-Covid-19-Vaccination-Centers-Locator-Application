package org.vosystems.covidvaccinationcenterslocator.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.vosystems.covidvaccinationcenterslocator.R;

public class NotificationsMessage extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_message);

        textView = findViewById(R.id.tv_message);
        Bundle bundle = getIntent().getExtras();

        //call the data which is passed by another intent
        textView.setText(bundle.getString("message"));


    }
}