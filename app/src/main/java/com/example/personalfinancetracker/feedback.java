package com.example.personalfinancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class feedback extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mFeedbackDatabase;

    String s1;

    RatingBar ratingBar;
    Button btnSubmit;

    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        if(mAuth!=null) {
            String uid = mUser.getUid();

            mFeedbackDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback").child(uid);
        }

        ratingBar=findViewById(R.id.rating_bar);
        btnSubmit=findViewById(R.id.rating_btn);
        commentEditText = findViewById(R.id.comment_edt);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1 = String.valueOf(ratingBar.getRating());
                String comment = commentEditText.getText().toString().trim();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Rating", s1);
                hashMap.put("Comment", comment);

                if (ratingBar.getRating() != 0.0) {
                    String id = mFeedbackDatabase.push().getKey();
                    mFeedbackDatabase.child(id).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Thanks for Rating and Commenting!", Toast.LENGTH_SHORT).show();
                            ratingBar.setRating(0);
                            commentEditText.setText("");
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please Rate us!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView back_arrow=findViewById(R.id.back);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}