package org.vosystems.covidvaccinationcenterslocator.Authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.vosystems.covidvaccinationcenterslocator.Helpers.MyUtil;
import org.vosystems.covidvaccinationcenterslocator.Helpers.SessionManager;
import org.vosystems.covidvaccinationcenterslocator.Models.User;
import org.vosystems.covidvaccinationcenterslocator.R;

public class SignUp extends AppCompatActivity {
    EditText username, email, password;
    MaterialButton btnSignup;
    TextView signIn;

    FirebaseAuth auth;
    SessionManager sessionManager;

    FirebaseFirestore db;

    ProgressDialog progressDialog;
    AlertDialog.Builder alert;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        preferences = getSharedPreferences("auth", MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        db = FirebaseFirestore.getInstance();

        initViews();
        initListeners();

        progressDialog = new ProgressDialog(SignUp.this);
        alert = new AlertDialog.Builder(SignUp.this);

    }

    private void initViews(){
        username = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);

        btnSignup = findViewById(R.id.btnSignUp);
        signIn = findViewById(R.id.tvSignIn);
    }

    private void initListeners(){
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().trim().isEmpty()){
                    showSnackbar("Username is required");
                }else if (email.getText().toString().trim().isEmpty()){
                    showSnackbar("Email is required");
                }else if (password.getText().toString().trim().isEmpty()){
                    showSnackbar("Password is required");
                }else {
                    final User user = new User();
                    user.setUsername(username.getText().toString());
                    user.setEmail(email.getText().toString());

                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    auth.createUserWithEmailAndPassword(user.getEmail(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.setMessage("Wrapping up...");
                                user.setUid(task.getResult().getUser().getUid());

                                db.collection("users").document(user.getUid()).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            MyUtil.saveUser(getSharedPreferences("auth", MODE_PRIVATE), user.getEmail(), user.getUsername());

                                            sessionManager.saveUserInfo(user.getEmail(), password.getText().toString(), user.getUsername());

                                            UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(user.getUsername()).build();

                                            progressDialog.setMessage("Please wait...");

                                            sessionManager.refresh();
                                            sessionManager.getCurrentUser().updateProfile(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        startActivity(new Intent(SignUp.this, SignIn.class));
                                                    }else{
                                                        showAlert("Error", "Something went wrong");
                                                    }
                                                }
                                            });
                                        }else {
                                            showAlert("Failed","An error occurred during the process. Try again after a moment");
                                        }
                                    }
                                });
                            }else {
                                showAlert("Sorry", task.getException().getMessage());
                            }
                        }
                    });

                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });
    }
    private void showSnackbar(String message){
        Snackbar.make(btnSignup,message,Snackbar.LENGTH_LONG).show();
    }

    public void showAlert(String title, String message){
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setCancelable(false);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        progressDialog.dismiss();
        alert.show();
    }
}