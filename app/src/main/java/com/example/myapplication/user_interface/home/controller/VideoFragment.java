package com.example.myapplication.user_interface.home.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;


public class VideoFragment extends Fragment {

    private String path;

    public VideoFragment() {
        // Required empty public constructor
    }


    public static VideoFragment newInstance(String path){
        VideoFragment fragment = new VideoFragment();
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
        View root = inflater.inflate(R.layout.item_product_detail_video, container, false);


        return root;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}