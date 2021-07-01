package com.example.passwordgeneratorv2.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.databinding.ActivityHomeBinding;
import com.example.passwordgeneratorv2.editPassword.EditPasswordView;
import com.example.passwordgeneratorv2.firebase.PasswordsDB;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.Security;
import com.example.passwordgeneratorv2.helpers.VibratorH;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.newPassword.NewPasswordActivity;
import com.example.passwordgeneratorv2.adapters.AdapterPasswords;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.settings.SettingsActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;


public class HomeActivity extends AppCompatActivity {

    private Executor executor;
    private static BiometricPrompt biometricPrompt;
    private static BiometricPrompt.PromptInfo promptInfo;

    private MenuItem menuItemLockUnlock;
    private int listSize = 0;
    private boolean isLoaded = false;

    private List<String> icons = new ArrayList<>();

    private HomeViewModel model;
    private static AdapterPasswords adapterPasswords;
    private ActivityHomeBinding binding;
    private boolean isUnlocked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setClickListeners();

        initializeComponents();
        initRecycler();

        model = new ViewModelProvider(this).get(HomeViewModel.class);
        addObservers();

    }

/*
    private void loadUserIcons() {
        FirebaseHelper.getUserIconsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                icons.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Log.i("FirebaseH", item.getKey() + " ADDED");
                    icons.add(item.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

 */

    @Override
    protected void onStart() {
        super.onStart();
        //loadUserIcons();
        if (menuItemLockUnlock != null && AdapterPasswords.isUnlocked()) {
            menuItemLockUnlock.setIcon(R.drawable.ic_open_padlock);
        } else if (menuItemLockUnlock != null && !AdapterPasswords.isUnlocked()) {
            menuItemLockUnlock.setIcon(R.drawable.ic_padlock);
        }
    }


    private void initializeComponents() {
        //Executor
        executor = ContextCompat.getMainExecutor(this);
        //BiometricPrompt
        biometricPrompt = new BiometricPrompt(HomeActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Security.Companion.turnLock(true);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        //PromptInfo
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title_home))
                .setDescription(getString(R.string.biometric_description_home))
                .setDeviceCredentialAllowed(true)
                .build();
    }

    private void addObservers() {
        model.getPasswordList().observe(this, passwords -> {
            adapterPasswords.updateList(passwords);
            //UPDATE THE VISIBLE PASSWORDS ARRAY
            int newListSize = passwords.size();
            if (listSize < newListSize || listSize > newListSize) {
                adapterPasswords.updateVisibleArray(newListSize);
                this.listSize = newListSize;
            }
        });
        Security.Companion.getAppUnlockStatus().observe(this, value -> {
            if(menuItemLockUnlock != null) {
                if (value) menuItemLockUnlock.setIcon(R.drawable.ic_open_padlock);
                else menuItemLockUnlock.setIcon(R.drawable.ic_padlock);
            }
            isUnlocked = value;
        });
    }

    private void initRecycler() {
        adapterPasswords = new AdapterPasswords();
        adapterPasswords.setOnRecyclerCLickListener(new AdapterPasswords.OnRecyclerItemClick() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(int position) {
                if (isUnlocked) {
                    Password password = model.getPasswordList().getValue().get(position);
                    showPasswordInfo(password);
                } else {
                    openBiometricAuth();
                }
            }

            @Override
            public void onLongClick(int position) { }
        });
        binding.recyclerHomePasswords.setAdapter(adapterPasswords);
        binding.recyclerHomePasswords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerHomePasswords.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    public static void openBiometricAuth() {
        biometricPrompt.authenticate(promptInfo);
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
        menuItemLockUnlock = menu.findItem(R.id.menuLockUnlock);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLockUnlock) {
            if (isUnlocked) Security.Companion.turnLock(false);
            else openBiometricAuth();
        } else if (item.getItemId() == R.id.menuSettings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showPasswordInfo(Password password) {

        View passwordInfoView = getLayoutInflater().inflate(R.layout.dialog_password_info, null, false);

        AlertDialog builder = new AlertDialog.Builder(this).create();

        ImageView psswdInfoIcon = passwordInfoView.findViewById(R.id.psswdInfoIcon);
        TextView psswdInfoSiteName = passwordInfoView.findViewById(R.id.psswdInfoSiteName);
        TextView psswdInfoPassword = passwordInfoView.findViewById(R.id.psswdInfoPassword);
        TextView psswdInfoSiteLink = passwordInfoView.findViewById(R.id.psswdInfoSiteLink);
        ImageButton psswdInfoCopy = passwordInfoView.findViewById(R.id.psswdInfoCopy);
        ImageButton psswdInfoFav = passwordInfoView.findViewById(R.id.psswdInfoFav);
        ImageButton psswdInfoEdit = passwordInfoView.findViewById(R.id.psswdInfoEdit);
        ImageButton psswdInfoDelete = passwordInfoView.findViewById(R.id.psswdInfoDelete);

        View.OnClickListener clickListener = v -> {
            if (v.getId() == R.id.psswdInfoCopy) {
                AdapterPasswords.copyPassword(password.getPassword(), this);
                Toast.makeText(this, "Your " + password.getSite() + " password was copied to clipboard", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.psswdInfoFav) {
                VibratorH vibratorH = new VibratorH(this);
                vibratorH.shortVibration();
            } else if (v.getId() == R.id.psswdInfoEdit) {
                Intent i = new Intent(this, EditPasswordView.class);
                i.putExtra("extraPassword", password);
                startActivity(i);
            } else if (v.getId() == R.id.psswdInfoSiteLink) {
                if (password.getSiteLink().equals("")) {
                    Toast.makeText(this, "No link assigned", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Intent openSite = new Intent(Intent.ACTION_VIEW);
                        openSite.setData(Uri.parse(password.getSiteLink()));
                        startActivity(openSite);
                    } catch (Exception e) {
                        Toast.makeText(this, "Unable to open your site", Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (v.getId() == R.id.psswdInfoDelete) {
                builder.dismiss();
                Snackbar snackConfirm = Snackbar.make(findViewById(R.id.layoutHome), getText(R.string.snackbar_text), Snackbar.LENGTH_SHORT);
                AtomicBoolean delete = new AtomicBoolean(true);
                snackConfirm.setAction("Cancel", v1 -> {
                    delete.set(false);
                });
                snackConfirm.setActionTextColor(ContextCompat.getColor(this, R.color.teal_200));
                snackConfirm.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (delete.get()) {
                            new PasswordsDB().deletePassword(password);
                            /*
                            boolean haveIcon = false;
                            for (String item : icons) {
                                if (password.getSite().equals(item)) {
                                /   haveIcon = true;
                                    break;
                                }
                            }

                            if (haveIcon) {
                                FirebaseHelper.getUserIconsReference().child(password.getSite()).child("beingUsed").setValue(false);
                            }
                            FirebaseHelper.getUserPasswordsReference().child(password.getSite()).removeValue();

                             */
                        }
                    }
                });
                snackConfirm.show();

            }

        };
        psswdInfoCopy.setOnClickListener(clickListener);
        psswdInfoFav.setOnClickListener(clickListener);
        psswdInfoEdit.setOnClickListener(clickListener);
        psswdInfoSiteLink.setOnClickListener(clickListener);
        psswdInfoDelete.setOnClickListener(clickListener);

        if (password.isFavorite()) {
            psswdInfoFav.setImageResource(R.drawable.ic_favorite);
        } else {
            psswdInfoFav.setImageResource(R.drawable.ic_not_favorite);
        }

        psswdInfoSiteName.setText(password.getSite());
        psswdInfoPassword.setText(Base64H.decode(password.getPassword()));

        if (password.getIconLink().equals("")) {
            Glide.with(passwordInfoView).load(R.drawable.default_image).into(psswdInfoIcon);
        } else {
            Glide.with(passwordInfoView).load(password.getIconLink()).into(psswdInfoIcon);
        }

        builder.setView(passwordInfoView);
        builder.show();

    }


}
