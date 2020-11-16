package com.fajar.android.chatting1.activities.fragments.post;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.components.NewsItem;
import com.fajar.android.chatting1.constants.SharedPreferencesConstants;
import com.fajar.android.chatting1.models.Post;
import com.fajar.android.chatting1.models.PostResponse;
import com.fajar.android.chatting1.service.GetPostOperation;
import com.fajar.android.chatting1.service.NewsService;
import com.fajar.android.chatting1.service.SharedPreferenceUtil;
import com.fajar.android.chatting1.util.Logs;

public class NewsFragment extends PostFragment {

    private LinearLayout navigationButtonsLayout;
    private String buttonLoadLabel = "Muat Berita";
    private int currentPage = 1;
    private int FIRST_PAGE = 1;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logs.log("layout.fragment_news on create view, parent fragment:", getParentFragment().getClass());
        view = inflater.inflate(R.layout.fragment_news, container, false);

        initComponents();
        setDefaultAttributes();

        return view;
    }

    @Override
    protected void setDefaultAttributes() {
        postListLayout.removeAllViews();

        //always load from 1st page
        buttonLoadAgenda.setOnClickListener(loadPostCurrentPage());
        clickSyncNow.setOnClickListener(loadPostCurrentPage());
        buttonLoadAgenda.setText(buttonLoadLabel);
        checkStoredAgendas();

        rollingLoader.setVisibility(View.GONE);
        rollingLoader.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(view.getContext(), android.R.color.background_dark), PorterDuff.Mode.SRC_IN );

    }


    @Override
    protected void initComponents() {
        setParentFragment();
        sharedpreferences = getActivity().getSharedPreferences(SharedPreferencesConstants.SHARED_CONTENT.value, Activity.MODE_PRIVATE);


        infoLayout = view.findViewById(R.id.news_info_wrapper);
        postListLayout = view.findViewById(R.id.news_list);
        buttonLoadAgenda = view.findViewById(R.id.news_btn_load);
        rollingLoader = view.findViewById(R.id.news_loader);
        navigationButtonsLayout = view.findViewById(R.id.news_navbar);
        lastUpdatedLabel = view.findViewById(R.id.txt_news_last_update);
        clickSyncNow = view.findViewById(R.id.click_sync_news_now);
    }
    private View.OnClickListener loadPostCurrentPage() {
        final NewsFragment parent = this;
        return (View v)->{ getNews(parent.getCurrentPage()); };
    }

    private View.OnClickListener loadPostListener(final int page) {
        return (View v)->{ getNews(page); };
    }

    private void getNews(int page) {
        startLoading();
        this.currentPage = page;
        showClickSyncNow(false);
        new GetPostOperation(this).execute(page);
    }

    @Override
    public PostResponse getPost(Object... params) {
        int page;
        try {
            page = Integer.parseInt(params[0].toString());
        } catch (Exception e) {
            page = 1;
        }
        PostResponse response = NewsService.instance().getNews(page);
        return response;
    }

    @Override
    protected   PostResponse getPostFromSharedPreferences(){
       return SharedPreferenceUtil.getNewsData(sharedpreferences);
    }

    @Override
    public String getTabName() {
        return "Berita";
    }

    private void updateNavigationButton() {
        if (null == postData) return;
        try {
            navigationButtonsLayout.removeAllViews();
            final int _currentPage = getCurrentPage();
            List<Integer> buttonValues = postData.displayedNavButtonValues();
            Logs.log("nav button pages: ", buttonValues, "current page: ", _currentPage);
            if (null == buttonValues || buttonValues.size() == 0) {
                return;
            }
           // navigationButtonsLayout.addView(prevButton(_currentPage, buttonValues));

            for (Integer buttonPage :
                    buttonValues) {
                Button navigationButton = createNavigationButton(buttonPage, null);
                navigationButtonsLayout.addView(navigationButton);
            }

          //  navigationButtonsLayout.addView(nextButton(_currentPage, buttonValues));
            Logs.log("updated nav buttons");
        } catch (Exception e) {
            Logs.log("Error creating nav button: ", e);
        }
    }


    private Button prevButton(int _currentPage, List<Integer> buttonValues) {
        Integer prevPage = _currentPage > FIRST_PAGE ? _currentPage - 1 : FIRST_PAGE;
        Button prevButton = createNavigationButton(prevPage, "Previous");
        prevButton.setBackgroundColor(Color.rgb(200,200,200));
        prevButton.setTextColor(Color.DKGRAY);
        return prevButton;
    }

    private Button nextButton(int _currentPage, List<Integer> buttonValues) {
        Integer nextPage = getCurrentPage() < buttonValues.get(buttonValues.size() - 1) ? _currentPage + 1 : _currentPage;
        Button nextButton = createNavigationButton(nextPage, "Next");
        nextButton.setBackgroundColor(Color.rgb(200,200,200));
        nextButton.setTextColor(Color.DKGRAY);
        return nextButton;
    }

    private int getCurrentPage() {
        if (null == postData) {
            return currentPage;
        }
        return postData.getCurrentPageInt2();
    }


    private Button createNavigationButton(Integer buttonPage, String text) {

        Button button = new Button(new ContextThemeWrapper(view.getContext(), R.style.NoPaddingButton), null, 0);
        button.setText(text != null ? text : String.valueOf(buttonPage));

        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);


        if (buttonPage == getCurrentPage()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ShapeDrawable shapedrawable = new ShapeDrawable();
                shapedrawable.setShape(new RectShape());
                shapedrawable.getPaint().setColor(Color.GRAY);
                shapedrawable.getPaint().setStrokeWidth(5f);
                shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                button.setBackground(shapedrawable);
                button.setTextColor(Color.GRAY);
            } else {
                button.setBackgroundColor(Color.DKGRAY);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setTextColor(Color.GRAY);
                button.setBackgroundColor(Color.TRANSPARENT);

            } else {
                button.setTextColor(Color.rgb(200, 200, 200));
                button.setBackgroundColor(Color.GRAY);
            }

        }
        ViewGroup.LayoutParams params;
        if (button.getLayoutParams() == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        } else {
            params = button.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        button.setLayoutParams(params);
        button.setOnClickListener(loadPostListener(buttonPage));
        return button;
    }

    @Override
    public void handleGetPost(PostResponse response, Exception e) {
        stopLoading();
        if (null != e) {
            handleErrorGetAgenda(e);
            return;
        } else if (null == response.getNewsPost()) {
            e = new RuntimeException("Post Not Found");
            handleErrorGetAgenda(e);
            return;

        }
        Logs.log("handle get post currentPage: ", currentPage);
        if (response.getCurrentPageInt2() > 1) {
            response.setCurrentPageJson(response.getCurrentPageInt2());
        } else {
            response.setCurrentPageJson(currentPage);
        }
        setPostData(response);
        updateNavigationButton();


        List<Post> agendas = new ArrayList<>();
        if(null!=response.getNewsPost().getBegin()){
            agendas.add(response.getNewsPost().getBegin());
        }
        agendas.addAll(response.getNewsPost().getRemains());

        postListLayout.removeAllViews();
        infoLayout.removeAllViews();
        for (Post post : agendas) {
            try {
                NewsItem newsItem = new NewsItem(getActivity(), post, isLoadedFromSharedPreference()==false, getActivity());
                addTask(newsItem.getDownloadImageTask());
                postListLayout.addView(newsItem);
            } catch (Exception ex) {
                Logs.log("error create news item: ", e);
            }
        }
        Logs.log("news item size: ", agendas.size());
        if (agendas.size() == 0) {
            handleErrorGetAgenda(new RuntimeException("Data is Empty"));
        }
        //fragmentLayout.removeView(buttonLoadAgenda);
    }

    private void handleErrorGetAgenda(Exception webServiceError) {
        currentPage = 1;
        populateInfo("Error Saat Memuat Berita", webServiceError.getMessage());
    }

    @Override
    public void setPostData(PostResponse postData) {
        super.setPostData(postData);
        SharedPreferenceUtil.storeNewsData(sharedpreferences, postData);
    }
}

