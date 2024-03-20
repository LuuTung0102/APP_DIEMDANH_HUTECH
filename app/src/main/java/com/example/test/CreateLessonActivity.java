package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.database.LessonClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CreateLessonActivity extends AppCompatActivity {

    EditText lessonId, lessonName, lessonDate;
    Button addLessonButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    String username, classroomCode, classroomName;

    TextView classroomNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        lessonId = findViewById(R.id.lesson_id);
        lessonName = findViewById(R.id.lesson_name);
        lessonDate = findViewById(R.id.lesson_date);
        addLessonButton = findViewById(R.id.add_lesson_button);

        username = getIntent().getStringExtra("username");
        classroomCode = getIntent().getStringExtra("classroomCode");
        classroomName = getIntent().getStringExtra("classroomName");

        classroomNameTextView = findViewById(R.id.classroom_name_textview);
        classroomNameTextView.setText("Lớp học: " + classroomName);

        addLessonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Teachers");

                String id = lessonId.getText().toString();
                String name = lessonName.getText().toString();
                String date = lessonDate.getText().toString();

                if (id.isEmpty() || name.isEmpty() || date.isEmpty()) {
                    Toast.makeText(CreateLessonActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (isIdDuplicate(id)) {
                    Toast.makeText(CreateLessonActivity.this, "Lỗi: Mã lớp học đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    if (isDateValid(date)) {
                        LessonClass lessonClass = new LessonClass(id, name, date, true);

                        if (classroomCode != null) {
                            reference.child(username).child("classrooms").child(classroomCode).child("lessons").child(id).setValue(lessonClass);
                            Toast.makeText(CreateLessonActivity.this, "Thêm buổi học thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateLessonActivity.this, ClassroomActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("classroomCode", classroomCode);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CreateLessonActivity.this, "Lỗi: Mã lớp học không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CreateLessonActivity.this, "Lỗi: Định dạng ngày hoặc ngày nhập bé hơn ngày hiện tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateLessonActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private boolean isDateValid(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date inputDate = sdf.parse(date);

            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            return inputDate.after(currentDate.getTime());
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isIdDuplicate(String id) {
        Set<String> usedIds = new HashSet<>();
        return usedIds.contains(id);
    }
}