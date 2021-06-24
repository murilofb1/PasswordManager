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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.adapters.AdapterSpinnerSites;
import com.example.passwordgeneratorv2.databinding.ActivityNewPasswordBinding;
import com.example.passwordgeneratorv2.helpers.Base64H;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.helpers.PasswordGenerator;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class ActivityNewPassword extends AppCompatActivity implements Observer {
    //CONSTANTS
    private final static int REQUEST_OPEN_GALLERY = 1;
    // Interface
    private EditText edtSenhaGerada;
    private EditText edtNewSiteLink;
    private TextView txtQtdLowerCase;
    private TextView txtQtdUpperCase;
    private TextView txtQtdSpecialChar;
    private TextView txtQtdPasswordSize;
    private TextView txtCustomItemHint;
    //private SeekBar seekLowerCase;
    private SeekBar seekUpperCase;
    private SeekBar seekSpecialChar;
    private SeekBar seekPasswordSize;
    private Spinner spinnerSiteSelected;
    private Toolbar toolbarCadastro;
    private Button btnShuflle;
    private Button btnCadastrar;

    //AlertDialog
    private ImageView dialogCustomImage;
    private Uri customImageUri;
    //Adapter
    private ArrayList<WebsiteModel> sitesList;
    private NewPasswordViewModel cadastrarSenha;
    public static AdapterSpinnerSites sitesAdapter;

    private PasswordGenerator psswdGenerator;

    private boolean maxSeekBarValue;

    private ActivityNewPasswordBinding binding;
    private ToastH toastH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastH = new ToastH(this);
        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setDefaultValues();
        updateValues();
        setChangeListeners();
        setClickListener();
        configureSpinner();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    //Settar todos os findViewById
    private void initializeComponents() {
        //Edit Text
        edtSenhaGerada = findViewById(R.id.edtGeneratedPassword);
        edtNewSiteLink = findViewById(R.id.newSiteLink);
        //TextView
        txtQtdLowerCase = findViewById(R.id.txt_qtde_lower_case);
        txtQtdUpperCase = findViewById(R.id.txt_qtde_upper_case);
        txtQtdSpecialChar = findViewById(R.id.txt_qtde_special_char);
        txtQtdPasswordSize = findViewById(R.id.txt_qtde_password_size);
        //txtCustomItemHint = findViewById(R.id.txt_custom_item_hint);
        //SeekBar
        //seekLowerCase = findViewById(R.id.sliderLowerCases);
        seekUpperCase = findViewById(R.id.seek_upper_cases);
        seekSpecialChar = findViewById(R.id.seek_special_char);
        seekPasswordSize = findViewById(R.id.seek_password_size);
        //Spinner
        spinnerSiteSelected = findViewById(R.id.spinner_site_selected);
        //Toolbar
        toolbarCadastro = findViewById(R.id.toolbar_cadastro);
        //Button
        btnShuflle = findViewById(R.id.btn_shuffle_password);
        btnCadastrar = findViewById(R.id.btnRegisterPassword);
        //CadastrarSenhaViewModel
        cadastrarSenha = new NewPasswordViewModel();
        cadastrarSenha.addObserver(this);
        //List<password>
        sitesList = cadastrarSenha.getSpinnerPasswordList();
        //AdapterSpinnerSites
        sitesAdapter = new AdapterSpinnerSites(ActivityNewPassword.this, sitesList);
    }

    //void de teste para spinner personalizado
    private void configureSpinner() {
        spinnerSiteSelected.setAdapter(sitesAdapter);
        spinnerSiteSelected.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == sitesList.size() - 1) {
                    showDialog();
                }
                edtNewSiteLink.setText(sitesList.get(position).getSiteLink());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Settar os valores padrões da classe PasswordGenerator nos seekbar
    private void setDefaultValues() {
        //SeekBar
        seekPasswordSize.setProgress(new PasswordGenerator().passwordSize);
        //  seekLowerCase.setProgress(new PasswordGenerator().lowerCaseSize);
        binding.sliderLowerCases.setValue(new PasswordGenerator().lowerCaseSize);
        seekUpperCase.setProgress(new PasswordGenerator().upperCaseSize);
        seekSpecialChar.setProgress(new PasswordGenerator().specialCharSize);
        //EditText
        edtSenhaGerada.setText(new PasswordGenerator().generatePassword());
    }

    private void updateValues() {
        //SeekBar
        // seekLowerCase.setMax(seekPasswordSize.getProgress());
        binding.sliderLowerCases.setValueTo(seekPasswordSize.getProgress());
        seekUpperCase.setMax(seekPasswordSize.getProgress());
        seekSpecialChar.setMax(seekPasswordSize.getProgress());
        //TextView
        // txtQtdLowerCase.setText(String.valueOf(seekLowerCase.getProgress()));
        txtQtdLowerCase.setText(String.valueOf(binding.sliderLowerCases.getValue()));
        txtQtdUpperCase.setText(String.valueOf(seekUpperCase.getProgress()));
        txtQtdSpecialChar.setText(String.valueOf(seekSpecialChar.getProgress()));
        txtQtdPasswordSize.setText(String.valueOf(seekPasswordSize.getProgress()));
        //EditText
        //int intLowerCase = seekLowerCase.getProgress();
        int intLowerCase = (int) binding.sliderLowerCases.getValue();
        int intPasswordSize = seekPasswordSize.getProgress();
        int intUpperCase = seekUpperCase.getProgress();
        int intSpecialChar = seekSpecialChar.getProgress();

        if (!maxSeekBarValue) {
            psswdGenerator = new PasswordGenerator(intPasswordSize, intLowerCase, intUpperCase, intSpecialChar);
            edtSenhaGerada.setText(psswdGenerator.generatePassword());
        }
    }

    // Settar Change para todos os componentes
    private void setChangeListeners() {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //int intLowerCase = seekLowerCase.getProgress();
                int intLowerCase = (int) binding.sliderLowerCases.getValue();
                int intPasswordSize = seekPasswordSize.getProgress();
                int intUpperCase = seekUpperCase.getProgress();
                int intSpecialChar = seekSpecialChar.getProgress();
                int charSum = intSpecialChar + intLowerCase + intUpperCase;


                if (charSum > intPasswordSize) {
                    if (seekBar.getId() != R.id.seek_password_size) {
                        seekBar.setProgress(seekBar.getProgress() - 1);
                        if (binding.sliderLowerCases.getValue() >0){
                            binding.sliderLowerCases.setValue(binding.sliderLowerCases.getValue() - 1);
                        }

                    } else {
                        maxSeekBarValue = false;
                        updateValues();
                    }

                } else if (charSum == intPasswordSize) {
                    updateValues();
                    maxSeekBarValue = true;
                } else {
                    maxSeekBarValue = false;
                    updateValues();
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        //seekLowerCase.setOnSeekBarChangeListener(listener);
        binding.sliderLowerCases.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                //int intLowerCase = seekLowerCase.getProgress();
                int intLowerCase = (int) binding.sliderLowerCases.getValue();
                int intPasswordSize = seekPasswordSize.getProgress();
                int intUpperCase = seekUpperCase.getProgress();
                int intSpecialChar = seekSpecialChar.getProgress();
                int charSum = intSpecialChar + intLowerCase + intUpperCase;


                if (charSum > intPasswordSize) {
                    slider.setValue(slider.getValue() - 1);
                } else if (charSum == intPasswordSize) {
                    updateValues();
                    maxSeekBarValue = true;
                } else {
                    maxSeekBarValue = false;
                    updateValues();
                }

            }
        });
        seekUpperCase.setOnSeekBarChangeListener(listener);
        seekSpecialChar.setOnSeekBarChangeListener(listener);
        seekPasswordSize.setOnSeekBarChangeListener(listener);
    }

    //Settar ClickListener para todos os componentes
    private void setClickListener() {
        binding.btnRegisterPassword.setOnClickListener(view -> {
            WebsiteModel spinnerItem = (WebsiteModel) spinnerSiteSelected.getSelectedItem();
            String newPsswd = Base64H.encode(edtSenhaGerada.getText().toString());
            String newIconLink = spinnerItem.getIconLink();
            String newSiteName = spinnerItem.getName();
            String newSiteLink = edtNewSiteLink.getText().toString();
            Password newPassword = new Password(newSiteName, newPsswd, newIconLink, newSiteLink);
            FirebaseHelper.registerPassword(newPassword, ActivityNewPassword.this);
        });

        binding.tilGeneratedPassword.setEndIconOnLongClickListener(v -> {
            toastH.showToast(getString(R.string.shuffle_password));
            return true;
        });

        binding.tilGeneratedPassword.setEndIconOnClickListener(v -> {
            edtSenhaGerada.setText(psswdGenerator.shufflePassword());
        });
    }

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


        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNewPassword.this);
        builder.setTitle("New item");
        builder.setView(dialogLayout);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (customImageUri != null) {
                FirebaseHelper.uploadSpinnerImage(edtCustomSite.getText().toString(), customImageUri, this);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create();
        builder.show();
    }

    //Opções Menu Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
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
            if (imageBitmap != null) {
                dialogCustomImage.setImageBitmap(imageBitmap);
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(NewPasswordViewModel.SPINNER_LIST_ARG)) {
            sitesAdapter.notifyDataSetChanged();
        }
    }
}
