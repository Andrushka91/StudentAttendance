package com.andrushka.studentattendance.fragments;

import android.app.Activity;
import android.app.AlertDialog;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.andrushka.studentattendance.CategoryActivity;
import com.andrushka.studentattendance.R;
import com.andrushka.studentattendance.model.Attendance;
import com.andrushka.studentattendance.model.Courses;
import com.andrushka.studentattendance.model.Degree;
import com.andrushka.studentattendance.model.Groups;
import com.andrushka.studentattendance.model.Student;
import com.andrushka.studentattendance.model.Years;
import com.andrushka.studentattendance.util.RetrieveDegreeInformation;
import com.andrushka.studentattendance.util.UserApi;
import com.anychart.core.Base;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;


import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;

public class AttendanceFragment extends Fragment {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private AlertDialog dialogGroup;
    private AlertDialog dialogCourse;
    private AlertDialog dialogFingerPrint;
    private AlertDialog dialogAddStudent;

    private EditText degreeNameEditText;
    private EditText degreeDurationEditText;
    private EditText groupNameEditText;
    private EditText courseNameEditText;
    private EditText studentName;

    private Button addDegree;
    private Button addStudent;
    private Button addFingerPrint;
    private Button buttonScan;
    private Button buttonSaveFingerPrint;
    private Button saveStudent;
    private Button addGroup;
    private Button addCourse;
    private Button saveGroup;
    private Button saveCourse;
    private Button saveDegree;
    private Button makeAttendance;
    private boolean studentFound = false;


    private Spinner degreeSpinner;
    private Spinner yearSpinner;
    private Spinner courseSpinner;
    private Spinner groupSpinner;
    private Spinner degree_Spinner;
    private Spinner year_Spinner;
    private Spinner course_Spinner;
    private Spinner group_Spinner;


    private RetrieveDegreeInformation retrieveDegreeInformation;

    private Degree degree;
    private Student student = new Student();
    private static ArrayList<String> degreeList = new ArrayList<>();
    private static ArrayList<Courses> courseList = new ArrayList<>();
    private static ArrayList<Groups> groupList = new ArrayList<>();
    private static ArrayList<Years> yearList = new ArrayList<>();

    private FirebaseFirestore db;
    private CollectionReference degreeReference;
    private CollectionReference studentReference;
    private CollectionReference attendanceReference;

    private ArrayList<String> courses;
    private ArrayList<String> groups;

    private ProgressBar progressBar;
    private ProgressBar progressBarGroup;
    private ProgressBar progressBarCourse;
    private ProgressBar progressBarAddStudent;
    private ProgressBar progressBarFingerPrint;

    private Uri fingerPrintUri;
    private Activity activity = getActivity();

    private FingerprintTemplate fingerprintTemplate;

    private UserApi userApi;

    //Fingerprint Scanner related fields
    private String errorMessage = "empty";
    private int deviceStatus;
    private TextView deviceError;
    private Fingerprint fingerprint;
    //capture fingerprint
    private ImageView ivFinger;
    private ImageView fingerPrintImageDisplay;
    private TextView tvMessage;
    private TextView tvStatus;
    private TextView tvError;
    private TextView fingerPrintTextViewState;
    private byte[] imageByte;
    private byte[] fingerP;
    ArrayList<byte[]> bytesArrayList = new ArrayList<>();
    private Bitmap bm;
    private static final int SCAN_FINGER = 0;
    private int RESULT_CODE = 1;
    private int maxFireBaseEntries = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        db = FirebaseFirestore.getInstance();
        degreeReference = db.collection("Degree");
        studentReference = db.collection("Student");
        attendanceReference = db.collection("Attendance");
        userApi = UserApi.getInstance();

        degreeSpinner = view.findViewById(R.id.spinnerDegree);
        courseSpinner = view.findViewById(R.id.spinnerCourse);
        groupSpinner = view.findViewById(R.id.spinnerGroup);
        yearSpinner = view.findViewById(R.id.spinnerYear);

        fetchDegree(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        progressBar = view.findViewById(R.id.progressBar_AddDegree);

        addStudent = view.findViewById(R.id.addStudent);
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!degreeList.isEmpty()) {
                    degreeList.clear();
                }
                createPopupAddStudent(v);
            }
        });

        addDegree = view.findViewById(R.id.addDegree);
        addDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupAddDegree(view);
            }
        });

        degreeNameEditText = view.findViewById(R.id.degreeNameInput);
        fingerprint = new Fingerprint();

        makeAttendance = view.findViewById(R.id.attendanceScanFingerPrint);
    }


    private void fetchDegree(final View view) {
        degreeList.clear();
        courseList.clear();
        groupList.clear();
        yearList.clear();

        Query degreeQuery = degreeReference.whereEqualTo("userId", userApi.getUserId());
        degreeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        degree = document.toObject(Degree.class);
                        //adding all the degrees
                        degreeList.add(degree.getName());
                        //adding all the courses for each degree
                        Courses course = new Courses(degree.getName(), degree.getCourses());
                        courseList.add(course);
                        //adding all the groups for each degree
                        Groups groups = new Groups(degree.getName(), degree.getGroups());
                        groupList.add(groups);

                        //adding all the year for each degree
                        Years years = new Years(degree.getName(), degree.getDuration());
                        yearList.add(years);

                    }
                    fillSpinnerDegree(view);

                } else {
                    Snackbar.make(view, "No user info was found", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fillSpinnerDegree(View view) {
        ArrayAdapter<String> adapter_1 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, degreeList);
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        degreeSpinner.setAdapter(adapter_1);

        degreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = String.valueOf(degreeSpinner.getSelectedItem());

                for (int i = 0; i < degreeList.size(); i++) {
                    if (courseList.get(i).getDegreeName().equals(selectedOption)) {
                        fillSpinnerCourses(view, courseList.get(i).getCoursesList());
                    }
                }

                for (int i = 0; i < degreeList.size(); i++) {
                    if (groupList.get(i).getDegreeName().equals(selectedOption)) {
                        fillSpinnerGroups(view, groupList.get(i).getGroupList());
                    }
                }

                for (int i = 0; i < degreeList.size(); i++) {
                    if (yearList.get(i).getDegreeName().equals(selectedOption)) {
                        fillSpinnerYears(view, yearList.get(i).getDuration());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                student.setDegree(degreeSpinner.getSelectedItem().toString());
            }
        });

        makeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.setDegree(degreeSpinner.getSelectedItem().toString());
                student.setCourse(courseSpinner.getSelectedItem().toString());
                student.setGroup(groupSpinner.getSelectedItem().toString());
                student.setYear(yearSpinner.getSelectedItem().toString());

                createPopupAddFingerPrintAttendance(v);

            }
        });

    }

    public void fillSpinnerCourses(View view, ArrayList<String> courseList) {
        ArrayAdapter adapter_2 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, courseList);
        adapter_2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter_2);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student.setCourse(courseSpinner.getSelectedItem().toString());
                Log.d("test", "cour_sp setonClick: " + student.getCourse());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void fillSpinnerGroups(View view, ArrayList<String> groupList) {
        ArrayAdapter adapter_3 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, groupList);
        adapter_3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter_3);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student.setGroup(groupSpinner.getSelectedItem().toString());
                Log.d("test", "group_sp setonClick: " + student.getGroup());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fillSpinnerYears(View view, int duration) {
        ArrayList<String> years = new ArrayList<>(generateYears(duration));

        ArrayAdapter adapter_4 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, years);
        adapter_4.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter_4);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student.setYear(yearSpinner.getSelectedItem().toString());
                Log.d("test", "year_sp setonClick: " + student.getYear());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void fetchDegreesAddPopup(final View view) {
        Query degreeQuery = degreeReference.whereEqualTo("userId", userApi.getUserId());
        degreeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        degree = document.toObject(Degree.class);
                        //adding all the degrees
                        degreeList.add(degree.getName());
                        //adding all the courses for each degree
                        Courses course = new Courses(degree.getName(), degree.getCourses());
                        courseList.add(course);
                        //adding all the groups for each degree
                        Groups groups = new Groups(degree.getName(), degree.getGroups());
                        groupList.add(groups);

                        //adding all the year for each degree
                        Years years = new Years(degree.getName(), degree.getDuration());
                        yearList.add(years);

                    }

                    fillSpinnerDegreeAddPopup(view);


                } else {
                    Snackbar.make(view, "No user info was found", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fillSpinnerDegreeAddPopup(View view) {

        ArrayAdapter<String> adapter_1 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, degreeList);
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        degree_Spinner.setAdapter(adapter_1);

        student.setDegree(degree_Spinner.getSelectedItem().toString());

        degree_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = String.valueOf(degree_Spinner.getSelectedItem());


                for (int i = 0; i < degreeList.size(); i++) {
                    if (courseList.get(i).getDegreeName().equals(selectedOption)) {
                        fillSpinnerCoursesAddPopup(view, courseList.get(i).getCoursesList());
                    }
                }

                for (int i = 0; i < degreeList.size(); i++) {
                    if (groupList.get(i).getDegreeName().equals(selectedOption)) {
                        fillSpinnerGroupsAddPopup(view, groupList.get(i).getGroupList());
                    }
                }

                for (int i = 0; i < degreeList.size(); i++) {
                    if (yearList.get(i).getDegreeName().equals(selectedOption)) {
                        fillSpinnerYearsAddPopup(view, yearList.get(i).getDuration());
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                student.setDegree(degree_Spinner.getSelectedItem().toString());
            }
        });

        saveStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                student.setDegree(degree_Spinner.getSelectedItem().toString());
                student.setName(studentName.getText().toString());
                student.setUserId(userApi.getUserId());
                Log.d("test", "saveData Student: " + student.getDegree());
                Log.d("test", "saveData StudentName: " + student.getName());
                if (imageByte != null && !student.getName().isEmpty() && !student.getDegree().isEmpty() && !student.getCourse().isEmpty() && !student.getGroup().isEmpty() && !student.getYear().isEmpty()) {
                    saveStudentInfo(v, student);
                } else {
                    Snackbar.make(v, "Empty Fields not Allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fillSpinnerCoursesAddPopup(View view, ArrayList<String> courseList) {
        ArrayAdapter adapter_2 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, courseList);
        adapter_2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        course_Spinner.setAdapter(adapter_2);

        course_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student.setCourse(course_Spinner.getSelectedItem().toString());
                Log.d("test", "cour_sp setonClick: " + student.getCourse());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void fillSpinnerGroupsAddPopup(View view, ArrayList<String> groupList) {
        ArrayAdapter adapter_3 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, groupList);
        adapter_3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        group_Spinner.setAdapter(adapter_3);

        group_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student.setGroup(group_Spinner.getSelectedItem().toString());
                Log.d("test", "group_sp setonClick: " + student.getGroup());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fillSpinnerYearsAddPopup(View view, int duration) {
        ArrayList<String> years = new ArrayList<>(generateYears(duration));
        ArrayAdapter adapter_4 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, years);
        adapter_4.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        year_Spinner.setAdapter(adapter_4);

        year_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                student.setYear(year_Spinner.getSelectedItem().toString());
                Log.d("test", "year_sp setonClick: " + student.getYear());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public ArrayList<String> generateYears(int duration) {
        ArrayList<String> years = new ArrayList<>();
        for (int i = 1; i <= duration; i++) {
            years.add(String.valueOf(i));
        }
        return years;

    }

    private void addStudentSpinnersGenerate(View view) {
        degree_Spinner = view.findViewById(R.id.spinner_degree);
        course_Spinner = view.findViewById(R.id.spinner_course);
        group_Spinner = view.findViewById(R.id.spinner_group);
        year_Spinner = view.findViewById(R.id.spinner_year);
        addFingerPrint = view.findViewById(R.id.addStudentFingerPrint);
    }


    private void createPopupAddStudent(final View view) {
        builder = new AlertDialog.Builder(view.getContext());
        final View viewDialog = getLayoutInflater().inflate(R.layout.popup_addstudent, null);
        progressBarAddStudent = viewDialog.findViewById(R.id.progressBar_AddStudent);
        progressBarAddStudent.setVisibility(View.INVISIBLE);

        fetchDegreesAddPopup(viewDialog);

        studentName = viewDialog.findViewById(R.id.studentName);

        addStudentSpinnersGenerate(viewDialog);

        saveStudent = viewDialog.findViewById(R.id.saveStudentButton);

        addFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupAddFingerPrint(v);
            }
        });


        builder.setView(viewDialog);
        dialogAddStudent = builder.create();
        dialogAddStudent.show();
    }

    private void createPopupAddDegree(final View view) {

        builder = new AlertDialog.Builder(view.getContext());
        final View viewDialog = getLayoutInflater().inflate(R.layout.popup_add_degree, null);

        degreeNameEditText = viewDialog.findViewById(R.id.degreeNameInput);
        degreeDurationEditText = viewDialog.findViewById(R.id.degreeDurationInput);

        progressBar = viewDialog.findViewById(R.id.progressBar_AddDegree);
        progressBar.setVisibility(View.INVISIBLE);

        saveDegree = viewDialog.findViewById(R.id.saveDegreeButton);

        groups = new ArrayList<>();
        courses = new ArrayList<>();

        addGroup = viewDialog.findViewById(R.id.addGroup);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupAddGroup(v);
            }
        });

        addCourse = viewDialog.findViewById(R.id.addCourse);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupAddCourse(v);
            }
        });

        saveDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!degreeNameEditText.getText().toString().isEmpty() && !degreeDurationEditText.getText().toString().isEmpty() && !groups.isEmpty() && !courses.isEmpty()) {
                    degree = new Degree();

                    String degreeName = degreeNameEditText.getText().toString();
                    int degreeDuration = Integer.parseInt(degreeDurationEditText.getText().toString().trim());
                    degree.setUserId(UserApi.getInstance().getUserId());
                    degree.setName(degreeName);
                    degree.setDuration(degreeDuration);
                    degree.setGroups(groups);
                    degree.setCourses(courses);

                    saveDegreeInfo(degree);

                } else {
                    Snackbar.make(viewDialog, "Empty Fields not Allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(viewDialog);
        dialog = builder.create();
        dialog.show();
    }

    private void createPopupAddGroup(final View view) {
        builder = new AlertDialog.Builder(view.getContext());
        final View viewDialog = getLayoutInflater().inflate(R.layout.popup_add_group, null);

        progressBarGroup = viewDialog.findViewById(R.id.progressBar_AddGroups);
        progressBarGroup.setVisibility(View.INVISIBLE);

        groupNameEditText = viewDialog.findViewById(R.id.groupInput);
        saveGroup = viewDialog.findViewById(R.id.saveGroup);

        saveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!groupNameEditText.getText().toString().isEmpty()) {
                    String group = groupNameEditText.getText().toString().trim();
                    saveGroup(group);
                } else {
                    Snackbar.make(view, "Empty Fields not Allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(viewDialog);
        dialogGroup = builder.create();
        dialogGroup.show();
    }

    private void createPopupAddCourse(final View view) {
        builder = new AlertDialog.Builder(view.getContext());
        View viewDialog = getLayoutInflater().inflate(R.layout.popup_add_course, null);

        progressBarCourse = viewDialog.findViewById(R.id.progressBar_AddCourse);
        progressBarCourse.setVisibility(View.INVISIBLE);

        courseNameEditText = viewDialog.findViewById(R.id.courseInput);
        saveCourse = viewDialog.findViewById(R.id.saveCourse);

        saveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!courseNameEditText.getText().toString().isEmpty()) {
                    String course = courseNameEditText.getText().toString().trim();
                    saveCourse(course);
                } else {
                    Snackbar.make(view, "Empty Fields not Allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(viewDialog);
        dialogCourse = builder.create();
        dialogCourse.show();
    }

    private void saveGroup(String group) {
        groups.add(group);
        progressBarGroup.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogGroup.dismiss();
            }
        }, 1200);//3s

    }

    private void saveCourse(String course) {
        courses.add(course);
        progressBarCourse.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogCourse.dismiss();
            }
        }, 1200);//3s

    }


    private void saveDegreeInfo(Degree degree) {

        if (!TextUtils.isEmpty(degree.getName()) && degree.getDuration() != 0 && !groups.isEmpty()) {

            degreeReference.add(degree).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                           refreshData();
                        }
                    }, 1300);//1.3s
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Error", "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void refreshData() {

        getActivity().getSupportFragmentManager().beginTransaction().replace(AttendanceFragment.this.getId(), new AttendanceFragment()).commit();

    }




    //FingerPrint Scanner methods
    private void createPopupAddFingerPrint(View view) {
        builder = new AlertDialog.Builder(view.getContext());
        final View viewDialog = getLayoutInflater().inflate(R.layout.popup_add_fingerprint, null);

        tvMessage = viewDialog.findViewById(R.id.tvMessage);
        tvError = viewDialog.findViewById(R.id.tvError);
        tvStatus = viewDialog.findViewById(R.id.tvStatus);
        ivFinger = viewDialog.findViewById(R.id.ivFingerDisplay);

        progressBarFingerPrint = viewDialog.findViewById(R.id.progressBar_addFingerPrint);
        progressBarFingerPrint.setVisibility(View.INVISIBLE);

        buttonScan = viewDialog.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprint.scan(viewDialog.getContext(), printHandler, updateHandler);
            }
        });

        buttonSaveFingerPrint = viewDialog.findViewById(R.id.buttonSaveFingerPrint);
        buttonSaveFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onClick Save: " + student.getDegree());

                if (imageByte != null) {
                    saveFingerPrint(v);
                } else {
                    Snackbar.make(v, "FingerPrint not scanned, please scan the fingerprint.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(viewDialog);
        dialogFingerPrint = builder.create();
        dialogFingerPrint.show();
    }

    private void createPopupAddFingerPrintAttendance(View view) {
        builder = new AlertDialog.Builder(view.getContext());
        final View viewDialog = getLayoutInflater().inflate(R.layout.popup_add_fingerprint, null);

        tvMessage = viewDialog.findViewById(R.id.tvMessage);
        tvError = viewDialog.findViewById(R.id.tvError);
        tvStatus = viewDialog.findViewById(R.id.tvStatus);
        ivFinger = viewDialog.findViewById(R.id.ivFingerDisplay);

        progressBarFingerPrint = viewDialog.findViewById(R.id.progressBar_addFingerPrint);
        progressBarFingerPrint.setVisibility(View.INVISIBLE);
        buttonScan = viewDialog.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprint.scan(viewDialog.getContext(), printHandler, updateHandler);
            }
        });

        buttonSaveFingerPrint = viewDialog.findViewById(R.id.buttonSaveFingerPrint);
        buttonSaveFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onClick Save: " + student.getDegree());

                if (imageByte != null) {
                    try {
                        makeAttendance(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(v, "FingerPrint not scanned, please scan the fingerprint.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(viewDialog);
        dialogFingerPrint = builder.create();
        dialogFingerPrint.show();
    }

    //&& !TextUtils.isEmpty(student.getFingerPrint())
    private void saveStudentInfo(final View view, Student student) {
        progressBarAddStudent.setVisibility(View.VISIBLE);
        studentReference.add(student).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogAddStudent.dismiss();
                        Toast.makeText(view.getContext(), "Student details saved", Toast.LENGTH_SHORT).show();
                        AttendanceFragment attendanceFragment = new AttendanceFragment();
                    }
                }, 1300);//1.3s
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(), "Student data not saved", Toast.LENGTH_SHORT).show();
                Log.d("AddStudent", "onFailure: " + e.getMessage());
            }
        });

//        getContext().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_profile,new ProfileFragment()).commit();
    }


    private void saveFingerPrint(final View v) {
        progressBarFingerPrint.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] encoded = Base64.getEncoder().encode(imageByte);
                String encodedString = new String(encoded);
                student.setFingerPrint(encodedString);
                dialogFingerPrint.dismiss();
                Toast.makeText(v.getContext(), "Student FingerPrint saved", Toast.LENGTH_SHORT).show();
            }
        }, 1300);//1.3s
    }


    private void makeAttendance(final View view) throws IOException {
        Log.d("test", "makeAttendance: student: " + student.getDegree());
        progressBarFingerPrint.setVisibility(View.VISIBLE);

        queryFingerPrintFireBase(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogFingerPrint.dismiss();
            }
        }, 1200);//3s


    }

    private void queryFingerPrintFireBase(final View view) {
        Query studentQuery = studentReference.whereEqualTo("userId", UserApi.getInstance().getUserId());
        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    boolean found = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Student studentDb = document.toObject(Student.class);
                        Log.d("test", "student_fingerPrint DB: " + studentDb.getFingerPrint());
                        if (compareStudents(student, studentDb)) {
                            found = true;
                            Log.d("test", "Attendace worked ");
                            saveAttendance(view, student, studentDb);
                        }
                    }
                    if (!found) {
                        Toast.makeText(view.getContext(), "Student was not found in the database, please add the student", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void saveAttendance(final View view, Student student, Student studentDb) {
        final Calendar calendar = Calendar.getInstance();
        Attendance attendance = new Attendance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        attendance = new Attendance(userApi.getUserId(), student.getDegree(), student.getCourse(), student.getGroup(), student.getYear(), studentDb.getName(), currentDate);

        attendanceReference.add(attendance).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(view.getContext(), "Student was found in the database and data was successfully stored", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void fetchFinger() {

        Query fingerPrintQuery = studentReference.whereEqualTo("userId", UserApi.getInstance().getUserId());
        fingerPrintQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Student studentDb = document.toObject(Student.class);
                        fingerP = Base64.getDecoder().decode(studentDb.getFingerPrint());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("test", "onComplete: failure ");
            }
        });
    }

    private boolean compareStudents(Student student, Student studentDb) {

        byte[] studentFingerPrintDb = Base64.getDecoder().decode(studentDb.getFingerPrint());

        return student.getDegree().equals(studentDb.getDegree()) &&
                student.getCourse().equals(studentDb.getCourse()) &&
                student.getGroup().equals(studentDb.getGroup()) &&
                student.getYear().equals(studentDb.getYear()) &&
                compareFingerPrints(imageByte, studentFingerPrintDb);
    }

    private boolean compareFingerPrints(byte[] imageByteCandidate, byte[] imageByteProbe) {
        boolean matches = false;
        double score = 0;
        byte[] fProbe = Arrays.copyOf(imageByteCandidate, imageByteCandidate.length);
        byte[] fCandidate = Arrays.copyOf(imageByteProbe, imageByteProbe.length);

        FingerprintTemplate probe = new FingerprintTemplate();
        probe.dpi(500);

        FingerprintTemplate candidate = new FingerprintTemplate();
        candidate.dpi(500);

        try {
            probe.create(fProbe);
            candidate.create(fCandidate);

            score = new FingerprintMatcher()
                    .index(probe)
                    .match(candidate);

            matches = score >= 40;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.setResult(Activity.RESULT_CANCELED);
                fingerprint.turnOffReader();
                activity.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop() {
        fingerprint.turnOffReader();
        super.onStop();
    }

    Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");

            tvError.setText("");
            switch (status) {
                case Status.INITIALISED:
                    tvStatus.setText("Setting up reader");
                    break;
                case Status.SCANNER_POWERED_ON:
                    tvStatus.setText("Reader powered on");
                    break;
                case Status.READY_TO_SCAN:
                    tvStatus.setText("Ready to scan finger");
                    break;
                case Status.FINGER_DETECTED:
                    tvStatus.setText("Finger detected");
                    break;
                case Status.RECEIVING_IMAGE:
                    tvStatus.setText("Receiving image");
                    break;
                case Status.FINGER_LIFTED:
                    tvStatus.setText("Finger has been lifted off reader");
                    break;
                case Status.SCANNER_POWERED_OFF:
                    tvStatus.setText("Reader is off");
                    break;
                case Status.SUCCESS:
                    tvStatus.setText("Fingerprint successfully captured");
                    break;
                case Status.ERROR:
                    tvStatus.setText("Error");
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    tvStatus.setText(String.valueOf(status));
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;
            }
        }
    };

    Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            deviceStatus = msg.getData().getInt("status");
            Log.d("printHandler", "handleMessage: " + deviceStatus);
            if (deviceStatus == Status.SUCCESS) {
                imageByte = msg.getData().getByteArray("img");
                tvMessage.setText("Fingerprint captured");
                bm = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                ivFinger.setImageBitmap(bm);

            } else {
                errorMessage = msg.getData().getString("errorMessage");
            }
        }
    };

}
