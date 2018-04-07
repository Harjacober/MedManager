package com.example.original_tech.medmanager.authentication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.original_tech.medmanager.AddNewMedActivity;
import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.SplashActivity;
import com.example.original_tech.medmanager.data.MedicationContract;
import com.example.original_tech.medmanager.utils.BitmapUtils;
import com.example.original_tech.medmanager.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView mEmailEdit;
    private ImageView mPasswordEdit;
    private Button mDone;
    private Button mRemoveUser;
    private TextView mEmail, mPassword;
    private TextView mMedCount, mMonthCount;
    private ImageView mProfilePicture;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button mSignOut;
    private static final int SELECT_PICTURE = 300;
    private static final String PROFILE_PICTURE_PATH = "profile-picture-path";
    private static final String IMAGE_PATHS = "image-path";
    ProgressDialog progressDialog;
    private boolean check = false;

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
        mMedCount = findViewById(R.id.med_count);
        mMonthCount = findViewById(R.id.month_count);


        displayProfilePicture();
        android.support.v7.app.ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        setUserCridentials();
        new GetMedicaticationCount().execute();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==SELECT_PICTURE){
                Uri selectedImageUri=data.getData();
                if (selectedImageUri!=null){
                    Bitmap bitmap=null;
                    try {
                        bitmap = BitmapUtils.getThumbnail(selectedImageUri, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mProfilePicture.setImageBitmap(bitmap);
                }
            }
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

    public void onEmailEditClicked(View view) {
        createDialog("email");
    }

    public void onPasswordEditClicked(View view) {
        createDialog("password");
    }

    public void onDoneClicked(View view) {
        //Saves Image path to shared preference and upload or updates it online
        String filePath = BitmapUtils.saveImageToSDCard(BitmapUtils.getIntentimage(mProfilePicture));
        SharedPreferences preferences = getSharedPreferences(PROFILE_PICTURE_PATH, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(IMAGE_PATHS, filePath);
        editor.commit();
        //Then
        finish();
    }

    public void onRemoveClicked(View view) {
        if (!NetworkUtils.isConnected(this)){
            Toast.makeText(this, "Check your Internet Connection", Toast.LENGTH_LONG).show();
        }else if (mUser != null){
            progressDialog.show();
            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfileActivity.this, SignUpActivity.class);
                        startActivity(intent);
                        check = true;
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void createDialog(final String email){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.DialogTheme));
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
        progressDialog.show();
        if (mUser != null) {
            mUser.updatePassword(password).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "password updated", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        signOut();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void changeEmail(String email){
        progressDialog.show();
        if (mUser != null) {
            mUser.updateEmail(email).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfileActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                signOut();
                            } else {
                                progressDialog.dismiss();
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
        startActivity(new Intent(UserProfileActivity.this, SignInActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
            if (check == true) {
                //save  user state
                SplashActivity.saveUserState(this, SplashActivity.SIGNED_OUT);
            }
        }
    }

    public void onSignOutClicked(View view) {
        if (!NetworkUtils.isConnected(this)){
            Toast.makeText(this, "Check your Internet Connection", Toast.LENGTH_LONG).show();
        }else {
            check = true;
            signOut();
        }
    }

    public void onImageChooserClicked(View view) {
        Intent intent1=new Intent(Intent.ACTION_GET_CONTENT);
        intent1.setType("image/*");
        startActivityForResult(Intent.createChooser(intent1,"Choose Image"), SELECT_PICTURE);
    }

    private void displayProfilePicture(){
        SharedPreferences preferences = getSharedPreferences(PROFILE_PICTURE_PATH, 0);
        String path = preferences.getString(IMAGE_PATHS, null);
        if (path != null){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            mProfilePicture.setImageBitmap(bitmap);
        }
    }

    class GetMedicaticationCount extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor = getContentResolver().query(MedicationContract.MedicationEntry.CONTENT_URI,
                    new String[] {MedicationContract.MedicationEntry._ID},
                    null,
                    null,
                    null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null){
                mMedCount.setText(String.valueOf(cursor.getCount()));
            }
            setElapsedMonth(mMonthCount);
        }
    }

    private void setElapsedMonth(TextView monthCount) {
        SharedPreferences preferences = getSharedPreferences(AddNewMedActivity.FIRST_MED_CREATED, 0);
        long firstCreatedTime = preferences.getLong(AddNewMedActivity.TIME_IN_MILLIS, 0);
        long currentTime = System.currentTimeMillis();
        long interval = (currentTime - firstCreatedTime) / 1000;
        int elapsedMonth = (int) (interval/60/60/24/30);
        monthCount.setText(String.valueOf(elapsedMonth));
    }


    private void setUserCridentials() {
        SharedPreferences preferences = getSharedPreferences(SignInActivity.USER_DETAILS, 0);
        mEmail.setText(preferences.getString(SignInActivity.USER_EMAIL, ""));
        mPassword.setText(preferences.getString(SignInActivity.USER_PASSWORD, ""));
    }
}
