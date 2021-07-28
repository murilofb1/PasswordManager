package com.example.passwordgeneratorv2.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.passwordgeneratorv2.dialogs.PasswordStatusCallback;
import com.example.passwordgeneratorv2.firebase.StorageDB;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.NewAdapterPasswords;
import com.example.passwordgeneratorv2.databinding.ActivityHomeBinding;
import com.example.passwordgeneratorv2.dialogs.PasswordInfoDialog;
import com.example.passwordgeneratorv2.helpers.OnRecyclerItemClick;
import com.example.passwordgeneratorv2.helpers.Security;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.new_password.NewPasswordActivity;
import com.example.passwordgeneratorv2.settings.SettingsActivity;

import org.jetbrains.annotations.NotNull;


public class HomeActivity extends AppCompatActivity {

    private HomeViewModel model;
    private ActivityHomeBinding binding;

    private MenuItem menuItemLockUnlock;
    private MenuItem menuToggleVisibility;
    private boolean isUnlocked = false;

    private final NewAdapterPasswords adapterPasswords = new NewAdapterPasswords();
    private ToastH toast;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toast = new ToastH(this);

        initRecycler();
        setClickListeners();

        model = new ViewModelProvider(this).get(HomeViewModel.class);
        addObservers();
    }

    @Override
    protected void onStart() {
        Log.i("HomeActivityDialog", "OnStartCalled");
        model.loadPasswords();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        model.detachListeners();
        super.onDestroy();
    }

    private void addObservers() {
        model.getPasswordList().observe(this, passwords -> {
            adapterPasswords.updateList(passwords);
        });
        Security.Companion.getUnlockStatus().observe(this, bool -> {
            if (menuItemLockUnlock != null) {
                if (bool) menuItemLockUnlock.setIcon(R.drawable.ic_open_padlock);
                else menuItemLockUnlock.setIcon(R.drawable.ic_padlock);
            }
            adapterPasswords.updateUnlockStatus(bool);
            isUnlocked = bool;
        });
        adapterPasswords.isPasswordsVisible().observe(this, visible -> {
            if (menuToggleVisibility != null) {
                if (visible) menuToggleVisibility.setIcon(R.drawable.ic_password_invisible);
                else menuToggleVisibility.setIcon(R.drawable.ic_password_visibility);
            }
        });
        StorageDB.Companion.addOnPasswordUpdateListener(() -> model.loadPasswords());
    }


    private void initRecycler() {
        adapterPasswords.addToastFeedBack(this);
        adapterPasswords.setOnRecyclerCLickListener(new OnRecyclerItemClick() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(int position) {
                if (isUnlocked) {
                    Password password = model.getPasswordList().getValue().get(position);
                    showPasswordInfoDialog(password);
                } else Security.Companion.unlockApp(HomeActivity.this, null);
            }

            @Override
            public void onLongClick(int position) {
            }
        });

        binding.recyclerHomePasswords.setAdapter(adapterPasswords);
        binding.recyclerHomePasswords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void setClickListeners() {
        binding.fabAddPassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_passwords_fragments, menu);
        menuItemLockUnlock = menu.findItem(R.id.menuToggleDeletedLock);
        menuToggleVisibility = menu.findItem(R.id.menuToggleDeletedVisibility);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuToggleDeletedLock:
                if (isUnlocked) Security.Companion.lockApp();
                else Security.Companion.unlockApp(HomeActivity.this, null);
                break;
            case R.id.menuSettings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.menuToggleDeletedVisibility:
                if (!isUnlocked) toast.showToast(getString(R.string.unlock_first));
                else adapterPasswords.toggleVisibility(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void showPasswordInfoDialog(Password password) {
        PasswordInfoDialog dialog = new PasswordInfoDialog(this);
        dialog.setPassword(password);
        dialog.setPasswordStatusCallback(new PasswordStatusCallback() {
            @Override
            public void onPasswordRemoved(@NotNull Password password) {
                adapterPasswords.removeItem(password);
            }

            @Override
            public void onPasswordRestored(@NotNull Password password) {
                adapterPasswords.restoreLastItem();
            }
        });
        dialog.create().show();
    }


}
