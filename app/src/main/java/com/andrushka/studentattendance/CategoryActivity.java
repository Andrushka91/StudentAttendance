package com.andrushka.studentattendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.andrushka.studentattendance.fragments.AttendanceFragment;
import com.andrushka.studentattendance.fragments.ProfileFragment;
import com.andrushka.studentattendance.fragments.StatisticsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CategoryActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private BottomNavigationView bottomNavigationView;
    private ImageView options;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private ProgressBar uploadPhotoProgressBar;
    private AlertDialog.Builder builder;
    private AlertDialog dialogUpdatePhoto;

    private StorageReference imageStorageReference;
    private static final int GALLERY_CODE = '2';
    private static Uri imageUri;
    private String userImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        imageStorageReference = FirebaseStorage.getInstance().getReference("Images");

        options = findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOptions(v);
            }
        });
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_profile, new ProfileFragment()).commit();
    }

    public void menuOptions(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.profile_options_menu, popup.getMenu());
        popup.show();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.navigation_attendance:
                    selectedFragment = new AttendanceFragment();
                    break;
                case R.id.navigation_statistics:
                    selectedFragment = new StatisticsFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_profile, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                return true;
            default:
                return false;
        }
    }

//    private void createPopupUpdatePhoto(){
//        builder = new AlertDialog.Builder(getApplicationContext());
//        final View viewDialog = getLayoutInflater().inflate(R.layout.popup_update_photo, null);
//         uploadPhotoProgressBar = viewDialog.findViewById(R.id.progress_bar_update_photo);
//         uploadPhotoProgressBar.setVisibility(View.INVISIBLE);
//        builder.setView(viewDialog);
//        dialogUpdatePhoto = builder.create();
//        dialogUpdatePhoto.show();
//    }


//    private void openFileChooser() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(galleryIntent, GALLERY_CODE);
//    }

//    private void uploadPicture() {
//        final StorageReference filepath = imageStorageReference.child("profile_images").child(String.valueOf(Timestamp.now().getSeconds())); //my_image_887654344          //..../journal_images/our_image.jpg
//
//        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        userImageUrl = uri.toString();
//                        Toast.makeText(getApplicationContext(),"Image was updated succesfully",Toast.LENGTH_SHORT);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
//            imageUri = data.getData();
//        }
//    }
}





