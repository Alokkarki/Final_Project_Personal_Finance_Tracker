package com.example.personalfinancetracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFGenerator {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private Context context;
    private ImageView downloadButton;

    public PDFGenerator(Context context, ImageView downloadButton) {
        this.context = context;
        this.downloadButton = downloadButton;

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the necessary permissions are granted
                if (checkPermissions()) {
                    generateAndDownloadPDF();
                } else {
                    // Request the necessary permissions
                    requestPermissions();
                }
            }
        });
    }

    private void generateAndDownloadPDF() {
        // Content for the PDF
        String pdfContent = "Sample PDF content";

        // Generate and download the PDF
        generatePDF(pdfContent);
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

            return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                (AppCompatActivity) context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE
        );
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateAndDownloadPDF();
            } else {
                // Display a dialog explaining that the permissions are required for PDF generation and downloading
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Permission Denied");
        builder.setMessage("To generate and download PDF files, please grant the necessary permissions.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void generatePDF(String content) {
        Document document = new Document();

        // Set the file path for the PDF
        String filePath = Environment.getExternalStorageDirectory() + "/my_pdf.pdf";

        try {
            // Create a PdfWriter instance to write the document to a file
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Open the document
            document.open();

            // Add content to the document
            document.add(new Paragraph(content));

            // Close the document
            document.close();

            // Show a toast indicating that the PDF was generated and saved successfully
            Toast.makeText(context, "PDF generated and saved successfully", Toast.LENGTH_SHORT).show();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
