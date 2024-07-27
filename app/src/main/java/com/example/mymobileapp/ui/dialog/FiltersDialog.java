package com.example.mymobileapp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.mymobileapp.adapter.BrandAdapter;
import com.example.mymobileapp.adapter.PriceAdapter;
import com.example.mymobileapp.databinding.FiltersDialogBinding;
import com.example.mymobileapp.listener.ClickItemBrandListener;
import com.example.mymobileapp.listener.ClickItemPriceListener;
import com.example.mymobileapp.model.Brand;
import com.example.mymobileapp.model.Price;
import java.util.List;

public class FiltersDialog extends DialogFragment implements ClickItemBrandListener, ClickItemPriceListener {
    private FiltersDialogBinding binding;
    private BrandAdapter brandAdapter;
    private PriceAdapter priceAdapter;
    private List<Brand> brands;
    private List<Price> price;
    private int selectedBrandPosition;
    private int selectedPricePosition;
    private Brand brandSelected;
    private Price priceSelected;

    public interface GetFilters{
        void getData(Brand brandSelected, int brandPosition, Price priceSelected, int pricePosition);
    }
    public GetFilters getFilters;
    public FiltersDialog(List<Brand> brands, int brandPosition, List<Price> price, int pricePosition){
        this.brands = brands;
        this.selectedBrandPosition = brandPosition;
        this.price = price;
        this.selectedPricePosition = pricePosition;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Dialog dialog = new Dialog(getActivity());
//        binding = FiltersDialogBinding.inflate(LayoutInflater.from(getActivity()));
        Dialog dialog = new Dialog(requireActivity());
        binding = FiltersDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.rcvBrand.setVisibility(View.VISIBLE);
        binding.rcvPrice.setVisibility(View.VISIBLE);

        setBrandAdapter(selectedBrandPosition);
        setPriceAdapter(selectedPricePosition);

        if (selectedBrandPosition != -1){
            brandSelected = brands.get(selectedBrandPosition);
        } else {
            brandSelected = new Brand();
        }
        if (selectedPricePosition != -1){
            priceSelected = price.get(selectedPricePosition);
        } else {
            priceSelected = new Price();
        }

        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send data to fragment
                getFilters.getData(brandSelected, selectedBrandPosition, priceSelected, selectedPricePosition);
                dialog.dismiss();
            }
        });

        binding.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBrandAdapter(-1);
                brandSelected = new Brand();
                selectedBrandPosition = -1;
                setPriceAdapter(-1);
                priceSelected = new Price();
                selectedPricePosition = -1;
            }
        });
        return dialog;
    }

    private void setPriceAdapter(int position) {
        priceAdapter = new PriceAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        binding.rcvPrice.setHasFixedSize(true);
        binding.rcvPrice.setLayoutManager(gridLayoutManager);
        binding.rcvPrice.setAdapter(priceAdapter);
        priceAdapter.setData(requireActivity(), price, position);
        //priceAdapter.notifyDataSetChanged();
    }

    private void setBrandAdapter(int position) {
        brandAdapter = new BrandAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        binding.rcvBrand.setHasFixedSize(true);
        binding.rcvBrand.setLayoutManager(gridLayoutManager);
        binding.rcvBrand.setAdapter(brandAdapter);
        brandAdapter.setData(requireActivity(), brands, position);
        //brandAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickItemBrand(Brand brandSelected, int position) {
        this.brandSelected = brandSelected;
        this.selectedBrandPosition = position;
    }
    @Override
    public void onPriceClick(Price price, int position) {
        this.priceSelected = price;
        this.selectedPricePosition = position;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getFilters = (GetFilters) getParentFragment();
    }
}