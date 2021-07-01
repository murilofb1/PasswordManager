package com.example.passwordgeneratorv2.newPassword;

import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterSpinnerSites;
import com.example.passwordgeneratorv2.databinding.ActivityNewPasswordBinding;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.helpers.PasswordGenerator;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class NewPasswordActivity extends AppCompatActivity implements Observer {
    //CONSTANTS
    private final static int REQUEST_OPEN_GALLERY = 1;

    //AlertDialog
    private ImageView dialogCustomImage;
    private Uri customImageUri;
    //Adapter
    private ArrayList<WebsiteModel> sitesList;

    private NewPasswordViewModel model;

    public static AdapterSpinnerSites sitesAdapter;

    private boolean maxSeekBarValue;

    private ActivityNewPasswordBinding binding;
    private ToastH toastH;
    private SlidersController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toastH = new ToastH(this);
        model = new ViewModelProvider(this).get(NewPasswordViewModel.class);
        addObservers();

        initSliders();
        initLayoutData();
        setChangeListeners();
        setClickListener();
        //initSpinner();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addObservers() {
        model.isRegistrationCompleted().observe(this, value -> {
            if (value) finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    protected void onDestroy() {
        controller.detachListeners();
        super.onDestroy();
    }

    //Settar todos os findViewById
    private void initLayoutData() {
        binding.edtGeneratedPassword.setText(model.currentPassword.generatePassword());
        updateValues();
        //sitesList = model.getSpinnerPasswordList();
        //AdapterSpinnerSites
        sitesAdapter = new AdapterSpinnerSites(NewPasswordActivity.this, sitesList);
    }

    void initSliders() {
        binding.sliderPasswordSize.setValue(model.currentPassword.passwordSize);
        binding.sliderPasswordSize.setValueTo(model.currentPassword.maxPasswordSize);
        binding.sliderPasswordSize.setValueFrom(model.currentPassword.minPasswordSize);
        binding.sliderUpperCase.setValue(model.currentPassword.upperCasesCount);
        binding.sliderLowerCases.setValue(model.currentPassword.lowerCasesCount);
        binding.sliderSpecialChar.setValue(model.currentPassword.specialCharCount);

        controller = new SlidersController(
                binding.sliderPasswordSize,
                binding.sliderUpperCase,
                binding.sliderLowerCases,
                binding.sliderSpecialChar
        );
    }


    private void initSpinner() {
        binding.spinnerSites.setAdapter(sitesAdapter);
        binding.spinnerSites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (position == sitesList.size() - 1) showDialog();
                binding.edtGeneratedPassword.setText(sitesList.get(position).getSiteLink());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    //Settar os valores padrões da classe PasswordGenerator nos seekbar
    private void updateValues() {
        controller.updateMaxValue();

        if (!maxSeekBarValue) {
            int intPsswdSize = (int) binding.sliderPasswordSize.getValue();
            int intLC = (int) binding.sliderLowerCases.getValue();
            int intUC = (int) binding.sliderUpperCase.getValue();
            int intSC = (int) binding.sliderSpecialChar.getValue();

            model.currentPassword = new PasswordGenerator(intPsswdSize, intLC, intUC, intSC);
            binding.edtGeneratedPassword.setText(model.currentPassword.generatePassword());
        }
    }

    private void setChangeListeners() {
        controller.setOnChangeListener((slider, value, fromUser) -> {
            if (!controller.validPassword()) {
                if (slider.getId() != R.id.sliderPasswordSize) {
                    //IF THE SLIDER ISN'T THE PASSWORD SIZE BACK ONE AND DON'T MOVE ON SCREEN
                    slider.setValue(slider.getValue() - 1);
                } else {
                    //ELSE MAX SEEK TO ENABLE UPDATE ON THE SLIDERS AND SUBTRACT ONE TO THE BIGGEST SLIDER
                    maxSeekBarValue = false;
                    controller.backTheLongestSlider();
                    updateValues();
                }

            } else if (controller.charactersEqualsSize()) {
                // IF PASSWORD SIZE MATCHES THE NUMBER OF CHAR STOP UPDATING THE EDIT TEXT
                updateValues();
                maxSeekBarValue = true;
            } else {
                maxSeekBarValue = false;
                updateValues();
            }

        });
    }

    //Settar ClickListener para todos os componentes
    private void setClickListener() {
        binding.btnRegisterPassword.setOnClickListener(view -> {
            WebsiteModel spinnerItem = (WebsiteModel) binding.spinnerSites.getSelectedItem();
            String newPsswd = Base64H.encode(binding.edtGeneratedPassword.getText().toString());
            String newIconLink = spinnerItem.getIconLink();
            String newSiteName = spinnerItem.getName();
            String newSiteLink = binding.edtGeneratedPassword.getText().toString();
            Password newPassword = new Password(newSiteName, newPsswd, newIconLink, newSiteLink);
            model.registerPassword(newPassword);
        });

        binding.tilGeneratedPassword.setEndIconOnLongClickListener(v -> {
            toastH.showToast(getString(R.string.shuffle_password));
            return true;
        });

        binding.tilGeneratedPassword.setEndIconOnClickListener(v -> {
            binding.edtGeneratedPassword.setText(model.currentPassword.shufflePassword());
        });
    }
/*
    private void showDialog() {
        LayoutInflater inflater = getLayoutInflater();
        // A gente infla a view do layout personalizado para o dialog
        View dialogLayout = inflater.inflate(R.layout.alert_dialogue_new_password, null);
        dialogCustomImage = dialogLayout.findViewById(R.id.img_custom_image);
        EditText edtCustomSite = dialogLayout.findViewById(R.id.edt_custom_site);
        dialogCustomImage.setOnClickListener(v -> {
            Intent intentGetImage = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetImage.setType("image/*");
            startActivityForResult(intentGetImage, REQUEST_OPEN_GALLERY);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(NewPasswordActivity.this);
        builder.setTitle("New item");
        builder.setView(dialogLayout);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (customImageUri != null) FirebaseHelper.uploadSpinnerImage(edtCustomSite.getText().toString(), customImageUri, this);

        }
        );

        builder.setNegativeButton("Cancel", null);
        builder.create();
        builder.show();
    }

 */

    //Opções Menu Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }


    //Result Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            customImageUri = data.getData();
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), customImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageBitmap != null) dialogCustomImage.setImageBitmap(imageBitmap);

        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(NewPasswordViewModel.SPINNER_LIST_ARG)) {
            sitesAdapter.notifyDataSetChanged();
        }
    }
}
