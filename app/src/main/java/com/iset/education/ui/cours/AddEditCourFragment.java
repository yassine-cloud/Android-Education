package com.iset.education.ui.cours;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iset.education.R;
import com.iset.education.data.models.Cour;
import com.iset.education.data.repositories.CourRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEditCourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditCourFragment extends Fragment {
    private static final int PICK_PDF_REQUEST = 1;

    private EditText etName, etInstructor, etSchedule;
    private Button btnSelectPdf, btnSave, btnViewPdf;

    private byte[] documentBytes = null;
    private Cour currentCour = null; // Use this if editing an existing course


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddEditCourFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddEditCourFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEditCourFragment newInstance(String param1, String param2) {
        AddEditCourFragment fragment = new AddEditCourFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_cour, container, false);

        etName = view.findViewById(R.id.et_name);
        etInstructor = view.findViewById(R.id.et_instructor);
        etSchedule = view.findViewById(R.id.et_schedule);

        btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        btnSave = view.findViewById(R.id.btn_save);
        btnViewPdf = view.findViewById(R.id.btn_view_pdf);

        btnSelectPdf.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveCour());
        btnViewPdf.setOnClickListener(v -> viewPdf());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(data.getData());
                documentBytes = toByteArray(inputStream);
                Toast.makeText(getContext(), "PDF selected successfully!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error reading PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private void saveCour() {
        String name = etName.getText().toString().trim();
        String instructor = etInstructor.getText().toString().trim();
        String schedule = etSchedule.getText().toString().trim();

        if (name.isEmpty() || instructor.isEmpty() || schedule.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Cour cour = new Cour();
        cour.setName(name);
        cour.setInstructor(instructor);
        cour.setSchedule(schedule);
        cour.setDocument(documentBytes);

        // Save the cour to the database (using a repository or DAO)
        // Example: repository.insertCour(cour);

        Toast.makeText(getContext(), "Course saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void viewPdf() {
        if (documentBytes == null) {
            Toast.makeText(getContext(), "No PDF attached", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File file = new File(requireContext().getCacheDir(), "temp.pdf");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(documentBytes);
            fos.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error displaying PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}