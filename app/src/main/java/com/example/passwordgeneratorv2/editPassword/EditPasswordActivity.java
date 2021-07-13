package com.example.passwordgeneratorv2.editPassword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;

import java.io.IOException;

public class EditPasswordActivity extends AppCompatActivity {

    //private Password originalPsswd;
    Uri selectedImageUri = null;
    private static final int REQUEST_OPEN_GALLERY = 1;
    private Password original;
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff1cbbb4));
        getSupportActionBar().setTitle("Edit Password");
    }

    private void setDefaultValues() {
        original = (Password) getIntent().getSerializableExtra(IntentTags.EXTRA_PASSWORD);

        if (original.getIconLink() != null) {
            Glide.with(this)
                    .load(original.getIconLink())
                    .placeholder(R.drawable.default_image)
                    .into(binding.imgEditSiteIcon);
        }

        binding.edtPasswordName.setText(original.getSiteName());
        binding.edtPasswordPsswd.setText(Base64H.decode(original.getPassword()));

        if (original.getSiteLink() != null) {
            binding.edtPasswordLink.setText(original.getSiteLink());
        }
    }

    private void setClickListener() {
        binding.fabConfirmEdit.setOnClickListener(v -> {
            if (validatedEditText()) showEditConfirmationDialog();
            else toast.showToast(getString(R.string.toast_empty_name_and_password));
        });
        binding.imgEditSiteIcon.setOnClickListener(v -> {
            Intent openGalery = new Intent(Intent.ACTION_GET_CONTENT);
            openGalery.setType("image/*");
            startActivityForResult(openGalery, REQUEST_OPEN_GALLERY);

            //aquele bagui la que abre antes do onCreate
        });
    }

    private void showEditConfirmationDialog() {
        new CustomDialogs(this)
                .setPassword(getNewPassword())
                .setDialogType(CustomDialogs.EDIT_PASSWORD)
                .showDialog();

        /*
        CustomDialogs.EditPasswordDialog dialog = new CustomDialogs.EditPasswordDialog(this);
        currentPassword = currentPassword.copyWith(
                binding.edtPasswordName.getText().toString(),
                Base64H.encode(binding.edtPasswordPsswd.getText().toString()),
                null,
                binding.edtPasswordLink.getText().toString()
        );
        dialog.setPassword(currentPassword);
        dialog.showDialog();

         */
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

    private Password getNewPassword() {
        Password newPassword = original;

        String inputSiteName = binding.edtPasswordName.getText().toString();
        if (!inputSiteName.equals(original.getSiteName())) newPassword.setSiteName(inputSiteName);

        String inputPassword = Base64H.encode(binding.edtPasswordPsswd.getText().toString());
        if (!inputPassword.equals(original.getPassword())) newPassword.setPassword(inputPassword);

        String inputSiteLink = binding.edtPasswordLink.getText().toString();
        if (!inputSiteLink.equals(original.getSiteLink())) newPassword.setPassword(inputSiteLink);

        return newPassword;
    }

    private boolean validatedEditText() {
        String siteName = binding.edtPasswordName.getText().toString();
        String sitePassword = binding.edtPasswordPsswd.getText().toString();
        return !siteName.isEmpty() && !sitePassword.isEmpty();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            Bitmap selectedImageBitmap = null;
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (selectedImageBitmap != null) {
                binding.imgEditSiteIcon.setImageBitmap(selectedImageBitmap);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
