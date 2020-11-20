package com.fajar.android.chatting1.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fajar.android.chatting1.util.Logs;

import java.io.InputStream;

public class ImageViewWithURL {

    private final String url;
    private final ImageView imageView;
    final HandleBitmapResult handleBitmapResult;

    public ImageViewWithURL(ImageView imageView, String url, HandleBitmapResult handleBitmapResult){
        this.imageView = imageView;
        this.url = url;
        this.handleBitmapResult = handleBitmapResult;
    }

    public void populate() {

        imageView.setImageResource(android.R.drawable.ic_menu_camera);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            try {
//                Resources res = imageView.getResources();
//
//                imageView.setImageDrawable(res.getDrawable(android.R.drawable.ic_menu_camera));
//            }catch (Exception e){ }
        }
        // show The Image in a ImageView
        Glide.with(imageView.getContext())
                .load(url)
                .asBitmap()
                .thumbnail(0.5f)

                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(glideListener())
                .into(imageView);

//            return new DownloadImageTask(imageView)
//                    .execute(url);

//            return new DownloadImageTask(imageView)
//                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
         
    }

    private RequestListener< String,Bitmap> glideListener() {
        return new RequestListener< String,Bitmap>(){

            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                imageView.setImageResource(+android.R.drawable.ic_menu_camera);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                imageView.setImageBitmap(resource);
                handleBitmapResult.handleBitmap(resource);
                return true;
            }
        };
    }


    @Deprecated
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onCancelled() {
            Logs.log("Download Image CANCELED");
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            Logs.log("Download Image CANCELED 2");
        }

        protected void onPostExecute(Bitmap result) {
            if(null!=result) {
                bmImage.setImageBitmap(result);
                if(null != handleBitmapResult){
                    handleBitmapResult.handleBitmap(result);
                }
            }else{
                bmImage.setImageResource(+android.R.drawable.ic_menu_camera);
            }
        }
    }

    public static interface  HandleBitmapResult{
        void handleBitmap(Bitmap bitmap);
    }
}
