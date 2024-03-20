package com.example.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.database.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context context;
    private List<Student> studentList;
    private List<Student> originalList;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
        this.originalList = new ArrayList<>(studentList); // Lưu trữ danh sách gốc
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, viewGroup, false);
        return new ViewHolder(view);
    }

    public void filterList(List<Student> filteredList) {
        studentList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Student student = studentList.get(position);

        viewHolder.mssvTextView.setText(student.getMssv());
        viewHolder.nameTextView.setText(student.getName());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mssvTextView;
        public TextView nameTextView;

        public ViewHolder(View view) {
            super(view);

            mssvTextView = view.findViewById(R.id.mssvTextView);
            nameTextView = view.findViewById(R.id.nameTextView);
        }
    }
}
