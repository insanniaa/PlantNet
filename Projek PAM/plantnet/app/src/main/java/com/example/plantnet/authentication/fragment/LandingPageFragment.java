package com.example.plantnet.authentication.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.plantnet.R;
import com.example.plantnet.authentication.AuthenticationActivity;

public class LandingPageFragment extends Fragment {

    public LandingPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landingpage, container, false);

        view.findViewById(R.id.bt_signUp).setOnClickListener(v -> {
            ((AuthenticationActivity) requireActivity()).showRegisterFragment();
        });

        view.findViewById(R.id.tv_loginOption).setOnClickListener(v -> {
            ((AuthenticationActivity) requireActivity()).showLoginFragment();
        });

        return view;
    }
}
