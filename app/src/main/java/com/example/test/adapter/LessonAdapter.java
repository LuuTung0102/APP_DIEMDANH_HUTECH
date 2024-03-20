package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.database.LessonClass;

import java.util.ArrayList;
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private ArrayList<LessonClass> lessonList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String lessonId);
    }

    public LessonAdapter(ArrayList<LessonClass> lessonList, OnItemClickListener listener) {
        this.lessonList = lessonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        LessonClass lesson = lessonList.get(position);
        holder.lessonIdTextView.setText(lesson.getId());
        holder.lessonNameTextView.setText(lesson.getName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(lesson.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        Button lessonIdTextView;
        TextView lessonNameTextView;

        public LessonViewHolder(View itemView) {
            super(itemView);
            lessonIdTextView = itemView.findViewById(R.id.lessonIdTextView);
            lessonNameTextView = itemView.findViewById(R.id.lessonNameTextView);
        }
    }
}

