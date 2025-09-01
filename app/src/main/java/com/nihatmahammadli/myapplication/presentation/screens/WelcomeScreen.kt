package com.nihatmahammadli.myapplication.presentation.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nihatmahammadli.myapplication.R
import com.nihatmahammadli.myapplication.databinding.FragmentWelcomeScreenBinding


class WelcomeScreen : Fragment() {
    private lateinit var binding: FragmentWelcomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeScreenBinding.inflate(inflater,container,false)
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeScreen_to_homePage)
        }
        return binding.root
    }



}