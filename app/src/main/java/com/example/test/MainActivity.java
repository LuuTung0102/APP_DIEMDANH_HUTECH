package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.test.adapter.ClassroomAdapter;
import com.example.test.database.ClassroomClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txtname;

    RecyclerView recyclerView;

    ArrayList<ClassroomClass> classroomList;

    ClassroomAdapter adapter;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtname = findViewById(R.id.txtname);
        recyclerView = findViewById(R.id.classroomRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Teachers").child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    txtname.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi cơ sở dữ liệu
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers").child(username).child("classrooms");
        classroomList = new ArrayList<>();
        adapter = new ClassroomAdapter(classroomList, new ClassroomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String classroomCode) {
                Intent intent = new Intent(MainActivity.this, ClassroomActivity.class);
                intent.putExtra("classroomCode", classroomCode);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                        String classroomName = classSnapshot.child("name").getValue(String.class);
                        String classroomCode = classSnapshot.getKey();
                        classroomList.add(new ClassroomClass(classroomCode, classroomName));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    public void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.page1) {
                    Intent intent = new Intent(MainActivity.this, CreateClassroomActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page2) {
                    logoutUser();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }

    private void logoutUser() {
        Intent intent = new Intent(MainActivity.this, DNMainActivity.class);
        startActivity(intent);
        finish();
    }
}
