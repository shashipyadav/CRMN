package com.example.myapplication.user_interface.menu.view;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.menu.controller.ExpandableItemClickListener;
import com.example.myapplication.user_interface.menu.controller.ExpandableMenuAdapter;
import com.example.myapplication.user_interface.menu.model.DrawerItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    private static final String DEBUG_TAG = MenuFragment.class.getSimpleName();
    private HashMap<DrawerItem, List<DrawerItem>> listDataChild;
    private ArrayList<DrawerItem> listDataHeader = new ArrayList<>();
    private MenuListener menuListener;

    public interface MenuListener {
         void menuItemClicked(DrawerItem drawerItem);
    }

    public MenuFragment() {
        // Required empty public constructor
    }


    public static MenuFragment newInstance(ArrayList<DrawerItem> listHeader,
                                           HashMap<DrawerItem,
            List<DrawerItem>> hashMap) {
        MenuFragment fragment = new MenuFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("HeaderList", listHeader);
        bundle.putSerializable("HashMap", hashMap);
        fragment.setArguments(bundle);
        return fragment;
    }

    private ExpandableItemClickListener expListener = new ExpandableItemClickListener() {
        @Override
        public void onExpandableItemClick(DrawerItem drawerItem) {
            menuListener.menuItemClicked(drawerItem);
        }
    };

//    public void replaceFragment(Fragment fragment) {
//        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right,
//                R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
//        fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(fragment.toString()).commit();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (getArguments() != null) {
            listDataHeader = getArguments().getParcelableArrayList("HeaderList");
            listDataChild = (HashMap<DrawerItem, List<DrawerItem>>) getArguments()
                    .getSerializable("HashMap");
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callAdapter(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            menuListener = (MenuListener) context;
        }
        catch (Exception e){
            // you should really do something here with this exception
        }
    }


    public void callAdapter(View root) {
         ExpandableListView expandableListView = root.findViewById(R.id.expandableListView);
        ExpandableMenuAdapter expAdapter = new ExpandableMenuAdapter(
                getActivity(),
                listDataHeader, listDataChild, expandableListView, expListener);
        expandableListView.setAdapter(expAdapter);
    }


    @Override
    public void onDestroyView() {
        Log.e(DEBUG_TAG, "onDestroyView() called");
        listDataChild = null;
        listDataHeader = null;
        super.onDestroyView();
    }
}
