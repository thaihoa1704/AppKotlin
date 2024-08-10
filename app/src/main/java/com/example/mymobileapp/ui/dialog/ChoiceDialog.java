package com.example.mymobileapp.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mymobileapp.databinding.ChoiceDialogBinding;
import com.example.mymobileapp.listener.OnClickChoice;

public class ChoiceDialog extends DialogFragment {
    private final String fragmentName;
    private final OnClickChoice onClickChoice;
    private ChoiceDialogBinding binding;
    public ChoiceDialog(String fragmentName, OnClickChoice onClickChoice){
        this.fragmentName = fragmentName;
        this.onClickChoice = onClickChoice;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireActivity());
        binding = ChoiceDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        if (fragmentName.equals("cartFragment")){
            binding.label.setVisibility(View.GONE);
            binding.tvTitle1.setText("Xoá sản phẩm khỏi giỏ hàng?");
            binding.tvTitle2.setVisibility(View.GONE);
            binding.btnYes.setText("Xoá");
        } else if (fragmentName.equals("orderFragment")){
            binding.label.setText("XÁC NHẬN ĐẶT HÀNG");
            binding.label.setVisibility(View.VISIBLE);
            binding.tvTitle1.setText("Bạn có chắc chắn muốn đặt hàng");
            binding.tvTitle2.setVisibility(View.GONE);
            binding.btnYes.setText("Thanh toán");
        } else if (fragmentName.equals("detailOrderFragment")){
            binding.label.setText("XÁC NHẬN HUỶ ĐƠN HÀNG");
            binding.label.setVisibility(View.VISIBLE);
            binding.tvTitle1.setVisibility(View.GONE);
            binding.tvTitle2.setVisibility(View.VISIBLE);
            binding.btnYes.setText("Huỷ đơn");
        } else if (fragmentName.equals("userFragment") || fragmentName.equals("adminActivity")){
            binding.label.setVisibility(View.GONE);
            binding.tvTitle1.setText("Bạn có muốn đăng xuất không?");
            binding.tvTitle2.setVisibility(View.GONE);
            binding.btnYes.setText("Đăng xuất");
        } else if (fragmentName.equals("editImage")) {
            binding.label.setVisibility(View.GONE);
            binding.tvTitle1.setText("Bạn có muốn đăng xuất không?");
            binding.tvTitle2.setVisibility(View.GONE);
            binding.btnYes.setText("Đăng xuất");
        }

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChoice.onClick(true);
                dialog.dismiss();
            }
        });

        return dialog;
    }
}