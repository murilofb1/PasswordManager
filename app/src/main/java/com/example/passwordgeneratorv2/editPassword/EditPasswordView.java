package com.example.passwordgeneratorv2.editPassword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.passwordgeneratorv2.CustomDialogs;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.constants.IntentTags;
import com.example.passwordgeneratorv2.databinding.ActivityEditPasswordBinding;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

public class EditPasswordView extends AppCompatActivity {

    //private Password originalPsswd;
    Uri selectedImageUri = null;
    private static final int REQUEST_OPEN_GALLERY = 1;
    private Password currentPassword;
    private ActivityEditPasswordBinding binding;
    private ToastH toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toast = new ToastH(this);
        initToolbar();
        setDefaultValues();
        setClickListener();
    }

    private void initToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setDefaultValues() {
        currentPassword = (Password) getIntent().getSerializableExtra(IntentTags.EXTRA_PASSWORD);
        if (!currentPassword.getIconLink().isEmpty()) {
            Glide.with(this)
                    .load(currentPassword.getIconLink())
                    .into(binding.imgEditSiteIcon);
        }
        binding.edtPasswordName.setText(currentPassword.getSite());
        binding.edtPasswordPsswd.setText(Base64H.decode(currentPassword.getPassword()));
        binding.edtPasswordLink.setText(currentPassword.getSiteLink());
    }

    private void setClickListener() {
        binding.fabConfirmEdit.setOnClickListener(v -> {
            if (validatedEditText()) changeConfirmation();
            else toast.showToast(getString(R.string.toast_invalidated));
        });
        binding.imgEditSiteIcon.setOnClickListener(v -> {
            Intent openGalery = new Intent(Intent.ACTION_GET_CONTENT);
            openGalery.setType("image/*");
            startActivityForResult(openGalery, REQUEST_OPEN_GALLERY);

            //aquele bagui la que abre antes do onCreate
        });
    }

    private void changeConfirmation() {
        CustomDialogs.ConfirmPasswordEditionDialog dialog = new CustomDialogs.ConfirmPasswordEditionDialog(this);
        currentPassword = currentPassword.copyWith(
                binding.edtPasswordName.getText().toString(),
                Base64H.encode(binding.edtPasswordPsswd.getText().toString()),
                null,
                binding.edtPasswordLink.getText().toString()
        );
        dialog.setPassword(currentPassword);
        dialog.showDialog();
        //NAO TA HABILITADO A FUNÇÃO DE MUDAR O ICONE
        /*
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference originalReference = FirebaseHelper.getUserPasswordsReference().child(originalPsswd.getSite());
            if (!originalPsswd.getSite().equals(edtSiteName.getText().toString())) {
                originalReference.removeValue();
                if (!originalPsswd.getSite().equals("New item")) {
                    FirebaseHelper.getUserIconsReference().child(originalPsswd.getSite()).child("beingUsed").setValue(false);
                }
                originalReference = FirebaseHelper.getUserPasswordsReference().child(edtSiteName.getText().toString());
                if (selectedImageUri == null) {
                    originalReference.child("iconLink").setValue(originalPsswd.getIconLink());
                }
            }
            originalReference.child("password").setValue(Base64H.encode(edtPassword.getText().toString()));
            originalReference.child("site").setValue(edtSiteName.getText().toString());
            originalReference.child("siteLink").setValue(edtSiteLink.getText().toString());
            if (selectedImageUri != null) {
                FirebaseHelper.uploadEditImage(edtSiteName.getText().toString(), selectedImageUri);
            }
            finish();
        });

         */

    }

    private boolean validatedEditText() {
        String siteName = binding.edtPasswordName.getText().toString();
        String sitePassword = binding.edtPasswordPsswd.getText().toString();
        return !siteName.isEmpty() || !sitePassword.isEmpty();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            Bitmap selecImageBitmap = null;
            try {
                selecImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (selecImageBitmap != null) {
                binding.imgEditSiteIcon.setImageBitmap(selecImageBitmap);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
