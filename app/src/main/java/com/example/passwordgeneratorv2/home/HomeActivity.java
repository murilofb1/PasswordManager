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
import com.example.passwordgeneratorv2.to_delete.NewCustomDialogs;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.NewAdapterPasswords;
import com.example.passwordgeneratorv2.databinding.ActivityHomeBinding;
import com.example.passwordgeneratorv2.dialogs.EditPasswordDialog;
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
                if (visible) {
                    menuToggleVisibility.setIcon(R.drawable.ic_password_invisible);
                } else {
                    menuToggleVisibility.setIcon(R.drawable.ic_password_visibility);
                }
            }
        });
    }


    private void initRecycler() {
        adapterPasswords.addToastFeedBack(this);
        adapterPasswords.setOnRecyclerCLickListener(new OnRecyclerItemClick() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(int position) {
                if (isUnlocked) {
                    Password password = model.getPasswordList().getValue().get(position);
                    Log.i("HomeDialog","PasswordSelected = " + password.getId());
                    showPasswordInfoDialog(password);
                } else {
                    Security.Companion.unlockApp(HomeActivity.this, null);
                }
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

    /*
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void showPasswordInfo(Password password) {

            View passwordInfoView = getLayoutInflater().inflate(R.layout.dialog_password_info, null, false);

            AlertDialog builder = new MaterialAlertDialogBuilder(this, R.style.RoundedDialogStyle).create();


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
                    Toast.makeText(this, "Your " + password.getSiteName() + " password was copied to clipboard", Toast.LENGTH_SHORT).show();
                } else if (v.getId() == R.id.psswdInfoFav) {
                    VibratorH vibratorH = new VibratorH(this);
                    vibratorH.shortVibration();
                } else if (v.getId() == R.id.psswdInfoEdit) {
                    Intent i = new Intent(this, EditPasswordActivity.class);
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

            psswdInfoSiteName.setText(password.getSiteName());
            psswdInfoPassword.setText(Base64H.decode(password.getPassword()));

            if (password.getIconLink().equals("")) {
                Glide.with(passwordInfoView).load(R.drawable.default_image).into(psswdInfoIcon);
            } else {
                Glide.with(passwordInfoView).load(password.getIconLink()).into(psswdInfoIcon);
            }


            builder.setView(passwordInfoView);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builder.show();

        }
        */
    //NewCustomDialogs.Companion.PasswordInfoDialog dialog;

    void showPasswordInfoDialog(Password password) {
        EditPasswordDialog dialog = new EditPasswordDialog(this);
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
