package com.andrushka.studentattendance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andrushka.studentattendance.R;
import com.andrushka.studentattendance.model.Attendance;
import com.andrushka.studentattendance.util.UserApi;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Cartesian3d;
import com.anychart.core.gauge.pointers.Bar;
import com.anychart.enums.ScaleStackMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference attendanceReference;
    private UserApi userApi;

    AnyChartView anyChartView;
    String[] months = {"Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"};
    int[] students = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private Attendance attendance;

    private ArrayList<String> monthList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        attendanceReference = db.collection("Attendance");
        userApi = UserApi.getInstance();
        generateAttendanceData();;
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = view.findViewById(R.id.any_chart_view);
    }

    public void setupBarChart() {
        Cartesian3d bar = AnyChart.bar3d();

        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            dataEntries.add(new ValueDataEntry(months[i], students[i]));
        }

        bar.data(dataEntries);
        anyChartView.setChart(bar);

    }

    private void generateAttendanceData() {
        Query query = attendanceReference.whereEqualTo("userId", userApi.getUserId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int[] students = new int[10];
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        attendance = document.toObject(Attendance.class);
                        String splitDate[] = attendance.getDate().split(" ");
                        String month = splitDate[0];
                        monthList.add(month);
                    }
                } else {
                    Log.d("Error", "onComplete: error");
                }
                countStudentsByMonth();
            }
        });
    }

    private void countStudentsByMonth() {
        for (int i = 0; i < students.length; i++) {
            for (int j = 0; j < monthList.size(); j++) {
                if (monthList.get(j).equals(months[i])) {
                    students[i]++;
                    Log.d("logd", "countStudentsByMonth:" + students[i]);
                }
            }
        }
        setupBarChart();
    }
}