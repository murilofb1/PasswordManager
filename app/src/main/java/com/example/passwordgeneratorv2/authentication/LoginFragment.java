package com.example.passwordgeneratorv2.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.databinding.FragmentLoginBinding;
import com.example.passwordgeneratorv2.helpers.ToastH;
import com.example.passwordgeneratorv2.home.HomeActivity;

import org.jetbrains.annotations.NotNull;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private FragmentLoginBinding binding;
    private LoginViewModel model;
    private ToastH toastH;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        addObservers();
        toastH = new ToastH(getActivity());
        setClickListener();
        return binding.getRoot();
    }

    private void addObservers() {
        model.getToastMessage().observe(getViewLifecycleOwner(), it -> {
            if (it != null) toastH.showToast(it);
        });
        model.wasLoginSuccessful().observe(getViewLifecycleOwner(), it -> openHomeActivity());
    }

    private void setClickListener() {
        binding.btnOpenRegister.setOnClickListener(v -> {
            openRegisterFragment();
        });
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtLoginEmail.getText().toString();
            String password = binding.edtLoginPassword.getText().toString();
            model.login(email, password);
        });
        binding.btnForgotPassword.setOnClickListener(v -> {
            //TO BE IMPLEMENTED
        });
    }

/*
//RESET PASSWORD DIALOG
    private void openDialog() {
        EditText editText = new EditText(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(layoutParams);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("We gonna send you a reset password email");
        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = editText.getText().toString();
            if (!email.isEmpty()) {
                FirebaseHelper.getFirebaseAuth().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "The e-mail will arrive in a few seconds", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Empty e-mail", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setView(editText);
        builder.show();
    }
 */

    private void openHomeActivity() {
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
    }

    private void openRegisterFragment() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameAuthActivity, registerFragment);
        transaction.commit();
    }

}