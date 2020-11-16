package com.fajar.android.chatting1.activities.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;

public class CatalogFragment extends BaseFragment{
    protected SharedPreferences sharedpreferences;
    private View view;

    public CatalogFragment(){
        Logs.log("Catalog Fragment Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_catalog, container, false);
        sharedpreferences = getActivity().getSharedPreferences(SharedPreferencesConstants.SHARED_CONTENT.value, Context.MODE_PRIVATE);
        initComponents();
        initEvents();
        Logs.log("Catalog Fragment onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    private <T extends View> T findViewById(int id){
        return view.findViewById(id);
    }

    private void initComponents() {

    }


    private void initEvents(){

        Logs.log("Catalog Fragment initEvents");
    }
    private View.OnClickListener gotoMenu(final int fragmentId){
        return gotoMenu(fragmentId, null);
    }
    private View.OnClickListener gotoMenu(final int fragmentId, final String breadCumbLabel) {

        return  (View v)-> {
                    switchFragment(fragmentId, breadCumbLabel);
            };
    }
    private View.OnClickListener exit() {
        final Context ctx = this.getContext();
        return new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        exitApplication();
                    }
                };
                AlertUtil.confirm(ctx, "Exit?", callback);

            }
        };
    }
    private void switchFragment(int fragmentId, String breadCumbLabel){

        HomeActivity parentActivity = null;
        if(getActivity() instanceof HomeActivity){
            parentActivity = (HomeActivity) getActivity();
        } else{
            return;
        }
        parentActivity.switchFragmentInCatalogPage(fragmentId, breadCumbLabel);

    }

}
