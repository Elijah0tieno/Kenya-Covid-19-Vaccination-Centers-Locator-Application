package org.vosystems.covidvaccinationcenterslocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.vosystems.covidvaccinationcenterslocator.Activities.LocationsActivity;
import org.vosystems.covidvaccinationcenterslocator.Activities.ProfileActivity;
import org.vosystems.covidvaccinationcenterslocator.Activities.ReminderActivity;

public class MainActivity extends AppCompatActivity {
    RelativeLayout vaxcenters, reminders, profile;
    ViewFlipper viewFlipper;
    MaterialButton btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initFlipper();
        initListeners();

    }

    public void initViews(){
        vaxcenters = findViewById(R.id.vaccinationCenters);
        reminders = findViewById(R.id.reminder);
        profile = findViewById(R.id.profile);

        btnSignOut = findViewById(R.id.btnSignOut);
        viewFlipper = findViewById(R.id.view_flipper);

    }

    public void initFlipper(){
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();
    }

    public void initListeners(){
        vaxcenters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationsActivity.class));
            }
        });

        reminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReminderActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
    }
}