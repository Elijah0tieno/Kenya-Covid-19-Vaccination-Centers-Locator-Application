package org.vosystems.covidvaccinationcenterslocator.Authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import org.vosystems.covidvaccinationcenterslocator.Helpers.MyUtil;
import org.vosystems.covidvaccinationcenterslocator.Helpers.SessionManager;
import org.vosystems.covidvaccinationcenterslocator.MainActivity;
import org.vosystems.covidvaccinationcenterslocator.Models.User;
import org.vosystems.covidvaccinationcenterslocator.R;

public class SignIn extends AppCompatActivity {
    FirebaseAuth auth;
    String redirect_to;

    EditText email, password;
    MaterialButton signIn;

    TextView forgotPassword, signUp;

    SessionManager sessionManager;

    ProgressDialog progressDialog;
    AlertDialog.Builder alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseApp.initializeApp(SignIn.this);

        initApp();

    }

    public void initApp(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        progressDialog = new ProgressDialog(SignIn.this);
        alert = new AlertDialog.Builder(SignIn.this);

        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        initViews();
        initListeners();

    }

    public void initViews(){
        email = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        forgotPassword = findViewById(R.id.signInForgotPass);
        signUp = findViewById(R.id.tvGetStarted);

        signIn = findViewById(R.id.btnSignIn);

    }

    public void initListeners(){
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputsAndSignIn();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
    }

    public void validateInputsAndSignIn(){
        if (email.getText().toString().isEmpty()){
            Snackbar.make(signIn, "Please enter your email", Snackbar.LENGTH_LONG).show();
        }else if (password.getText().toString().isEmpty()){
            Snackbar.make(signIn, "Please enter your password", Snackbar.LENGTH_LONG).show();
        }else{
            progressDialog.show();
            auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        completeSignIn(progressDialog);
                    }else {
                        progressDialog.dismiss();
                        alert.setTitle("Authentication Failed");
                        alert.setMessage("Please check that you have provided the correct credentials and have an internet connection");
                        alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                            }
                        });
                        alert.setCancelable(false);
                        alert.show();
                    }
                }
                //TODO
                public void handleForgotPassword(){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View view = inflater.inflate(R.layout.dialog_data_entry,null);
                    builder.setView(view);
                    final EditText text_entry = view.findViewById(R.id.data_entry_item);

                    builder.setTitle("Reset Password");
                    builder.setView(view);

                    text_entry.setLines(1);
                    text_entry.setHint("Your email address");

                    builder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (text_entry.getText().toString().length() > 0){
                                final ProgressDialog dialog1 = new ProgressDialog(SignIn.this);
                                dialog1.setMessage("Please wait...");
                                dialog1.setCancelable(false);
                                dialog1.show();

                                auth.sendPasswordResetEmail(text_entry.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog1.dismiss();
                                        if (task.isSuccessful()){
                                            MyUtil.showAlert(SignIn.this, "A password reset email has been sent to "+text_entry.getText().toString(),"Email sent");
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(SignIn.this, "Invalid email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                }
            });
        }
    }

    public void handleForgotPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_data_entry,null);
        builder.setView(view);
        final EditText text_entry = view.findViewById(R.id.data_entry_item);

        builder.setTitle("Reset Password");
        builder.setView(view);

        text_entry.setLines(1);
        text_entry.setHint("Your email address");

        builder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (text_entry.getText().toString().length()>0){
                    final ProgressDialog dialog = new ProgressDialog(SignIn.this);
                    dialog.setMessage("Please wait....");
                    dialog.setCancelable(false);
                    dialog.show();

                    auth.sendPasswordResetEmail(text_entry.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                MyUtil.showAlert(SignIn.this,"A password reset email has been sent to "+text_entry.getText().toString(),"Email sent");
                            }else {
                                MyUtil.showAlert(SignIn.this, task.getException().getMessage(),"Error");
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignIn.this, "Invalid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setCancelable(false);
        builder.show();


    }

    public void completeSignIn(final ProgressDialog progressDialog){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            User saved_user = task.getResult().toObject(User.class);

                            if (user!=null){
                                if (!user.getDisplayName().equals(saved_user.getUsername())){
                                    UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(saved_user.getUsername()).build();
                                    user.updateProfile(updates);
                                }
                                MyUtil.saveUser(getSharedPreferences("auth", MODE_PRIVATE),saved_user.getUsername(),saved_user.getEmail());
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                                finish();
                            }else {
                                MyUtil.showAlert(SignIn.this, "Unable to fetch some data, try again after sometime","Sorry");

                            }
                        }else {
                            MyUtil.showAlert(SignIn.this,"Unable to fetch some data, check your internet connection and try again", "Sorry");
                        }
                    }
                });
    }
}