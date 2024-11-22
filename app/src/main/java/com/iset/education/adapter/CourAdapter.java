package com.iset.education.adapter;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.iset.education.R;
import com.iset.education.data.models.Cour;
import com.iset.education.data.models.Task;
import com.iset.education.data.repositories.CourRepository;
import com.iset.education.ui.cours.DetailsCourFragment;

public class CourAdapter extends ListAdapter<Cour, CourAdapter.CourHolder> {
    private OnCourClickListener courClickListener;
    private Context context;
    private CourRepository courRepository;
    private FragmentActivity requireActivity;

    public CourAdapter(Context context, CourRepository courRepository, FragmentActivity requireActivity) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.courRepository = courRepository;
        this.requireActivity = requireActivity;
    }

    private static final DiffUtil.ItemCallback<Cour> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Cour>() {
                @Override
                public boolean areItemsTheSame(@NonNull Cour oldItem, @NonNull Cour newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Cour oldItem, @NonNull Cour newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public CourHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cour_item, parent, false);
        return new CourHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourHolder holder, int position) {
        Cour cour = getItem(position);
        holder.name.setText(cour.getName());
        holder.instructor.setText("Instructor: " + cour.getInstructor());
        holder.schedule.setText("Schedule: " + cour.getSchedule());
    }

    public void setOnCourClickListener(OnCourClickListener listener) {
        this.courClickListener = listener;
    }

    private void navigateToCourDetails(Cour cour) {
        // Create a new instance of DetailsCourFragment
        DetailsCourFragment detailsCourFragment = new DetailsCourFragment();

        // Pass cour data to the fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("cour", cour);  // Pass the cour object to the fragment
        detailsCourFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsCourFragment);  // Replace with your container's ID
        transaction.addToBackStack(null);  // Allows the user to press back to return to the previous fragment
        transaction.commit();
    }


    class CourHolder extends RecyclerView.ViewHolder {
        TextView name, instructor, schedule;
        public CourHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_course_name);
            instructor = itemView.findViewById(R.id.text_course_instructor);
            schedule = itemView.findViewById(R.id.text_course_schedule);
        }
    }

    public interface OnCourClickListener{
        void onCourClick(Cour cour);
    }
}
