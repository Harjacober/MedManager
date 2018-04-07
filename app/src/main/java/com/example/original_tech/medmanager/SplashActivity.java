package com.example.original_tech.medmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.original_tech.medmanager.authentication.IntroductionActivity;
import com.example.original_tech.medmanager.authentication.SignInActivity;
import com.example.original_tech.medmanager.authentication.SignUpActivity;

/**
 * Created by Original-Tech on 4/6/2018.
 */

public class SplashActivity extends AppCompatActivity {
    public static final String SPLASH_PREF = "splash-pref";
    public static final String STATE_KEY = "splash-pref";
    public static final String LOG_IN_VAL = "log-in-val";
    public static final String SIGN_UP_VAL = "sign-up-val";
    public static final String SIGNED_OUT = "signed-out";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processSomeShit();
    }

    private void processSomeShit() {
        SharedPreferences sharedPreferences = getSharedPreferences(SPLASH_PREF, 0);
        String state = sharedPreferences.getString(STATE_KEY,"");
        //if the User already logged in before
        switch (state) {
            case LOG_IN_VAL: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            //if the user already signed up before, no need to sign up again
            case SIGN_UP_VAL: {
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case SIGNED_OUT: {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            default: {
                Intent intent = new Intent(this, IntroductionActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    public static void saveUserState(Context context, String string) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPLASH_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATE_KEY, string);
        editor.commit();
    }
}
