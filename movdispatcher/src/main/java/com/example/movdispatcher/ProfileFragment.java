package com.example.movdispatcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movdispatcher.models.Dispatcher;
import com.example.movdispatcher.utils.UserPreferences;

public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Dispatcher dispatcher = UserPreferences.getUser();

        TextView dispatcherName = view.findViewById(R.id.dispatcherName);
        ImageView dispatcherPic = view.findViewById(R.id.dispatcherPic);
        TextView dispatcherPhone = view.findViewById(R.id.dispatcherPhone);
        TextView dispatcherEmail = view.findViewById(R.id.dispatcherEmail);
        Button editProfileBtn = view.findViewById(R.id.editProfileBtn);
        TextView logoutBtn = view.findViewById(R.id.logoutBtn);

        assert dispatcher != null;
        Glide.with(requireContext()).load(dispatcher.getImgUrl()).into(dispatcherPic);
        dispatcherPhone.setText(dispatcher.getPhoneNumber());
        dispatcherEmail.setText(dispatcher.getEmail());
        dispatcherName.setText(dispatcher.getName());

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            intent.putExtra("dispatcher", dispatcher);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            UserPreferences.clearPreferences();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }
}