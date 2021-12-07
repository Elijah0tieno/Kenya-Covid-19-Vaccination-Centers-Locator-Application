package org.vosystems.covidvaccinationcenterslocator.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Context context;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private ProgressDialog dialog;
    private SharedPreferences preferences;



    public SessionManager(Context context) {
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        this.firebaseUser = auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        this.dialog = new ProgressDialog(context);
        this.preferences = context.getSharedPreferences("auth",Context.MODE_PRIVATE);
        dialog.setCancelable(false);
    }

    public void refresh(){this.firebaseUser = auth.getCurrentUser();}

    public boolean sessionExists(){
        if (firebaseUser!=null){
            return true;
        }
        return false;
    }

    public FirebaseUser getCurrentUser(){
        return firebaseUser;
    }

    public void signOut(){
        auth.signOut();
    }

    public void saveUserInfo(String username, String email, String password){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username",username);
        editor.putString("email",email);
        editor.putString("password",password);
        editor.apply();
    }

    public Map<String, String> getSavedUser(){
        Map<String, String> user = new HashMap<>();
        user.put("username", preferences.getString("username",""));
        user.put("email", preferences.getString("email",""));
        return user;
    }

}
