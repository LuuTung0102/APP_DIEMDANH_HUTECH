package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.adapter.LessonAdapter;
import com.example.test.database.LessonClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassroomActivity extends AppCompatActivity {

    RecyclerView lessonRecyclerView;
    ArrayList<LessonClass> lessonList;
    LessonAdapter lessonAdapter;
    String username, classroomCode,classroomName;

    TextView txtClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        classroomCode = intent.getStringExtra("classroomCode");
        txtClassName = findViewById(R.id.txtClassName);
        DatabaseReference classroomRef = FirebaseDatabase.getInstance().getReference("Teachers")
                .child(username).child("classrooms").child(classroomCode);

        classroomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    classroomName = dataSnapshot.child("name").getValue(String.class);
                    txtClassName.setText("Tên phòng học: " + classroomName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi cơ sở dữ liệu
            }
        });
        lessonRecyclerView = findViewById(R.id.lessonRecyclerView);
        lessonRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference lessonRef = FirebaseDatabase.getInstance().getReference("Teachers")
                .child(username).child("classrooms").child(classroomCode).child("lessons");

        lessonList = new ArrayList<>();
        lessonAdapter = new LessonAdapter(lessonList, new LessonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String lessonId) {
                LessonClass selectedLesson = getLessonById(lessonId);

                if (selectedLesson != null) {
                    if (selectedLesson.isStatus()) {
                        // Nếu status là true, chuyển đến trang điểm danh
                        Intent attendanceIntent = new Intent(ClassroomActivity.this, AttendanceActivity.class);
                        attendanceIntent.putExtra("lessonId", lessonId);
                        attendanceIntent.putExtra("username", username);
                        attendanceIntent.putExtra("classroomCode", classroomCode);
                        startActivity(attendanceIntent);
                    } else {

                        Intent statisticsIntent = new Intent(ClassroomActivity.this, StatisticsActivity.class);
                        statisticsIntent.putExtra("lessonId", lessonId);
                        statisticsIntent.putExtra("username", username);
                        statisticsIntent.putExtra("classroomCode", classroomCode);
                        startActivity(statisticsIntent);
                    }
                }
            }
        });

        lessonRecyclerView.setAdapter(lessonAdapter);

        lessonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lessonList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LessonClass lesson = snapshot.getValue(LessonClass.class);
                    lessonList.add(lesson);
                }
                lessonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi cơ sở dữ liệu
            }
        });

        Button addLessonButton = findViewById(R.id.createLessonButton);
        addLessonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassroomActivity.this, CreateLessonActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("classroomCode", classroomCode);
                intent.putExtra("classroomName", classroomName);
                startActivity(intent);
            }
        });
    }

    private LessonClass getLessonById(String lessonId) {
        for (LessonClass lesson : lessonList) {
            if (lesson.getId().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }
}