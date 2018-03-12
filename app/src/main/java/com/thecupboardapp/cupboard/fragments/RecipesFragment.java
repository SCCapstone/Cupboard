package com.thecupboardapp.cupboard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.thecupboardapp.cupboard.R;

/**
 * Created by Kyle on 1/12/2018.
 */

public class RecipesFragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_recipes);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
            return v;
        }

        View v = inflater.inflate(R.layout.fragment_recipes, container, false);
        return v;
    }
}
