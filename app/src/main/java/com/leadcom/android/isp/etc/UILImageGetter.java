package com.leadcom.android.isp.etc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.InputStream;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 00:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 00:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UILImageGetter implements Html.ImageGetter {

    private Context c;
    private TextView container;

    /***
     * Construct the UILImageGetter which will execute AsyncTask and refresh the container
     * @param t textView
     * @param c content
     */
    public UILImageGetter(View t, Context c) {
        this.c = c;
        this.container = (TextView) t;
    }

    @Override
    public Drawable getDrawable(String source) {
        UrlImageDownloader urlDrawable = new UrlImageDownloader(c.getResources(), source);
        urlDrawable.drawable = c.getResources().getDrawable(R.mipmap.img_image_default);

        ImageLoader.getInstance().loadImage(source, new SimpleListener(urlDrawable));
        return urlDrawable;
    }

    private class SimpleListener extends SimpleImageLoadingListener {
        UrlImageDownloader urlImageDownloader;

        public SimpleListener(UrlImageDownloader downloader) {
            super();
            urlImageDownloader = downloader;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            int width = loadedImage.getWidth();
            int height = loadedImage.getHeight();
            int newWidth = width;
            int newHeight = height;

            if( width > container.getWidth() ) {
                newWidth = container.getWidth();
                newHeight = (newWidth * height) / width;
            }

            container.getLayoutParams().width = newWidth;
            container.getLayoutParams().height = newHeight;
            Drawable result = new BitmapDrawable(c.getResources(), loadedImage);
            result.setBounds(0, 0,newWidth, newHeight);
            urlImageDownloader.setBounds(0, 0, newWidth, newHeight);
            urlImageDownloader.drawable = result;
            container.requestLayout();
            container.invalidate();
        }
    }

    private class UrlImageDownloader extends BitmapDrawable {
        public Drawable drawable;

        /**
         * Create a drawable by decoding a bitmap from the given input stream.
         *
         * @param res
         * @param is
         */
        public UrlImageDownloader(Resources res, InputStream is) {
            super(res, is);
        }

        /**
         * Create a drawable by opening a given file path and decoding the bitmap.
         *
         * @param res
         * @param filepath
         */
        public UrlImageDownloader(Resources res, String filepath) {
            super(res, filepath);
            drawable = new BitmapDrawable(res, filepath);
        }

        /**
         * Create drawable from a bitmap, setting initial target density based on
         * the display metrics of the resources.
         *
         * @param res
         * @param bitmap
         */
        public UrlImageDownloader(Resources res, Bitmap bitmap) {
            super(res, bitmap);
        }

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
