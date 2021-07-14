package com.example.passwordgeneratorv2.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.passwordgeneratorv2.authentication.login.LoginFragment
import com.example.passwordgeneratorv2.authentication.registration.RegisterFragment
import com.example.passwordgeneratorv2.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    lateinit var binding: FragmentWelcomeBinding

    companion object {
        val getInstance = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        val authAct = activity as AuthenticationActivity
        binding.btnGoToLogin.setOnClickListener { authAct.moveToFragment(LoginFragment()) }
        binding.btnGoToRegistration.setOnClickListener { authAct.moveToFragment(RegisterFragment()) }
    }

}