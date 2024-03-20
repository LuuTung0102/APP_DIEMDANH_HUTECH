package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.database.ClassroomClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CreateClassroomActivity extends AppCompatActivity {

    EditText classroomName, classroomCode;
    Button createButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classroom);

        classroomName = findViewById(R.id.classroom_name);
        classroomCode = findViewById(R.id.classroom_code);
        createButton = findViewById(R.id.create_button);

        // Lấy tên người dùng hiện tại đã đăng nhập
        username = getIntent().getStringExtra("username");

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Teachers");

                String name = classroomName.getText().toString();
                String code = classroomCode.getText().toString();

                ClassroomClass classroomClass = new ClassroomClass(code, name);

                if (name.isEmpty() || code.isEmpty()) {
                    Toast.makeText(CreateClassroomActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    if (username != null) {
                        reference.child(username).child("classrooms").child(code).setValue(classroomClass);
                        Toast.makeText(CreateClassroomActivity.this, "Tạo lớp học thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateClassroomActivity.this, CreateLessonActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("classroomCode", code);
                        intent.putExtra("classroomName", name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CreateClassroomActivity.this, "Lỗi: Tên người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}