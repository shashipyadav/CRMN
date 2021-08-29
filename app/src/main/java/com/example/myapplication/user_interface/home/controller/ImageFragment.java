package com.example.myapplication.user_interface.home.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {
    private String path;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String path){
        ImageFragment fragment = new ImageFragment();
        fragment.setPath(path);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=  inflater.inflate(R.layout.item_product_detail_image, container, false);
        ImageView imageView = root.findViewById(R.id.product_image);

        if(getPath() != null){
            Picasso.with(getActivity()).load(getPath())
////                .placeholder(R.drawable.one)
//                  .error(R.drawable.two)
                    .into(imageView);
        }
        return root;
    }

    private void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}