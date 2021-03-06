package com.example.original_tech.medmanager.authentication;

import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText mEmail;
    private Button mReset;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        //Get instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.email_field);
        mReset = findViewById(R.id.reset_button);

        android.support.v7.app.ActionBar actiobar=getSupportActionBar();
        if (actiobar!=null){
            actiobar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResetButtonClicked(View view) {
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        }else if(!email.contains("@")){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }else if (!NetworkUtils.isConnected(this)){
            Toast.makeText(this, "Check your Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            mAuth.sendPasswordResetEmail(email).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this,
                                        "We have sent you an email to reset your password",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ResetPasswordActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
