package com.andrushka.studentattendance.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuInflater;
import com.andrushka.studentattendance.CategoryActivity;
import com.andrushka.studentattendance.MainActivity;
import com.andrushka.studentattendance.R;
import com.andrushka.studentattendance.model.ProfileInformation;
import com.andrushka.studentattendance.model.User;
import com.andrushka.studentattendance.model.UserInformation;
import com.andrushka.studentattendance.ui.RecyclerViewAdapter;
import com.andrushka.studentattendance.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "logd";
    private CoordinatorLayout rootLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private TextView name;
    private Context context;
    private Button addProfileInfo;
    private Button uploadProfilePhoto;
    private Button saveInfoButton;
    private TextView profileName;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private ImageView profilePicture;
    private ProgressBar progressBar;
    private String imageUrl;
    private List<ProfileInformation> profileInformationList;
    //Connection to Firestore
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private CollectionReference userCollectionReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private StorageReference dataStorageReference;
    private String currentUserId;
    private String currentUserName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (UserApi.getInstance() != null) {
            currentUserId = UserApi.getInstance().getUserId();
            currentUserName = UserApi.getInstance().getUsername();
        }


        profilePicture = view.findViewById(R.id.profilePicture);

        profileName = view.findViewById(R.id.profile_name);
        context = getContext();
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("UserInfo");

        dataStorageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

        addProfileInfo = view.findViewById(R.id.add_infoProfile);
        addProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupDialog(view);
            }

        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePicture = view.findViewById(R.id.profilePicture);
        getUserInfo(view);
        setUserPhoto(view);
    }

    private void updateUserInfo(){
        //Little query to clean the previous information
        Query userInfoQuery = collectionReference.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        userInfoQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                    saveInfoProfile();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(ProfileFragment.this.getId(), new ProfileFragment()).commit();

                } else {
                    saveInfoProfile();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(ProfileFragment.this.getId(), new ProfileFragment()).commit();
                }
            }
        });
    }

    private void saveInfoProfile() {
        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString().trim();
        final String phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(email)) {

            UserInformation userInformation = new UserInformation();
            userInformation.setName(name);
            userInformation.setEmail(email);
            userInformation.setPhoneNumber(phoneNumber);
            userInformation.setUserId(currentUserId);

            collectionReference.add(userInformation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 3200);//1s
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void getUserInfo(final View view) {
        collectionReference = db.collection("UserInfo");

        Query userInfoQuery = collectionReference.whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        profileInformationList = new ArrayList<>();

        userInfoQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserInformation userInformation = document.toObject(UserInformation.class);
                        ProfileInformation p1 = new ProfileInformation("Email",userInformation.getEmail());
                        ProfileInformation p2 = new ProfileInformation("PhoneNumber",userInformation.getPhoneNumber());
                        profileInformationList.add(p1);
                        profileInformationList.add(p2);
                        profileName.setText(userInformation.getName());
                    }
                    recyclerViewAdapter = new RecyclerViewAdapter(view.getContext(),profileInformationList);
                    recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewAdapter.notifyDataSetChanged();

                } else {
                    Snackbar.make(view, "No user info was found", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUserPhoto(final View view){
        userCollectionReference = db.collection("Users");

        Query userPhoto = userCollectionReference.whereEqualTo("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());

        userPhoto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        imageUrl = user.getImageUrl();
                    }
                    Picasso.get().load(imageUrl).into(profilePicture);
                }else {
                    Snackbar.make(view, "No user info was found", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void createPopupDialog(final View viewContext) {
        builder = new AlertDialog.Builder(viewContext.getContext());

        final View view = getLayoutInflater().inflate(R.layout.popup_updateinfo, null);

        nameEditText = view.findViewById(R.id.user_profileName);
        emailEditText = view.findViewById(R.id.user_profileEmail);
        phoneNumberEditText = view.findViewById(R.id.user_profilePhoneNumber);
        progressBar = view.findViewById(R.id.progressBarUpdateInfo);
        progressBar.setVisibility(View.INVISIBLE);
        saveInfoButton = view.findViewById(R.id.save_InfoButton);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameEditText.getText().toString().isEmpty()
                        && !emailEditText.getText().toString().isEmpty() && !phoneNumberEditText.getText().toString().isEmpty()) {

                    profileName.setText(nameEditText.getText().toString());
                    updateUserInfo();
                } else {
                    Snackbar.make(view, "Empty Fields not Allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }







}