package com.luyin.compattoast;

import android.app.Application;
import android.content.Context;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/9/7 15:35
 * Email：rainyeveningstreet@gmail.com
 */
public class App extends Application {
    public static Context INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
