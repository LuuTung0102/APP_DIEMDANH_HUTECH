package com.example.test.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapter.ClassroomAdapter;
import com.example.test.database.ClassroomClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ClassroomItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ClassroomAdapter adapter;

    private ArrayList<ClassroomClass> classroomList;
    private String username;

    public ClassroomItemTouchHelper(ClassroomAdapter adapter, ArrayList<ClassroomClass> classroomList, String username) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.classroomList = classroomList;
        this.username = username;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (classroomList != null) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                String classroomCode = classroomList.get(position).getCode();
                DatabaseReference classroomRef = FirebaseDatabase.getInstance().getReference("Teachers").child(username).child("classrooms").child(classroomCode);
                classroomRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Remove from the adapter and notify item removed inside onComplete
                            classroomList.remove(position);
                            adapter.notifyItemRemoved(position);
                        } else {
                            Log.e("FirebaseError", "Error deleting data from Firebase: " + task.getException());
                        }
                    }
                });
            }
        }
    }
}
