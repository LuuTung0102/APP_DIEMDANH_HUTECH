package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.adapter.StudentAdapter;
import com.example.test.database.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    EditText mssvEditText;
    Button checkButton, camqr;
    RecyclerView studentRecyclerView;
    String lessonId, username, classroomCode;
    DatabaseReference studentsRef, lessonRef;
    List<Student> studentList;
    StudentAdapter studentAdapter;
    private ValueEventListener attendanceValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        mssvEditText = findViewById(R.id.mssvEditText);
        checkButton = findViewById(R.id.checkButton);
        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        camqr = findViewById(R.id.Camqr);
        lessonId = getIntent().getStringExtra("lessonId");
        username = getIntent().getStringExtra("username");
        classroomCode = getIntent().getStringExtra("classroomCode");

        studentsRef = FirebaseDatabase.getInstance().getReference("Students");
        lessonRef = FirebaseDatabase.getInstance().getReference("Teachers")
                .child(username).child("classrooms").child(classroomCode).child("lessons").child(lessonId);

        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(this, studentList);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentRecyclerView.setAdapter(studentAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(studentRecyclerView);

        camqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateQRScan();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mssv = mssvEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(mssv)) {
                    studentsRef.child(mssv).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists()) {
                                String studentName = dataSnapshot.child("name").getValue(String.class);
                                lessonRef.child("attendance").child(mssv).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot attendanceSnapshot) {
                                        boolean alreadyAttended = attendanceSnapshot.exists();
                                        if (alreadyAttended) {
                                            Toast.makeText(AttendanceActivity.this, "Sinh viên đã điểm danh", Toast.LENGTH_SHORT).show();
                                        } else {
                                            lessonRef.child("attendance").child(mssv).setValue(true);
                                            Toast.makeText(AttendanceActivity.this, "Điểm danh thành công cho sinh viên " + studentName, Toast.LENGTH_SHORT).show();
                                            studentList.add(new Student(mssv, studentName));
                                            studentAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle database errors
                                    }
                                });
                            } else {
                                Toast.makeText(AttendanceActivity.this, "Mã số sinh viên không đúng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database errors
                        }
                    });
                } else {
                    Toast.makeText(AttendanceActivity.this, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference classroomRef = FirebaseDatabase.getInstance().getReference("Teachers")
                        .child(username).child("classrooms").child(classroomCode).child("lessons").child(lessonId);
                classroomRef.child("status").setValue(false, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            // Xử lý lỗi khi cập nhật không thành công
                            Toast.makeText(AttendanceActivity.this, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AttendanceActivity.this, "Đã kết thúc buổi học", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void initiateQRScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Quét mã QR");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Handle the case when the scan was canceled
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the scanned data
                String scannedData = result.getContents();
                // Display the scanned data in the TextView
                mssvEditText.setText(""+ scannedData);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        attendanceValueEventListener = lessonRef.child("attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                studentList.clear();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String mssv = studentSnapshot.getKey();
                    boolean attendanceStatus = studentSnapshot.getValue(Boolean.class);
                    studentsRef.child(mssv).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot studentDataSnapshot) {
                            String studentName = studentDataSnapshot.child("name").getValue(String.class);
                            studentList.add(new Student(mssv, studentName));
                            studentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        lessonRef.child("attendance").removeEventListener(attendanceValueEventListener);
    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        public SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT);
        }
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Student student = studentList.get(position);
            String mssv = student.getMssv();
            lessonRef.child("attendance").child(mssv).removeValue();
            studentList.remove(position);
            studentAdapter.notifyItemRemoved(position);
        }
    }

}


