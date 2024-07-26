package com.example.mymobileapp.ui.fragment.shopping;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mymobileapp.R;
import com.example.mymobileapp.databinding.FragmentHandleOrderBinding;


public class HandleOrderFragment extends Fragment {
    private FragmentHandleOrderBinding binding;
    private NavController controller;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHandleOrderBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);

        binding.constraintLayout.setVisibility(View.INVISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.constraintLayout.setVisibility(View.VISIBLE);
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) binding.imgDone.getDrawable();
                drawable.start();
                binding.tvNotification.setText("Đặt hàng thành công!");
            }
        }, 4000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controller.navigate(R.id.action_handleOrderFragment_to_cartFragment);
            }
        }, 7000);
    }
}