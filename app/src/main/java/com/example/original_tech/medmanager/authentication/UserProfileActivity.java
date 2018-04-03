package com.example.original_tech.medmanager.authentication;

import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original_tech.medmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView mEmailEdit;
    private ImageView mPasswordEdit;
    private Button mDone;
    private Button mRemoveUser;
    private TextView mEmail;
    private TextView mPassword;
    private ImageView mProfilePicture;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button mSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Get Firebase auth Instance
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser == null){
                    startActivity(new Intent(UserProfileActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };

        mEmailEdit = findViewById(R.id.profle_edit_email);
        mPasswordEdit = findViewById(R.id.profile_edit_password);
        mDone = findViewById(R.id.profile_edit_done);
        mRemoveUser = findViewById(R.id.profile_remove_account);
        mEmail = findViewById(R.id.profile_email);
        mPassword = findViewById(R.id.profile_password);
        mProfilePicture = findViewById(R.id.profile_picture);
        mSignOut = findViewById(R.id.sign_out);
    }

    public void onEmailEditClicked(View view) {
        createDialog("email");
    }

    public void onPasswordEditClicked(View view) {
        createDialog("password");
    }

    public void onDoneClicked(View view) {
    }

    public void onRemoveClicked(View view) {
        if (mUser != null){
            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfileActivity.this, SignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(UserProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void createDialog(final String email){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dia_user_profile_changes, null);
        final EditText one = view.findViewById(R.id.edit_text_one);
        final EditText two = view.findViewById(R.id.edit_text_two);
        if (email.equals("email")){
            two.setVisibility(View.GONE);
        }
                builder.setView(view).setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (email.equals("email")){
                    String value1 = one.getText().toString().trim();
                    if (validateEmailInput(value1)){
                        changeEmail(value1);
                    }
                }else if(email.equals("password")){
                    String value1 = one.getText().toString().trim();
                    String value2 = two.getText().toString().trim();
                    if (validatePasswordInput(value1, value2)){
                        changePassword(value1);
                    }
                }

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validatePasswordInput(String value1, String value2) {
        if (!value1.equals(value2)){
            Toast.makeText(this, "password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(value1)){
            Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(value2)){
            Toast.makeText(this, "Enter password again", Toast.LENGTH_SHORT).show();
            return false;
        }else if(value1.length() < 6){
            Toast.makeText(this, "password too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateEmailInput(String value1) {
        if (TextUtils.isEmpty(value1)){
            Toast.makeText(this, "Enter new Email", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!value1.contains("@")){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void changePassword(String password){
        if (mUser != null) {
            mUser.updatePassword(password).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "password updated", Toast.LENGTH_SHORT).show();
                        signOut();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void changeEmail(String email){
        if (mUser != null) {
            mUser.updateEmail(email).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfileActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                signOut();
                            } else {
                                Toast.makeText(UserProfileActivity.this, "Failed to update Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //This listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null){
                startActivity(new Intent(UserProfileActivity.this, SignInActivity.class));
                finish();
            }else{

            }
        }
    };

    public void signOut(){
        mAuth.signOut();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void onSignOutClicked(View view) {
        signOut();
    }
}
