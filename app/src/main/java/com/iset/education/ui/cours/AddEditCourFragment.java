package com.iset.education.ui.cours;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iset.education.R;
import com.iset.education.data.models.Cour;
import com.iset.education.data.repositories.CourRepository;
import com.iset.education.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private SessionManager sessionManager;
    private CourRepository courRepository;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private int currentCourId = -1;

    private List<File> generatedPdfFiles = new ArrayList<>();


    public AddEditCourFragment() {
        // Required empty public constructor
    }

    public static AddEditCourFragment newInstance(int courId) {
        AddEditCourFragment fragment = new AddEditCourFragment();
        Bundle args = new Bundle();
        args.putInt("courId", courId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentCourId = getArguments().getInt("courId");
        }
        sessionManager = new SessionManager(requireContext());
        courRepository = new CourRepository(getActivity().getApplication());
        Executors.newSingleThreadExecutor().execute(() -> {
            if (currentCourId != -1)
                currentCour = courRepository.getCourseById(currentCourId);
        });


        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm a", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_cour, container, false);

        etName = view.findViewById(R.id.et_name);
        etInstructor = view.findViewById(R.id.et_instructor);
        etInstructor.setText(sessionManager.getUser().getUsername());
        etInstructor.setEnabled(false);
        etSchedule = view.findViewById(R.id.et_schedule);

        btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        btnSave = view.findViewById(R.id.btn_save);
        btnViewPdf = view.findViewById(R.id.btn_view_pdf);
        if (currentCour != null) {
            etName.setText(currentCour.getName());
            etSchedule.setText(currentCour.getSchedule());
        }
        if (currentCour != null && currentCour.getDocument() != null) {
            documentBytes = currentCour.getDocument();
        }
        if (documentBytes != null) {
            btnViewPdf.setEnabled(true);
        } else {
            btnViewPdf.setEnabled(false);
        }

        btnSelectPdf.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveCour());
        btnViewPdf.setOnClickListener(v -> viewPdf());

        etSchedule.setOnClickListener(v -> showDateTimePicker());

        return view;
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog startTimePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                Calendar startTimeCalendar = (Calendar) calendar.clone();
                String startTime = new SimpleDateFormat("yyyy-MM-dd, hh:mm a", Locale.getDefault()).format(calendar.getTime());

                TimePickerDialog endTimePickerDialog = new TimePickerDialog(getContext(), (view2, hourOfDay1, minute1) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                    calendar.set(Calendar.MINUTE, minute1);
                    Calendar endTimeCalendar = (Calendar) calendar.clone();

                    if (endTimeCalendar.before(startTimeCalendar)) {
                        Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                    } else {
                        String endTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.getTime());
                        etSchedule.setText(startTime + " - " + endTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                endTimePickerDialog.show();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            startTimePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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
                btnViewPdf.setEnabled(true);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error reading PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_PDF_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
//            try {
//                InputStream inputStream = requireContext().getContentResolver().openInputStream(data.getData());
//                documentBytes = toByteArray(inputStream);
//
//                if (documentBytes != null && documentBytes.length > 0) {
//                    Toast.makeText(getContext(), "PDF selected successfully! Size: " + documentBytes.length + " bytes", Toast.LENGTH_SHORT).show();
//
//                    // Write the bytes to a temporary file
//                    File tempFile = File.createTempFile("temp_pdf", ".pdf", getContext().getCacheDir());
//                    FileOutputStream fos = new FileOutputStream(tempFile);
//                    fos.write(documentBytes);
//                    fos.close();
//
//                    // Log the file path for debugging
//                    Log.d("PDFViewer", "Temporary file path: " + tempFile.getAbsolutePath());
//
//                    // Create an intent to view the PDF
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Uri pdfUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", tempFile);
//                    intent.setDataAndType(pdfUri, "application/pdf");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getContext(), "Failed to read PDF", Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (IOException e) {
//                Toast.makeText(getContext(), "Error reading PDF", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
//    }

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
        if (currentCour != null) cour.setId(currentCourId);
        cour.setName(name);
        cour.setInstructor(instructor);
        cour.setSchedule(schedule);
        cour.setDocument(documentBytes);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (currentCour != null)
                courRepository.update(cour);
            else
                courRepository.insert(cour);

            getActivity().runOnUiThread(() -> {
                deleteGeneratedFiles(); // delete generated files
                Toast.makeText(getContext(), "Course saved successfully", Toast.LENGTH_SHORT).show();
                clearCache();
                getParentFragmentManager().popBackStack();
            });


        });
    }

//    private void viewPdf() {
//        if (documentBytes == null) {
//            Toast.makeText(getContext(), "No PDF attached", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            File file = new File(requireContext().getCacheDir(), "temp.pdf");
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(documentBytes);
//            fos.close();
//
//            Uri pdfUri = FileProvider.getUriForFile(
//                    requireContext(),
//                    requireContext().getPackageName() + ".provider",
//                    file);
//
//            Intent intent = null;
//
//                intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                Uri pdfURI = FileProvider.getUriForFile(
//                        requireContext(),
//                        requireContext().getPackageName() + ".provider",
//                        file);
//                intent.putExtra(Intent.EXTRA_STREAM, pdfURI);
//                intent.setDataAndType(pdfUri, "application/pdf");
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////                intent.setType("application/pdf");
//
//            try {
//                if (intent.resolveActivity(requireContext().getPackageManager()) != null)
//                    startActivity(intent);
//                else
//                    Toast.makeText(getContext(), "No Application found to open the pdf", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            Toast.makeText(getContext(), "Error displaying PDF", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//
//            /*
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(pdfUri, "application/pdf");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            // Check if there's an app to handle the intent
//            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(getContext(), "No PDF viewer found. Please install one.", Toast.LENGTH_SHORT).show();
//                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.reader")); // Example for Adobe Acrobat Reader
//                startActivity(playStoreIntent);
//            }
//        } catch (IOException e) {
//            Toast.makeText(getContext(), "Error displaying PDF", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }*/
////        if (documentBytes == null) {
////            Toast.makeText(getContext(), "No PDF attached", Toast.LENGTH_SHORT).show();
////            return;
////        }
//
////        Intent intent = new Intent(getContext(), PdfViewerActivity.class);
////        intent.putExtra("pdfBytes", documentBytes);
////        startActivity(intent);
//    }

    private void viewPdf() {
        if (documentBytes != null) {
            try {
                // Create a folder named after the application
                File appFolder = new File(
                        requireContext().getExternalFilesDir(null), // Application-specific external storage directory
                        "Education" // Replace with your app's name
                );

                if (!appFolder.exists()) {
                    if (!appFolder.mkdirs()) {
                        Toast.makeText(getContext(), "Failed to create folder", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Create a file in the folder
//                    File pdfFile = new File(appFolder, "Document.pdf"); // Replace with desired file name
                File pdfFile = new File(appFolder, "Document-"+(new Date()).getTime()+".pdf");
                FileOutputStream fos = new FileOutputStream(pdfFile);
                fos.write(documentBytes);
                fos.close();

                // add generated file to the tracking list
                generatedPdfFiles.add(pdfFile);

                Uri pdfUri = FileProvider.getUriForFile(
                        getContext(),
                        requireContext().getPackageName() + ".provider",
                        pdfFile
                );

                // Open the PDF file
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(pdfUri, "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent); // Open PDF

            } catch (IOException e) {
                Toast.makeText(getContext(), "Error saving or opening PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "No document available", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearCache() {
        File cacheDir = getContext().getCacheDir();
        if (cacheDir != null && cacheDir.isDirectory()) {
            deleteDir(cacheDir);
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        deleteGeneratedFiles(); // Cleanup files on fragment exit
    }

    private void deleteGeneratedFiles() {
        for (File file : generatedPdfFiles) {
            if (file.exists() && !file.delete()) {
                Log.e("PDF Cleanup", "Failed to delete file: " + file.getAbsolutePath());
            }
        }
        // Clear the list after deletion
        generatedPdfFiles.clear();
    }

}