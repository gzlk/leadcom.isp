package com.leadcom.android.isp.holder.individual;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.Blur;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.share.Shareable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <b>功能描述：</b>首页 - 个人 - 高斯模糊背景的头像部分<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/21 11:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/21 11:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserHeaderBlurViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_user_header_container)
    private RelativeLayout root;
    @ViewId(R.id.ui_tool_individual_name)
    private TextView nameTextView;
    @ViewId(R.id.ui_tool_individual_edit_icon)
    private CustomTextView editIconView;
    @ViewId(R.id.ui_tool_individual_additional)
    private TextView additionalTextView;
    @ViewId(R.id.ui_holder_view_user_header_layout)
    private View userHeaderLayout;
    @ViewId(R.id.ui_holder_view_user_header)
    private ImageDisplayer userHeader;
    @ViewId(R.id.ui_holder_view_user_header_background)
    private ImageDisplayer headerBackground;
    @ViewId(R.id.tool_view_individual_top_padding)
    private LinearLayout topPadding;

    public UserHeaderBlurViewHolder(View itemView, BaseFragment fragment, boolean isSelf) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        paddingContent(isSelf);
        userHeader.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                userHeaderLayout.performClick();
            }
        });
    }

    private void paddingContent(boolean isSelf) {
        int status = BaseActivity.getStatusHeight(fragment().Activity());
        int actionSize = getDimension(R.dimen.ui_static_dp_20);
        topPadding.setPadding(0, status + (isSelf ? actionSize : 0), 0, 0);
    }

    public void showContent(User user) {
        nameTextView.setText(isEmpty(user.getName()) ? StringHelper.getString(R.string.ui_text_user_information_name_empty) : user.getName());
        editIconView.setVisibility(user.isMySelf() ? View.VISIBLE : View.INVISIBLE);
        final String header = user.getHeadPhoto();
        userHeader.displayImage(getDefaultHeader(header), getDimension(R.dimen.ui_static_dp_60), false, false);
        userHeader.setTag(R.id.hlklib_ids_custom_view_click_tag, header);
        additionalTextView.setText(isEmpty(user.getSignature()) ? StringHelper.getString(R.string.ui_text_user_information_signature_empty) : Html.fromHtml(user.getSignature()));
        if (!isEmpty(header)) {
            fragment().Handler().post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) headerBackground.getLayoutParams();
                    bWidth = root.getMeasuredWidth();
                    bHeight = root.getMeasuredHeight();
                    params.height = bHeight;
                    headerBackground.setLayoutParams(params);
                    String blur = getBlurImage(header);
                    if (!isEmpty(blur)) {
                        clearHandler();
                        //changeColor(header);
                        headerBackground.displayImage("file://" + blur, bWidth, bHeight, false, false);
                    } else {
                        blurHeader();
                    }
                }
            });
        }
    }

    private int bWidth, bHeight;

    private void blurHeader() {
        fragment().Handler().postDelayed(runnable, 3000);
    }

    private String getDefaultHeader(String header) {
        return isEmpty(header) ? ("drawable://" + R.drawable.img_default_user_header) : header;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Object object = userHeader.getTag(R.id.hlklib_ids_custom_view_click_tag);
            String header = null == object ? "" : (String) object;
            userHeader.displayImage(getDefaultHeader(header), getDimension(R.dimen.ui_static_dp_60), false, false);
            String blur = getBlurImage(header);
            if (!isEmpty(blur)) {
                clearHandler();
                //changeColor(header);
                headerBackground.displayImage("file://" + blur, bWidth, bHeight, false, false);
            } else {
                log("no blur image, 3s to try again.");
                blurHeader();
            }
        }
    };

    private void clearHandler() {
        fragment().Handler().removeCallbacks(runnable);
    }

    @Override
    public void detachedFromWindow() {
        clearHandler();
        super.detachedFromWindow();
    }

    private String getBlurImage(String httpUrl) {
        String source = Shareable.getLocalPath(httpUrl);
        if (isEmpty(source)) {
            return "";
        }
        assert source != null;
        File exist = new File(source);
        if (!exist.exists()) {
            return "";
        }
        String local = HttpHelper.helper().getLocalFilePath(httpUrl, App.IMAGE_DIR) + ".blured";
        File file = new File(local);
        if (!file.exists()) {
            // No image found => let's generate it!
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap image = BitmapFactory.decodeFile(source, options);
            Bitmap newImg = Blur.fastblur(fragment().Activity(), image, 10);
            storeImage(newImg, file);
            image.recycle();
            assert newImg != null;
            newImg.recycle();
        }
        return local;
    }

    private void storeImage(Bitmap image, File pictureFile) {
        if (pictureFile == null) {
            log("Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            log("File not found: " + e.getMessage());
        } catch (IOException e) {
            log("Error accessing file: " + e.getMessage());
        }
    }

    private void changeColor(String url) {
        Bitmap bitmap = BitmapFactory.decodeFile(Shareable.getLocalPath(url));
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (null == vibrant) {
                    vibrant = palette.getDarkVibrantSwatch();
                }
                if (null == vibrant) {
                    vibrant = palette.getLightVibrantSwatch();
                }
                if (null == vibrant) {
                    vibrant = palette.getMutedSwatch();
                }
                if (null == vibrant) {
                    vibrant = palette.getDarkMutedSwatch();
                }
                if (null != vibrant) {
                    int color = (vibrant.getRgb());
                    //nameTextView.setTextColor(color);
                    //additionalTextView.setTextColor(color);
                    //((PersonalityFragment) fragment()).resetIconColor(color);
                }
            }
        });
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *                  Android中我们一般使用它的16进制，
     *                  例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *                  red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *                  所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return color
     */
    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.argb(alpha, red, green, blue);
    }

    @Click({R.id.ui_holder_view_user_header_layout, R.id.ui_tool_individual_edit_icon, R.id.ui_holder_view_user_header_name_layout})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
