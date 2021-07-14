package com.example.passwordgeneratorv2.authentication;


import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.passwordgeneratorv2.firebase.FirebaseAuthentication;
import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.home.HomeActivity;

public class AuthenticationActivity extends AppCompatActivity {
    private static final WelcomeFragment welcomeFragment = new WelcomeFragment();
    private static Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseAuthentication auth = new FirebaseAuthentication();
        if (auth.isSomeoneLogged()) skipScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        moveToFragment(welcomeFragment);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof WelcomeFragment) super.onBackPressed();
        else moveToFragment(welcomeFragment);
    }

    protected void moveToFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameAuthActivity, fragment, null);
        transaction.commit();
        currentFragment = fragment;
    }

    public void skipScreen() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
