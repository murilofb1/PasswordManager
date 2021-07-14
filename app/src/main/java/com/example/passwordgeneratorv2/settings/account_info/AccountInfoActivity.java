package com.example.passwordgeneratorv2.settings.account_info;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.ViewModelProvider;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterPasswords;
import com.example.passwordgeneratorv2.databinding.ActivityAccountInfoBinding;

import java.util.concurrent.Executor;


public class AccountInfoActivity extends AppCompatActivity {


    private final static int DIALOG_EDIT_NAME = 0;
    private final static int DIALOG_EDIT_EMAIL = 1;
    private final static int DIALOG_EDIT_PASSWORD = 2;
    private final static int DIALOG_DELETE_ACCOUNT = 3;

    private ActivityAccountInfoBinding binding;
    private AccountInfoViewModel model;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new ViewModelProvider(this).get(AccountInfoViewModel.class);
        addObservers();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      //  setDefaultValues();
//        setClickListener();
    }

    @Override
    protected void onDestroy() {
        model.detachListeners();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addObservers() {
        model.getUserData().observe(this, model -> {
            binding.edtAccountInfoName.setText(model.getName());
            binding.edtAccountInfoEmail.setText(model.getEmail());
        });
    }


    /*
    private void openDialog(int editField) {
        String title = "";
        String message = "";

        EditText editText = new EditText(AccountInfoActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(layoutParams);

        if (editField == DIALOG_EDIT_NAME) {
            title = "Edit name";
            editText.setText(btnSettingsUserName.getText().toString());
        } else if (editField == DIALOG_EDIT_EMAIL) {
            title = "Edit E-mail";
            message = "Fill the field bellow with your new e-mail address";
            editText.setText(btnSettingsUserEmail.getText().toString());
        } else if (editField == DIALOG_EDIT_PASSWORD) {
            title = "Edit password";
            message = "Fill the field bellow with your new password";
            editText.setText(btnSettingsUserPassword.getText().toString());
        } else if (editField == DIALOG_DELETE_ACCOUNT) {
            title = "Confirmation";
            message = "All of your data gonna be erased, are you sure?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInfoActivity.this);
        builder.setTitle(title);
        if (!message.isEmpty()) {
            builder.setMessage(message);
        }
        if (editField != DIALOG_DELETE_ACCOUNT) {
            builder.setView(editText);
        }
        builder.setPositiveButton("Confirm", (dialog, which) -> {

            if (editField == DIALOG_EDIT_PASSWORD) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(this, "Unsaved changes, the password can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseHelper.editPassword(Base64H.encode(editText.getText().toString()));
                    btnSettingsUserPassword.setText(editText.getText().toString());
                }
            } else if (editField == DIALOG_EDIT_NAME) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(this, "Unsaved changes, your name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseHelper.editName(editText.getText().toString());
                    btnSettingsUserName.setText(editText.getText().toString());
                    UserModel.updateUserName(editText.getText().toString());
                }
            } else if (editField == DIALOG_EDIT_EMAIL) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(this, "Unsaved changes, your email can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseHelper.editEmail(editText.getText().toString());
                    btnSettingsUserEmail.setText(editText.getText().toString());
                    UserModel.updateUserEmail(editText.getText().toString());
                }
            } else if (editField == DIALOG_DELETE_ACCOUNT) {
                FirebaseHelper.deleteUser(AccountInfoActivity.this);
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setClickListener() {
        View.OnClickListener clickListener = v -> {
            if (AdapterPasswords.isUnlocked()) {
                if (v.getId() == R.id.btnSettingsUserName) {
                    openDialog(DIALOG_EDIT_NAME);
                } else if (v.getId() == R.id.btnSettingsUserEmail) {
                    openDialog(DIALOG_EDIT_EMAIL);
                } else if (v.getId() == R.id.btnSettingsUserPassword) {
                    openDialog(DIALOG_EDIT_PASSWORD);
                } else if (v.getId() == R.id.btnDeleteAccount) {
                    openDialog(DIALOG_DELETE_ACCOUNT);
                }
            } else {
                openBiometric();
            }
        };
        btnSettingsUserName.setOnClickListener(clickListener);
        btnSettingsUserEmail.setOnClickListener(clickListener);
        btnSettingsUserPassword.setOnClickListener(clickListener);
        btnDeleteAccount.setOnClickListener(clickListener);
    }

 */

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void openBiometric() {
        Executor executor = getMainExecutor();
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_unlock_title))
                .setDescription(getString(R.string.biometric_unlock_info))
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AdapterPasswords.setUnlocked(true);
            }
        });
        biometricPrompt.authenticate(promptInfo);
    }

}
