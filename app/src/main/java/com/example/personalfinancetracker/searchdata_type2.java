package com.example.personalfinancetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalfinancetracker.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchdata_type2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference mref;
    private ListView listdata;
    private AutoCompleteTextView txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdata_type);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();


        if (mAuth != null) {
            String uid = mUser.getUid();
            mref = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        }

        listdata = findViewById(R.id.listdata);
        txtSearch = findViewById(R.id.txtSearch);

        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mref.addListenerForSingleValueEvent(event);

        ImageView back_arrow = findViewById(R.id.back);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void populateSearch(DataSnapshot snapshot) {
        Log.d("ExpenseData", "Reading Data");
        ArrayList<String> names = new ArrayList<>();

        if (snapshot.exists()) {
            for (DataSnapshot ds : snapshot.getChildren()) {
                Data data = ds.getValue(Data.class);
                String name = data.getType();
                names.add(name);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
            txtSearch.setAdapter(adapter);

            txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = txtSearch.getText().toString();
                    searchType(name);
                }
            });

        } else {
            Log.d("ExpenseData", "No data Found");
        }
    }

    private void searchType(String name) {
        Query query = mref.orderByChild("type").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    ArrayList<String> listtype = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data data = ds.getValue(Data.class);
                        double amount = data.getAmount();
                        String strType = data.getType();
                        listtype.add("Amount: " + amount + "\nType: " + strType + "\nNote: " + data.getNote() + "\nDate: " + data.getDate());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listtype);
                    listdata.setAdapter(adapter);

                } else {
                    Log.d("ExpenseData", "No data found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}