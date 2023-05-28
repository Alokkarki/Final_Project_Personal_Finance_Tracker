package com.example.personalfinancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class editprofile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText nameEditText, phoneNumberEditText;
    TextView dobTextView;
    RadioGroup genderRadioGroup;

    Button saveButton, cancelButton;
    DatePickerDialog datePickerDialog;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.editText_name);
        phoneNumberEditText = findViewById(R.id.editText_phone);
        dobTextView = findViewById(R.id.textView_dob);
        genderRadioGroup = findViewById(R.id.radioGroup_gender);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        // Fetch the user's existing data and populate the fields
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference usersDataRef = FirebaseDatabase.getInstance().getReference("usersData").child(userId);
        usersDataRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String dob = dataSnapshot.child("dob").getValue(String.class);

                    nameEditText.setText(name);
                    phoneNumberEditText.setText(phoneNumber);
                    if (gender.equals("Male")) {
                        genderRadioGroup.check(R.id.radioButton_male);
                    } else {
                        genderRadioGroup.check(R.id.radioButton_female);
                    }
                    dobTextView.setText(dob);
                }
            }
        });

        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        selectedDate = dateFormat.format(calendar.getTime());
                        dobTextView.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void saveProfileData() {
        String name = nameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.radioButton_male ? "Male" : "Female";

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Please enter your name");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 10 || !phoneNumber.startsWith("9")) {
            phoneNumberEditText.setError("The phone number should be 10 digits starting with 9");
            return;
        }

        // Save the profile data to Firebase under "usersData"
        DatabaseReference usersDataRef = FirebaseDatabase.getInstance().getReference("usersData");
        String userId = mAuth.getCurrentUser().getUid();
        usersDataRef.child(userId).child("name").setValue(name);
        usersDataRef.child(userId).child("phoneNumber").setValue(phoneNumber);
        usersDataRef.child(userId).child("gender").setValue(gender);
        usersDataRef.child(userId).child("dob").setValue(selectedDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(editprofile.this, "Data added", Toast.LENGTH_SHORT).show();
                        finish(); // Return to the profile page after saving the data
                    }
                });
    }
}
