package com.fajar.android.chatting1.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.util.Logs;

public class MenuButton extends RelativeLayout {
    @StyleableRes
    int index0 = 0;
    @StyleableRes
    int index1 = 1;
    private ImageView imageView;
    private TextView label;
    private LinearLayout layout;
    private int drawableId =  R.drawable.ic_home_black_24dp;
    public MenuButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.component_image_button, this);
        initComponents();
        int[] sets = {R.attr.drawableId, R.attr.buttonLabel};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        for (int i = 0; i< attrs.getAttributeCount(); i++){
            String attributeName = attrs.getAttributeName(i);
            switch (attributeName){
                case "drawableId":
                    drawableId= attrs.getAttributeResourceValue(i, R.drawable.ic_home_black_24dp);
                    imageView.setImageResource( drawableId);
                    break;
                case "buttonLabel":
                    String labelText = attrs.getAttributeValue(i);
                    label.setText(labelText);
                    break;
            }
            Logs.log(i," attr: ", attrs.getAttributeName(i),"=",attrs.getAttributeValue(i));
        }
        setDefaultAttributes();
        typedArray.recycle();
    }

    private void setDefaultAttributes() {
        setImageTint();
        setTextColor();
    }

    private int getImageAndTextColor(){
        return Color.rgb(255,255,255);//.rgb(115,115,115);
    }

    private void setTextColor() {
        label.setTextColor(getImageAndTextColor());
    }

    private void setImageTint() {
        //change image tint
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), drawableId);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, getImageAndTextColor());//.rgb(81,38,74));
        imageView.setImageDrawable(wrappedDrawable);
    }

    public void setImageDimension(int size){
        imageView.getLayoutParams().width = size;
        imageView.getLayoutParams().height = size;
        imageView.requestLayout();
    }

    public void setOnClickListener(OnClickListener listener){
        layout.setOnClickListener(listener);
    }

    private void initComponents() {
        imageView = findViewById(R.id.image_view);
        label = findViewById(R.id.button_label);
        layout = findViewById(R.id.image_button_layout);

    }
}
