package com.example.mymobileapp.helper;

import com.example.mymobileapp.model.Product;
import java.util.Comparator;

public class ComparatorPrice {
    public static Comparator<Product> descending = new Comparator<Product>() {
        @Override
        public int compare(Product one, Product two) {
            return -Integer.compare(one.getPrice(), two.getPrice());
        }
    };
    public static Comparator<Product> ascending = new Comparator<Product>() {
        @Override
        public int compare(Product one, Product two) {
            return -Integer.compare(two.getPrice(), one.getPrice());
        }
    };
}
