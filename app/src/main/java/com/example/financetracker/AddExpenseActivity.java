package com.example.financetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.financetracker.databinding.ActivityAddExpenseBinding;

public class AddExpenseActivity extends AppCompatActivity {
    ActivityAddExpenseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}