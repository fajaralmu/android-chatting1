package com.fajar.android.chatting1.components;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fajar.android.chatting1.R;
import com.fajar.android.chatting1.util.Logs;

public class ImageLabel extends ConstraintLayout {
    @StyleableRes
    int index0 = 0;
    @StyleableRes
    int index1 = 1;
    private ImageView imageView;
    private TextView label;
    private int drawableId =  R.drawable.ic_home_black_24dp;

    private Activity parentActivity;

    public ImageLabel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.component_image_with_text, this);
        initComponents();

        int[] sets = {R.attr.imageLabelDrawableId, R.attr.imageLabelText};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        for (int i = 0; i< attrs.getAttributeCount(); i++){
            String attributeName = attrs.getAttributeName(i);
            switch (attributeName){
                case "imageLabelDrawableId":
                    drawableId = attrs.getAttributeResourceValue(i, R.drawable.ic_home_black_24dp);
                    imageView.setImageResource( drawableId);
                    break;
                case "imageLabelText":
                    String labelText = attrs.getAttributeValue(i);
                    label.setText(labelText);
                    break;
            }
            Logs.log(i," attr: ", attrs.getAttributeName(i),"=",attrs.getAttributeValue(i));
        }
        imageView.setAdjustViewBounds(true);
        initEvents();
        typedArray.recycle();
    }

    private boolean imageFitToScreen;

    public boolean isImageFitToScreen(){
        return  imageFitToScreen;
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public void initEvents(){
        if(parentActivity == null){return;}
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri path =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getContext().getResources().getResourcePackageName(drawableId)
                        + '/' + getContext().getResources().getResourceTypeName(drawableId)
                        + '/' + getContext().getResources().getResourceEntryName(drawableId) );
                //Uri.parse("android.resource://"+ BuildConfig.APPLICATION_ID+"/drawable/_class");
                Logs.log("loading image path:",path);
                try {
                    intent.setDataAndType(path, "image/*");
                    parentActivity.startActivity(intent);
                }catch (Exception e){
                    Logs.log("error view image: ", e);
                }
//                if(isImageFitToScreen()) {
//                    Toast.makeText(getContext(), "NORMAL SIZE!", Toast.LENGTH_LONG).show();
//                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    imageView.setAdjustViewBounds(true);
//                   setImageFitToScreen(false);
//                }else{
//                    Toast.makeText(getContext(), "FULLSCREEN!", Toast.LENGTH_LONG).show();
//                    imageView.setLayoutParams(new LinearLayout.LayoutParams(Dimension.getScreenWidth(getContext()), Dimension.getScreenHeight(getContext())));
//                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                   setImageFitToScreen(true);
//                }

            }
        });
    }

    private void initComponents() {
        imageView = (ImageView) findViewById(R.id.image_with_text_img);
        label = (TextView) findViewById(R.id.image_with_text_label);

    }

    public void setImageFitToScreen(boolean imagetFitToScreen) {
        this.imageFitToScreen = imagetFitToScreen;
    }
}
