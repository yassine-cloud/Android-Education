package com.iset.education.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.CourRepository;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {

    private UserAdapter.OnCourClickListener courClickListener;
    private Context context;
    private FragmentActivity requireActivity;


    public UserAdapter(Context context, FragmentActivity requireActivity) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.requireActivity = requireActivity;
    }

    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<User>() {
                @Override
                public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);

        // Set username and email
        holder.userName.setText(user.getUsername());
        holder.userEmail.setText(user.getEmail());

        // Set role with uppercase and add visuals
        holder.userRole.setText(user.getRole().toString());
//        int roleColor = () -> {
//            switch (user.getRole()) {
//                case ADMIN:
//                    return context.getResources().getColor(R.color.admin_color);
//                case ETUDIANT:
//                    return context.getResources().getColor(R.color.etudiant_color);
//                case ENSEIGNANT:
//                    return context.getResources().getColor(R.color.enseignant_color);
//                default:
//                    return context.getResources().getColor(R.color.white);
//            }
//        }

        int roleColor = holder.userRole.getContext().getResources().getColor(R.color.white);
        if (user.getRole() == UserRole.ENSEIGNANT) {
            roleColor = holder.userRole.getContext().getResources().getColor(R.color.enseignant_color);
        } else if (user.getRole() == UserRole.ETUDIANT) {
            roleColor = holder.userRole.getContext().getResources().getColor(R.color.etudiant_color);
        } else if (user.getRole() == UserRole.ADMIN) {
            roleColor = holder.userRole.getContext().getResources().getColor(R.color.admin_color);
        }
//        holder.userRole.setBackgroundColor(roleColor);
        holder.userRole.setBackgroundTintList(ColorStateList.valueOf(roleColor));

        // Decode image bytes into a bitmap and set it
        if (user.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImage(), 0, user.getImage().length);
            holder.userImage.setImageBitmap(bitmap);
        } else {
            holder.userImage.setImageResource(R.drawable.ic_profile); // Fallback image
        }


        holder.itemView.setOnClickListener(v -> {
            if (courClickListener != null) {
                courClickListener.onUserClick(user);
            }
        });
    }

    public void setOnCourClickListener(OnCourClickListener listener) {
        this.courClickListener = listener;
    }


    class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName, userEmail, userRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            userRole = itemView.findViewById(R.id.user_role);
        }
    }

    public interface OnCourClickListener{
        void onUserClick(User user);
    }
}
