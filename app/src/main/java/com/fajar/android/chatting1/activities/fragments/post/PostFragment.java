package com.fajar.android.chatting1.activities.fragments.post;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.fajar.android.chatting1.R;

import com.fajar.android.chatting1.activities.fragments.BaseFragment;
import com.fajar.android.chatting1.models.PostResponse;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.Logs;

public abstract class PostFragment extends BaseFragment implements PostContentPage {
    protected View view;
    protected Button buttonLoadAgenda;
    protected ProgressBar rollingLoader;
    protected LinearLayout postListLayout, infoLayout;
    protected TextView lastUpdatedLabel;
    protected LinearLayout clickSyncNow;
    protected ScrollView mainScrollView;

    protected NewsFragmentView parentFragment;

    protected PostResponse postData;

    protected boolean loadingState;

    protected boolean loadedFromSharedPreference;

    private final List<AsyncTask> tasks = new ArrayList<>();

    public void addTask(AsyncTask task){
        tasks.add(task);
    }

    public String getTabName(){
        return getClass().getSimpleName();
    }

    public void setPostData(PostResponse postData) {
        this.postData = postData;
        if(null!=lastUpdatedLabel && postData.getLastUpdated()!=null){
            lastUpdatedLabel.setText(postData.getLastUpdated().toString());
        }
    }

    protected void setParentFragment(){
        Fragment fragment = getParentFragment();
        if(fragment instanceof  NewsFragmentView){
            parentFragment = (NewsFragmentView) fragment;
        }
    }

    protected  void scrollToTop(){
        if(null != parentFragment){
            parentFragment.scrollToTop();
        }
    }

    @Override
    public void onAttach(Context context) {
        tasks.clear();
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Logs.log("FRAGMENTS ",getClass().getSimpleName(), " ON DETACH will clear task: ", tasks.size());
        for (AsyncTask task:tasks){
            try{
                if(null!=task)
                task.cancel(true);
            }catch (Exception e){

            }
        }
        Logs.log("cleared task: ", tasks.size());
        super.onDetach();
    }

    public void setLoadedFromSharedPreference(boolean loadedFromSharedPreference){
        this.loadedFromSharedPreference = loadedFromSharedPreference;

    }

    public boolean isLoadedFromSharedPreference(){
        return loadedFromSharedPreference;
    }

    protected abstract void initComponents();

    protected abstract void setDefaultAttributes();

    protected abstract PostResponse getPostFromSharedPreferences();

    protected void checkStoredAgendas() {

        if (SharedPreferenceUtil.isAgendaExist(sharedpreferences)) {
            setLoadedFromSharedPreference(true);
            startLoading();
            newsLayoutConstructionOperation(this).execute("");
            showClickSyncNow(true);
        } else {
            showClickSyncNow(false);
        }
    }

    protected void showClickSyncNow(boolean visible){
        if(null!=clickSyncNow  ){
            clickSyncNow.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        }
    }

    public void setLoadingState(boolean loadingState) {
        this.loadingState = loadingState;
    }

    public boolean isLoadingState() {
        return loadingState;
    }



    protected void startLoading() {
        try {

            scrollToTop();
            rollingLoader.setVisibility(View.VISIBLE);
            buttonLoadAgenda.setVisibility(View.INVISIBLE);
            infoLayout.removeAllViews();
            postListLayout.removeAllViews();
            setLoadingState(true);
        } catch (Exception e) {
            Logs.log("Error start loading: ", e);
            setLoadingState(false);
        }
    }

    protected void stopLoading() {
        try {
            rollingLoader.setVisibility(View.GONE);
            buttonLoadAgenda.setVisibility(View.VISIBLE);
            buttonLoadAgenda.setText("Reload");
        } catch (Exception e) {
            Logs.log("Error stop loading: ", e);
        } finally {
            setLoadingState(false);
        }
    }

    protected void populateInfo(String title, String message) {
        infoLayout.removeAllViews();
        ImageView imageView = new ImageView(view.getContext());
        imageView.setImageResource(R.drawable.exclamation);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60));

        TextView titleTextView = new TextView(view.getContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(17);
        TextView messageTextView = new TextView(view.getContext());
        messageTextView.setText(message);

        adjustLabelLayout(titleTextView);
        adjustLabelLayout(messageTextView);

        infoLayout.addView(imageView);
        infoLayout.addView(titleTextView);
        infoLayout.addView(messageTextView);
    }

    public static void adjustLabelLayout(TextView v) {
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setGravity(Gravity.CENTER);
    }

    /**
     * used when loaded from shared preference
     * @param parent
     * @return
     */
    protected AsyncTask<String, Void, PostResponse> newsLayoutConstructionOperation(final PostFragment parent) {
        return new AsyncTask<String, Void, PostResponse>() {
            @Override
            protected PostResponse doInBackground(String... strings) {
                Logs.log("construct new doInBackground ", parent.getClass());
                PostResponse agendaData = parent.getPostFromSharedPreferences();

                return agendaData;

            }

            private void handle(PostResponse postResponse){
                if (null != postResponse) {
                    parent.handleGetPost(postResponse, null);
                    showClickSyncNow(true);
                    parent.setLoadedFromSharedPreference(false);
                } else {
                    parent.populateInfo("Tidak ada agenda untuk ditampilkan", "Cek koneksi internet Anda sebelum memuat agenda");
                }
            }

            @Override
            protected void onPostExecute(PostResponse postResponse) {
                stopLoading();
                handle(postResponse);

            }
        };
    }
}
