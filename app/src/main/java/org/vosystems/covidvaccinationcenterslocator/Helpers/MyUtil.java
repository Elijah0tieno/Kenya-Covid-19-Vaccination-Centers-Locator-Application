package org.vosystems.covidvaccinationcenterslocator.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyUtil {
    public static String toString(int n){
        int i = 0;
        String str = "";

        if(n==0){
            return "0";
        }

        while (n>0){
            str = (n%10)+str;
            n = (n-(n%10))/10;
            i++;

            if(i==3&&n>10){
                str = ","+str;
                i=0;
            }
        }
        return str;
    }

    public static void showAlert(Context context, String msg, String title){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title).setMessage(msg)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    public static String getTime(long timestamp){
        Date date = new Date(timestamp);
        int month = date.getMonth();
        int day = date.getDate();
        int year = date.getYear();
        int hour = date.getHours();
        int minutes = date.getMinutes();

        String dy = ""+day;

        if(day<10){
            dy = "0"+day;
        }

        String hr = ""+hour;
        if(hour<10){
            hr = "0"+hr;

        }

        String mins = ""+minutes;
        if(minutes<10){
            mins = "0"+mins;
        }

        String date_time = "";

        Date today = new Date();
        if(today.getDate()==day&&today.getMonth()==month&&today.getYear()==year){
            date_time = "Today "+hr+":"+mins;
        }else if(today.getDate()==(day+1)&&today.getMonth()==month&&today.getYear()==year){
            date_time = "Yesterday "+hr+":"+mins;
        }else{
            date_time = dy+"/"+(month+1)+"/"+(1900+year)+" "+hr+":"+mins;
        }

        return date_time;
    }

    public static String getDate(long timestamp){
        Date date = new Date(timestamp);
        int month = date.getMonth();
        int day = date.getDate();
        int year = date.getYear();
        int hour = date.getHours();
        int minutes = date.getMinutes();

        String dy = ""+day;

        if(day<10){
            dy = "0"+day;
        }

        String hr = ""+hour;
        if(hour<10){
            hr = "0"+hr;
        }

        String mins = ""+minutes;
        if(minutes<10){
            mins = "0"+mins;
        }

        String date_time = "";

        Date today = new Date();
        if(today.getDate()==day&&today.getMonth()==month&&today.getYear()==year){
            date_time = "Today "+hr+":"+mins;
        }else if(today.getDate()==(day+1)&&today.getMonth()==month&&today.getYear()==year){
            date_time = "Yesterday "+hr+":"+mins;
        }else{
            date_time = dy+"/"+(month+1)+"/"+(1900+year);
        }

        return date_time;
    }

    public static void showProgress(View progress, View content){
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    public static void hideProgress(View progress, View content){
        content.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }


    public static Map<String, String> getUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("auth",Context.MODE_PRIVATE);
        Map<String, String> details = new HashMap<>();

        details.put("username",preferences.getString("username", ""));

        return details;

    }

    public static void saveUser(SharedPreferences auth,String email, String username) {
        SharedPreferences.Editor editor = auth.edit();
        editor.putString("email",email);
        editor.putString("username", username);

        editor.apply();
    }
}
