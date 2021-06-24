package com.example.passwordgeneratorv2.editPassword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.models.Password;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

public class EditPasswordView extends AppCompatActivity {
    private ImageView imgSiteIcon;
    private EditText edtSiteName;
    private EditText edtPassword;
    private EditText edtSiteLink;
    private FloatingActionButton fabConfirm;
    private Password originalPsswd;
    Uri selecImageUri = null;
    private static final int REQUEST_OPEN_GALLERY = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        setDefaultValues();
        setClickListener();
    }

    private void initComponents() {
        //ImageView
        imgSiteIcon = findViewById(R.id.psswdEditIcon);
        //EditText
        edtSiteName = findViewById(R.id.psswdEditName);
        edtPassword = findViewById(R.id.psswdEditPassword);
        edtSiteLink = findViewById(R.id.psswdEditSiteLink);
        //FloatingActionButton
        fabConfirm = findViewById(R.id.fabConfirmEdit);
    }

    private void setDefaultValues() {
        originalPsswd = (Password) getIntent().getSerializableExtra("extraPassword");
        if (!originalPsswd.getIconLink().equals("")) {
            Glide.with(this).load(originalPsswd.getIconLink()).into(imgSiteIcon);
        }

        edtSiteName.setText(originalPsswd.getSite());
        edtPassword.setText(Base64H.decode(originalPsswd.getPassword()));
        edtSiteLink.setText(originalPsswd.getSiteLink());
    }

    private boolean validatedEditText() {
        String strSiteName = edtSiteName.getText().toString();
        String strPassword = edtPassword.getText().toString();


        if (strPassword.equals("") || strSiteName.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void setClickListener() {

        fabConfirm.setOnClickListener(v -> {
            if (validatedEditText()) {
                changeConfirmation();
            } else {
                Toast.makeText(this, getString(R.string.toast_invalidated), Toast.LENGTH_SHORT).show();
            }
        });
        imgSiteIcon.setOnClickListener(v -> {
            Intent openGalery = new Intent(Intent.ACTION_GET_CONTENT);
            openGalery.setType("image/*");
            startActivityForResult(openGalery, REQUEST_OPEN_GALLERY);
        });
    }

    private void changeConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.edt_password_dialog_title));
        builder.setMessage(getString(R.string.edt_password_dialog_message));
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference originalReference = FirebaseHelper.getUserPasswordsReference().child(originalPsswd.getSite());
            if (!originalPsswd.getSite().equals(edtSiteName.getText().toString())) {
                originalReference.removeValue();
                if (!originalPsswd.getSite().equals("New item")){
                    FirebaseHelper.getUserIconsReference().child(originalPsswd.getSite()).child("beingUsed").setValue(false);
                }
                originalReference = FirebaseHelper.getUserPasswordsReference().child(edtSiteName.getText().toString());
                if (selecImageUri == null) {
                    originalReference.child("iconLink").setValue(originalPsswd.getIconLink());
                }
            }
            originalReference.child("password").setValue(Base64H.encode(edtPassword.getText().toString()));
            originalReference.child("site").setValue(edtSiteName.getText().toString());
            originalReference.child("siteLink").setValue(edtSiteLink.getText().toString());
            if (selecImageUri != null) {
                FirebaseHelper.uploadEditImage(edtSiteName.getText().toString(), selecImageUri);
            }
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            selecImageUri = data.getData();
            Bitmap selecImageBitmap = null;
            try {
                selecImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selecImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (selecImageBitmap != null) {
                imgSiteIcon.setImageBitmap(selecImageBitmap);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
