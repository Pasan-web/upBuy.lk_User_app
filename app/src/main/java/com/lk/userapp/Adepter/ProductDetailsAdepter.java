package com.lk.userapp.Adepter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lk.userapp.ProductDescriptionFragment;
import com.lk.userapp.ProductSpecificationFragment;

public class ProductDetailsAdepter extends FragmentPagerAdapter {

    private int totalTabs;

    public ProductDetailsAdepter(FragmentManager fm, int totalTabs){
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ProductDescriptionFragment descriptionFragment = new ProductDescriptionFragment();
                return descriptionFragment;
            case 1:
                ProductSpecificationFragment specificationFragment = new ProductSpecificationFragment();
                return specificationFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
