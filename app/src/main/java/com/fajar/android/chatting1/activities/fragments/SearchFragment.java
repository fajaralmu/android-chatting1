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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.MyConsumer;
import com.fajar.android.chatting1.handlers.SearchFragmentHandler;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.AlertUtil;
import com.fajar.android.chatting1.util.Logs;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebResponse;

import androidx.cardview.widget.CardView;

public class SearchFragment extends BaseFragment<SearchFragmentHandler>{

    private TextView accountName, accountId, accountRegisteredDate;
    private EditText inputPartnerId;
    private ImageButton buttonSearch;
    private CardView accountResultCard;

    public SearchFragment(){
       setHandler(SearchFragmentHandler.getInstance(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        setSharedpreferences();
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

        accountResultCard  =   findById(R.id.card_partner_result);
        accountName = findById(R.id.result_account_name);
        accountId = findById(R.id.result_account_id);
        accountRegisteredDate = findById(R.id.result_account_date);
        loader = findById(R.id.loader_search_fragment);
        buttonSearch =  findById(R.id.button_search_partner);
        inputPartnerId = findById(R.id.search_partner_id);
    }
    private void initEvents(){
        setLoaderGone();
        accountResultCard.setVisibility(View.GONE);
        buttonSearch.setOnClickListener(searchPartnerListener());
    }

    private View.OnClickListener searchPartnerListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPartner();
            }
        };
    }

    private void searchPartner() {

        String partnerId = inputPartnerId.getText().toString();
        if (partnerId == null || partnerId.isEmpty()) {
            AlertUtil.YesAlert(getActivity(), "Invalid Input!");
            return;
        }
        accountResultCard.setVisibility(View.GONE);
        handler.getPartner(partnerId, getRequestKey(), new MyConsumer<WebResponse>() {
            @Override
            public void accept(WebResponse response, Exception error) {
                stopLoading();
                if(error != null){
                    AlertUtil.YesAlert(getActivity(), "Partner Not Found!");
                    return;
                }
                populateSessionData(response);
            }
        });
    }

    private void populateSessionData(WebResponse sessionData) {
        RegisteredRequest account = sessionData.getRegisteredRequest();
        accountName.setText(account.getUsername());
        accountId.setText(account.getRequestId());
        accountRegisteredDate.setText(account.getCreated().toString());

        accountResultCard.setVisibility(View.VISIBLE);
    }

}
