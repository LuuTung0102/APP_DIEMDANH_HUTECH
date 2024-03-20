package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.database.ClassroomClass;

import java.util.ArrayList;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {

    private ArrayList<ClassroomClass> classroomList;
    private OnItemClickListener listener;

    public ClassroomAdapter(ArrayList<ClassroomClass> classroomList, OnItemClickListener listener) {
        this.classroomList = classroomList;

        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(String classroomCode);
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classroom, parent, false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        ClassroomClass classroom = classroomList.get(position);
        holder.classCodeTextView.setText(classroom.getCode());
        holder.classNameTextView.setText(classroom.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(classroom.getCode());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    public static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        Button classCodeTextView;
        TextView classNameTextView;

        public ClassroomViewHolder(View itemView) {
            super(itemView);
            classCodeTextView = itemView.findViewById(R.id.classCodeTextView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
        }
    }
}
