package com.example.passwordgeneratorv2.new_password;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterSpinnerSites;
import com.example.passwordgeneratorv2.databinding.ActivityNewPasswordBinding;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.PasswordGenerator;
import com.example.passwordgeneratorv2.helpers.Security;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;


public class NewPasswordActivity extends AppCompatActivity {
    private NewPasswordViewModel model;
    public static AdapterSpinnerSites sitesAdapter;

    private boolean maxSeekBarValue;
    private boolean customItemSelected = false;

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
        initSpinner();
        initToolbar();
    }

    private void addObservers() {
        model.loadSpinnerList();
        model.isRegistrationCompleted().observe(this, value -> {
            if (value) finish();
        });
        model.getSpinnerList().observe(this, list -> sitesAdapter.updateList(list));
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
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.new_password);
        }
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
        sitesAdapter = new AdapterSpinnerSites(getBaseContext());
        binding.spinnerSites.setAdapter(sitesAdapter);
        binding.spinnerSites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemLink = model.getSpinnerList()
                        .getValue()
                        .get(position)
                        .getSiteLink();

                if (position == sitesAdapter.getCount() - 1) {
                    binding.tilNewSiteName.setVisibility(View.VISIBLE);
                    customItemSelected = true;
                } else {
                    binding.tilNewSiteName.setVisibility(View.GONE);
                    customItemSelected = false;
                }

                binding.edtNewSiteLink.setText(itemLink);
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
            if (Security.Companion.isAppUnlocked()) registerPassword();
            else Security.Companion.unlockApp(this, () -> {
                registerPassword();
                return null;
            });
        });

        binding.tilGeneratedPassword.setEndIconOnLongClickListener(v -> {
            toastH.showToast(getString(R.string.shuffle_password));
            return true;
        });

        binding.tilGeneratedPassword.setEndIconOnClickListener(v -> {
            binding.edtGeneratedPassword.setText(model.currentPassword.shufflePassword());
        });
    }

    private void registerPassword() {
        WebsiteModel spinnerItem = (WebsiteModel) binding.spinnerSites.getSelectedItem();

        String newSiteName;
        String newSiteLink;
        if (customItemSelected) {
            newSiteName = binding.edtNewSiteName.getText().toString();
            newSiteLink = binding.edtNewSiteLink.getText().toString();
        } else {
            newSiteName = spinnerItem.getSiteName();
            newSiteLink = spinnerItem.getSiteLink();
        }
        String newPsswd = Base64H.encode(binding.edtGeneratedPassword.getText().toString());
        String newIconLink = spinnerItem.getIconLink();

        Password newPassword = new Password(newSiteName, newPsswd, newIconLink, newSiteLink);
        model.registerPassword(newPassword);

    }

}
