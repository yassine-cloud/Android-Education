package com.iset.education.ui.cours;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.data.models.Cour;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.CourRepository;
import com.iset.education.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsCourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsCourFragment extends Fragment {

    private Cour cour;
    private User enseignant;
    private boolean isOwner = false;
    private SessionManager sessionManager;

    public static DetailsCourFragment newInstance(Cour cour, User enseignant) {
        DetailsCourFragment fragment = new DetailsCourFragment();
        Bundle args = new Bundle();
        args.putSerializable("cour", cour);
        args.putSerializable("enseignant", enseignant);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cour = (Cour) getArguments().getSerializable("cour");
            enseignant = (User) getArguments().getSerializable("enseignant");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_cour, container, false);
        sessionManager = new SessionManager(requireContext());
        isOwner = sessionManager.getUser().getUsername().equals(enseignant.getUsername());
        boolean delAccess = isOwner || sessionManager.getUser().getRole().equals(UserRole.ADMIN);

        // Initialize UI elements
        ImageView profileImage = view.findViewById(R.id.enseignant_image);
        TextView courseName = view.findViewById(R.id.course_name);
        TextView instructorName = view.findViewById(R.id.instructor_name);
        TextView schedule = view.findViewById(R.id.schedule);
        TextView email = view.findViewById(R.id.email);
        TextView phone = view.findViewById(R.id.phone);
        FloatingActionButton fab = view.findViewById(R.id.fab_view_pdf);
        FloatingActionButton fabCall = view.findViewById(R.id.fab_call);
        fabCall.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        FloatingActionButton fabEmail = view.findViewById(R.id.fab_email);
        fabEmail.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        FloatingActionButton fabEdit = view.findViewById(R.id.fab_edit);
        fabEdit.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        FloatingActionButton fabDelete = view.findViewById(R.id.fab_delete);
        fabDelete.setVisibility(delAccess ? View.VISIBLE : View.GONE);

        // Set course details
        courseName.setText(cour.getName());
        instructorName.setText("Instructor: " + enseignant.getUsername());
        schedule.setText("Schedule: " + cour.getSchedule());
        email.setText("Email: " + enseignant.getEmail());
        phone.setText("Phone: " + enseignant.getPhoneNumber());

        // Set profile image
        if (enseignant.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(enseignant.getImage(), 0, enseignant.getImage().length);
            profileImage.setImageBitmap(bitmap);
        }

        if (cour.getDocument() == null) {
            fab.setEnabled(false);
        }

        // Floating Action Button to view PDF
        fab.setOnClickListener(v -> {
            if (cour.getDocument() != null) {

                try {
                    File file = File.createTempFile("temp_pdf", ".pdf", getContext().getCacheDir());
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(cour.getDocument());
                    fos.close();

                    Uri pdfUri = FileProvider.getUriForFile(
                            getContext(),
                            getContext().getPackageName() + ".provider",
                            file);

                    Log.d("PDFViewer", "Temporary file path: " + file.getAbsolutePath());

                    Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(pdfUri);
                    intent.setDataAndType(pdfUri, "application/pdf");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent); // force open pdf file
                }catch (IOException e) {
                    Toast.makeText(getContext(), "Error displaying PDF", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "No document available", Toast.LENGTH_SHORT).show();
            }
        });

        // Floating Action Button for calling
        fabCall.setOnClickListener(v -> {
            if (enseignant.getPhoneNumber() != null && !enseignant.getPhoneNumber().isEmpty()) {
                // Check if CALL_PHONE permission is granted
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    // Permission granted, place the call
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + enseignant.getPhoneNumber()));
                    startActivity(callIntent);
                }
            } else {
                Toast.makeText(getContext(), "No phone number available", Toast.LENGTH_SHORT).show();
            }
        });

        // Floating Action Button for sending email
        fabEmail.setOnClickListener(v -> {
            if (enseignant.getEmail() != null && !enseignant.getEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + enseignant.getEmail()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding the course: " + cour.getName());
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + enseignant.getUsername() + ",");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } else {
                Toast.makeText(getContext(), "No email address available", Toast.LENGTH_SHORT).show();
            }
        });

        fabEdit.setOnClickListener(v -> {

            Executors.newSingleThreadExecutor().execute(()->{
                AddEditCourFragment addEditCourFragment = new AddEditCourFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("cour", cour);
                addEditCourFragment.setArguments(bundle);

                getActivity().runOnUiThread(() -> {
                    getParentFragmentManager().popBackStack();
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, addEditCourFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                });
            });

        });

        fabDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, execute ACTION_CALL
                if (enseignant.getPhoneNumber() != null && !enseignant.getPhoneNumber().isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + enseignant.getPhoneNumber()));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(getContext(), "No phone number available", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Call permission is required to make a call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteCourse();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCourse() {
        // Replace with actual delete logic
        CourRepository courRepository = new CourRepository(requireActivity().getApplication());
        courRepository.delete(cour); // Implement deleteCour in your repository
        Toast.makeText(getContext(), "Course deleted", Toast.LENGTH_SHORT).show();

        // Navigate back to the previous fragment
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}