package com.example.passwordgeneratorv2.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.authentication.AuthenticationActivity;
import com.example.passwordgeneratorv2.firebase.FirebaseAuthentication;
import com.example.passwordgeneratorv2.settings.deleted_passwords.DeletedPasswordsActivity;


public class SettingsActivity extends AppCompatActivity {

    private Button btnLogOut;
    private Button btnDeletedPasswords;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        setOnClick();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        //Button

        btnLogOut = findViewById(R.id.btnLogOut);

        btnDeletedPasswords = findViewById(R.id.btnDeletedPasswords);
    }

    private void setOnClick() {
        View.OnClickListener clickListener = v -> {
             if (v.getId() == R.id.btnLogOut) {
                new FirebaseAuthentication().signOutUser(this);
                startActivity(new Intent(getApplicationContext(), AuthenticationActivity.class));
                finishAffinity();
            } else if (v.getId() == R.id.btnDeletedPasswords) {
                startActivity(new Intent(getApplicationContext(), DeletedPasswordsActivity.class));
            }
        };
        btnLogOut.setOnClickListener(clickListener);
        btnDeletedPasswords.setOnClickListener(clickListener);
    }
}
