package com.andrushka.studentattendance.util;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.andrushka.studentattendance.R;
import com.andrushka.studentattendance.model.Degree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.andrushka.studentattendance.model.ProfileInformation;
import com.andrushka.studentattendance.model.UserInformation;
import com.andrushka.studentattendance.ui.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class RetrieveDegreeInformation {
    private Degree degree;
    private String userId;
    private ArrayList<Degree> degreeList;
    private ArrayList<String> coursesList;
    private ArrayList<Integer> duration;
    private ArrayList<Degree> groupList;

    private FirebaseFirestore db;
    private CollectionReference degreeReference;
    private CollectionReference studentReference;

    public RetrieveDegreeInformation(String userId) {
        this.userId = userId;
        db = FirebaseFirestore.getInstance();
        degreeReference = db.collection("Degree");
        degreeList = new ArrayList<>();
    }

    public ArrayList<Degree> fetchDegrees(final View view) {
        Query degreeQuery = degreeReference.whereEqualTo("userId", userId);

        degreeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        degree = document.toObject(Degree.class);
                        degree.setName(degree.getName());
                        degree.setCourses(degree.getCourses());
                        degree.setGroups(degree.getGroups());
                        degree.setDuration(degree.getDuration());
                    }
                    degreeList.add(degree);
                    Log.d("fetchDegreelist", "onComplete: Querry was successefull first object is: " + degreeList.get(0).getName());
                } else {
                    Snackbar.make(view, "No user info was found", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return degreeList;
    }

    public ArrayList<String> fetchDegreeNameList() {
        ArrayList<String> nameList = new ArrayList<>();
       for(int i = 0; i < degreeList.size();i++){
            nameList.add(degreeList.get(i).getName());
       }
       return nameList;
    }

    public ArrayList<String> fetchCourses(Degree degree) {
        return degree.getCourses();
    }

    private ArrayList<Integer> fetchDuration() {
        for (int i = 1; i <= degree.getDuration(); i++) {
            duration.add(i);
        }
        return duration;
    }

    public ArrayList<String> fetchGroups(Degree degree) {
        return degree.getGroups();
    }


}
