package com.example.financetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.financetracker.databinding.ActivityMainBinding;
import com.example.financetracker.databinding.ActivityMainDashboardBinding;

public class MainDashboard extends AppCompatActivity {
    ActivityMainDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}