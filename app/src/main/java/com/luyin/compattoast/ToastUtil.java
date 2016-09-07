package com.luyin.compattoast;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.luyin.compattoast.widget.CompatToast;


/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/5 11:36
 * Email：rainyeveningstreet@gmail.com
 */
public class ToastUtil {
    /**暂时使用Application*/
    private static Context mContext = App.INSTANCE;
    private static CompatToast mToast;

    public static void showToast(@StringRes int resId) {
        showToast(mContext.getString(resId), CompatToast.LENGTH_SHORT);
    }

    public static void showToast(String text) {
        showToast(text, CompatToast.LENGTH_SHORT);
    }

    private static void showToast(String text, int duration) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast != null) {
                mToast.setText(text);
            } else {
                mToast = CompatToast.makeText(mContext, text, duration);
            }
            mToast.show();
        }
    }
}
