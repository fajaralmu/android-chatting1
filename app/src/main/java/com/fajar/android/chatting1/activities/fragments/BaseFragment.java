package com.fajar.android.chatting1.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.HashMap;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.activities.HomeActivity;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.handlers.BaseHandler;
import com.fajar.android.chatting1.util.Logs;

public class BaseFragment<H extends  BaseHandler> extends Fragment {

    protected Integer fragmentId = null;
    protected SharedPreferences sharedpreferences;
    private static HashMap<Integer, Class> customFragments = initCustomFragments();
    private String breadCumbLabel = null;
    protected View view;
    protected H handler;
    protected ProgressBar loader;

    public BaseFragment() {
    }

    public void setHandler(H handler){
        this.handler = handler;
    }

    protected  <T extends View> T findById(int id){
        if(null == view){return null;}
        return view.findViewById(id);
    }

    public final String getBreadCumbLabel(){
        return breadCumbLabel;
    }

    public void setBreadCumbLabel(String breadCumbLabel) {
        this.breadCumbLabel = breadCumbLabel;
    }

    @Override
    public void onAttach(Context context) {
        if(getActivity() instanceof HomeActivity){
            ((HomeActivity) getActivity()).setBreadCumbText(getBreadCumbLabel());
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
//        if(getActivity() instanceof HomeActivity){
//            ((HomeActivity) getActivity()).setBreadCumbText(null);
//        }
        super.onDetach();
    }

    public static BaseFragment newInstance(int fragmentId) {
        return newInstance(fragmentId, null);
    }

    public static BaseFragment newInstance(int fragmentId, Class<?> _class){
        return newInstance(fragmentId, _class, null);
    }
    public static BaseFragment newInstance(int fragmentId, Class<?> _class, String breadCumbLabel) {
        if (_class == null && customFragments.get(fragmentId) != null) {
            _class = customFragments.get(fragmentId);
        }
        if (null == _class) {
            _class = BaseFragment.class;
        }
        BaseFragment myFragment = null;
        try {
            myFragment = (BaseFragment) _class.newInstance();
            Bundle args = new Bundle();
            args.putInt("fragmentId", fragmentId);
            args.putString("breadCumbLabel", breadCumbLabel);
            myFragment.setArguments(args);
            myFragment.setFragmentId(fragmentId);
            myFragment.setBreadCumbLabel(breadCumbLabel);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logs.log("BaseFragment fragmentId: ", fragmentId);
        return inflater.inflate(fragmentId == null ? R.layout.fragment_base : fragmentId, container, false);
    }

    private static HashMap<Integer, Class> initCustomFragments() {
        HashMap<Integer, Class> customFragments = new HashMap<Integer, Class>();
        customFragments.put(R.layout.fragment_chatting_list, ChattingListFragment.class);
        customFragments.put(R.layout.fragment_home, HomeFragment.class);
        customFragments.put(R.layout.fragment_search, SearchFragment.class);

        return customFragments;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    public  void startLoading(){
        if(null == loader){
            return;
        }
        loader.setVisibility(View.VISIBLE);
    }

    public void stopLoading(){

        setLoaderGone();
    }

    protected void setLoaderGone(){
        if(null == loader){
            return;
        }
        loader.setVisibility(View.GONE);
    }

    public SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(SharedPreferencesConstants.SHARED_CONTENT.value, Context.MODE_PRIVATE);
    }
}

