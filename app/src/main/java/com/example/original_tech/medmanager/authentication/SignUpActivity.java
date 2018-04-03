package com.example.original_tech.medmanager.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.original_tech.medmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mUSername;
    private Button mSignIn;
    private Button mSignUp;
    private Button mForgotPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Get FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.email_field);
        mPassword = findViewById(R.id.password_field);
        mConfirmPassword = findViewById(R.id.c_password_field);
        mUSername = findViewById(R.id.username_field);
        mSignIn = findViewById(R.id.sign_in_button);
        mSignUp = findViewById(R.id.sign_up_button);
        mForgotPassword = findViewById(R.id.forgot_pass_button);

        //Check if Intent has extras
        Intent intent = getIntent();
        if (intent.hasExtra("email")) {
            String email = intent.getStringExtra("email");
            mEmail.setText(email);
        }
    }

    public void onSignInButtonClicked(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void onForgotPasswordClicked(View view) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void onSignUpButtonClicked(View view) {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String cPassword = mConfirmPassword.getText().toString();
        if (!password.equals(cPassword)){
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
        }else if(password.length() < 6){
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
        }else if(!email.contains("@")){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else{
            registerUser(email, password, mAuth);
        }
    }

    private void registerUser(String email, String password, FirebaseAuth auth) {
        showProgressDialog(true);
        auth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignUpActivity.this, "createdUserWithEmail: onComplete: "
                                +task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()){
                            showProgressDialog(false);
                            Toast.makeText(SignUpActivity.this, "Authentication failed "
                                    +task.getException(), Toast.LENGTH_SHORT).show();
                        }else{
                            showProgressDialog(false);
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }
    private void showProgressDialog(boolean show){
        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        if (show) {
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }
}
