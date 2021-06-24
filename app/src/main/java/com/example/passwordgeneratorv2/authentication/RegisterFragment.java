package com.example.passwordgeneratorv2.authentication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.passwordgeneratorv2.databinding.FragmentRegistrationBinding;
import com.example.passwordgeneratorv2.helpers.ToastH;


public class RegisterFragment extends Fragment {

    public RegisterFragment() {}

    private FragmentRegistrationBinding binding;
    private RegisterViewModel model;
    private ToastH toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        toast = new ToastH(getActivity());
        initViewModel();
        setClickListener();
        return binding.getRoot();
    }

    void initViewModel() {
        model = new ViewModelProvider(getActivity()).get(RegisterViewModel.class);
        model.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            toast.showToast(message);
        });
        model.wasRegistrationSuccessful().observe(getViewLifecycleOwner(), message ->{
            ((AuthenticationActivity)getActivity()).skipScreen();
        });
    }

    private void setClickListener() {
        binding.btnSignUp.setOnClickListener(v -> {
            String email = binding.edtNewUserEmail.getText().toString();
            String password = binding.edtRegisterPassword.getText().toString();
            String password2 = binding.edtRegisterPassword2.getText().toString();
            model.registerWithEmailAndPassword(email, password, password2);
        });
    }
}
