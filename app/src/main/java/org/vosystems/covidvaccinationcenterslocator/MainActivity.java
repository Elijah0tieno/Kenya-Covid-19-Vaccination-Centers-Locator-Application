package org.vosystems.covidvaccinationcenterslocator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.vosystems.covidvaccinationcenterslocator.Activities.AddReminderActivity;
import org.vosystems.covidvaccinationcenterslocator.Activities.LocationsActivity;
import org.vosystems.covidvaccinationcenterslocator.Activities.ReminderActivity;
import org.vosystems.covidvaccinationcenterslocator.Authentication.SignIn;
import org.vosystems.covidvaccinationcenterslocator.Helpers.SessionManager;

public class MainActivity extends AppCompatActivity {
    RelativeLayout vaxcenters, addReminder, viewReminders;
    ViewFlipper viewFlipper;
    MaterialButton btnSignOut;

    private SharedPreferences preferences;
    SessionManager sessionManager;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FirebaseApp.initializeApp(MainActivity.this);

        sessionManager = new SessionManager(this);
        user = sessionManager.getCurrentUser();
        preferences = getSharedPreferences("auth",MODE_PRIVATE);

        initViews();
        initFlipper();
        initListeners();
    }

    public void initViews(){
        vaxcenters = findViewById(R.id.vaccinationCenters);
        addReminder = findViewById(R.id.reminder);
        viewReminders = findViewById(R.id.view_reminders);
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

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddReminderActivity.class));
            }
        });

        viewReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReminderActivity.class));
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public void signOut(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(MainActivity.this, SignIn.class));
    }
}