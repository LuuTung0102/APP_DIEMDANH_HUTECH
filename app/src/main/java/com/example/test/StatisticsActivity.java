package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.test.adapter.StudentAdapter;
import com.example.test.database.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    RecyclerView studentRecyclerView;
    String lessonId, username, classroomCode;
    DatabaseReference lessonRef;
    List<Student> studentList;
    StudentAdapter studentAdapter;
    private ValueEventListener statisticsValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        studentRecyclerView = findViewById(R.id.statisticsRecyclerView);
        lessonId = getIntent().getStringExtra("lessonId");
        username = getIntent().getStringExtra("username");
        classroomCode = getIntent().getStringExtra("classroomCode");

        lessonRef = FirebaseDatabase.getInstance().getReference("Teachers")
                .child(username).child("classrooms").child(classroomCode).child("lessons").child(lessonId);

        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(this, studentList);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentRecyclerView.setAdapter(studentAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn nút tìm kiếm
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void filter(String searchText) {
        List<Student> filteredList = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getMssv().toLowerCase().contains(searchText.toLowerCase())
                    || student.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(student);
            }
        }
        studentAdapter.filterList(filteredList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        statisticsValueEventListener = lessonRef.child("attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentList.clear();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String mssv = studentSnapshot.getKey();
                    boolean attendanceStatus = studentSnapshot.getValue(Boolean.class);
                    DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");
                    studentsRef.child(mssv).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot studentDataSnapshot) {
                            String studentName = studentDataSnapshot.child("name").getValue(String.class);
                            studentList.add(new Student(mssv, studentName));
                            studentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        lessonRef.child("attendance").removeEventListener(statisticsValueEventListener);
    }
}
