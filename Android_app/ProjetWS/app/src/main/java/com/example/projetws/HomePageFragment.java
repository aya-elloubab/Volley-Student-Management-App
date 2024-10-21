package com.example.projetws;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class HomePageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Get references to views
        ImageView logoImage = view.findViewById(R.id.logo_image);
        TextView welcomeText = view.findViewById(R.id.welcome_text);
        TextView subtitleText = view.findViewById(R.id.subtitle_text);

        // Add simple fade-in animations
        logoImage.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        welcomeText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        subtitleText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

        return view;
    }
}