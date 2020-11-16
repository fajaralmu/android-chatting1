package com.fajar.android.chatting1.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dimension implements Serializable {

    public static final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private int x, y, height, width;
    private int allMargin, marginTop, marginBottom, marginLeft, marginRight;
    private int allPadding, paddingLeft, paddingRight, paddingTop, paddingBottom;
    @Builder.Default
    private int gravity = Gravity.NO_GRAVITY;

    public  Dimension(int w, int h){
        width = w;
        height = h;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static LinearLayout setLinearLayoutParams(LinearLayout LinearLayout, int w, int h) {
        LinearLayout.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        return LinearLayout;
    }

    public static LinearLayout setLinearLayoutParamsPercentage(LinearLayout View, double w, double h) {
        w = w <1?100.0:w;
        h = h <1?100.0:h;
        int ScreenWidth = getScreenWidth(View.getContext());
        int ScreenHeight = getScreenHeight(View.getContext());

        return setLinearLayoutParams(View, (int)(w * ScreenWidth), (int)(h * ScreenHeight));
    }

    public static <T extends View>  T  setViewLayoutParams(View View, int w, int h) {
        LinearLayout.LayoutParams lp = View.getLayoutParams() == null? new LinearLayout.LayoutParams(w, h) : (LinearLayout.LayoutParams) View.getLayoutParams();
        lp.width = w;
        lp.height = h;
        View.setLayoutParams( lp);
        View.requestLayout();
        return (T)View;
    }

    public static <T extends View>  T setViewLayoutParamsPercentage(View View, double w, double h) {

        int ScreenWidth = getScreenWidth(View.getContext());
        int ScreenHeight = getScreenHeight(View.getContext());

        return setViewLayoutParams(View, (int)(w * ScreenWidth), (int)(h * ScreenHeight));
    }

}