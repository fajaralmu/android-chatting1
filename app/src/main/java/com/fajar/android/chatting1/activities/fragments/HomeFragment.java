package com.fajar.android.chatting1.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.HomeFragmentHandler;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.WebResponse;

public class HomeFragment extends BaseFragment<HomeFragmentHandler>{
    protected SharedPreferences sharedpreferences;


    private Button buttonRegister;
    private EditText inputUsername;

    public HomeFragment(){
        setHandler(HomeFragmentHandler.getInstance(this));
        Logs.log("Catalog Fragment Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
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


    private void initComponents() {
        buttonRegister = findById(R.id.btn_register);
        inputUsername = findById(R.id.input_username);
        loader = findById(R.id.home_loader);
    }
    private void initEvents(){
        buttonRegister.setOnClickListener(this::register);
        setLoaderGone();
        Logs.log("Home Fragment initEvents");
    }

    private void register(View v){
        String username = inputUsername.getText().toString();
        handler.register(username, this::handleRegisterResponse);
    }

    public void handleRegisterResponse(WebResponse response, Exception error){
        if(null == error){
            AlertUtil.YesAlert(view.getContext(), "Info", "SUCCESS");
        }
    }
}
