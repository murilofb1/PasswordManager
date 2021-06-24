package com.example.passwordgeneratorv2.settings.options;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterPasswords;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.models.UserModel;

import java.util.concurrent.Executor;


public class AccountInfo extends AppCompatActivity {

    private Button btnSettingsUserName;
    private Button btnSettingsUserEmail;
    private Button btnSettingsUserPassword;
    private Button btnDeleteAccount;

    private final static int DIALOG_EDIT_NAME = 0;
    private final static int DIALOG_EDIT_EMAIL = 1;
    private final static int DIALOG_EDIT_PASSWORD = 2;
    private final static int DIALOG_DELETE_ACCOUNT = 3;


    private static final UserModel userModel = UserModel.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        setDefaultValues();
        setClickListener();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        btnSettingsUserName = findViewById(R.id.btnSettingsUserName);
        btnSettingsUserEmail = findViewById(R.id.btnSettingsUserEmail);
        btnSettingsUserPassword = findViewById(R.id.btnSettingsUserPassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
    }

    private void setDefaultValues() {

        if (userModel == null) {
            setContentView(R.layout.user_data_error);
        } else {
            btnSettingsUserName.setText(userModel.getName());
            btnSettingsUserEmail.setText(userModel.getEmail());
        }

    }

    private void openDialog(int editField) {
        String title = "";
        String message = "";

        EditText editText = new EditText(AccountInfo.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInfo.this);
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
                FirebaseHelper.deleteUser(AccountInfo.this);
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void openBiometric() {
        Executor executor = getMainExecutor();
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title_home))
                .setDescription(getString(R.string.biometric_description_account_info))
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
