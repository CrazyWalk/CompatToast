package com.luyin.compattoast.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.luyin.compattoast.R;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 解决通知关闭后Toast无法使用的情况
 * Author：洪培林
 * Created Time:2016/8/5 16:35
 * Email：rainyeveningstreet@gmail.com
 */
public class CompatToast {
    private static final String TAG = "Toast";
    private Context mContext;
    final TN mTN;
    public WindowManager mWindowManager;
    private static BlockingQueue<CompatToast> mQueue = new LinkedBlockingDeque<>();
    private static AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private long mDurationMillis = 2000;
    private View mNextView;
    int mDuration;

    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    public CompatToast(Context context) {
        this.mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mTN = new TN(mWindowManager);
        mTN.mY = context.getResources().getDimensionPixelSize(R.dimen.toast_y_offset);

    }

    public static CompatToast makeText(Context context, CharSequence text, int duration) {
        CompatToast result = new CompatToast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.layout_toast, null);
        TextView tv = (TextView) v.findViewById(R.id.message);
        tv.setText(text);

        result.mNextView = v;
        result.mDuration = duration;

        return result;
    }


    public void setText(CharSequence s) {
        if (mNextView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView) mNextView.findViewById(R.id.message);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }

    public void setDuration(int duration) {
        if (duration == CompatToast.LENGTH_LONG) {
            mDurationMillis = 1500;
        } else {
            mDurationMillis = 2000;
        }

    }

    public void show() {
        if (mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        TN tn = mTN;
        tn.mNextView = mNextView;
        if (mQueue.contains(this)) {
            return;
        }
        mQueue.offer(this);

        if (mAtomicInteger.get() == 0) {
          //  Log.i(TAG, "队列为空 执行show： ");
            mAtomicInteger.incrementAndGet();
            mTN.show();
        }
    }

    public void cancel() {
        if (0 == mAtomicInteger.get() && mQueue.isEmpty()) return;
        if (this.equals(mQueue.peek())) {
            mTN.hide();
        }
    }

    private static class TN {
        final Runnable mShow = new Runnable() {
            @Override
            public void run() {
                handleShow();
            }
        };
        final Runnable mHide = new Runnable() {
            @Override
            public void run() {
                handleHide();
            }
        };
        final Runnable mActivate = new Runnable() {
            @Override
            public void run() {
                activeQueue();
            }
        };
        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        final Handler mHandler = new Handler();

        int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        int mX, mY;
        View mView;
        View mNextView;
        WindowManager mWM;

        TN(WindowManager mWM) {
            final WindowManager.LayoutParams params = mParams;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.setTitle("Toast");
            this.mWM = mWM;

        }

        public void show() {
            mHandler.post(mActivate);
        }

        public void hide() {
            mHandler.removeCallbacks(mActivate);
            mHandler.post(mHide);
            mHandler.post(mActivate);
        }

        private void activeQueue() {
            CompatToast toast = mQueue.peek();
            if (toast == null) {
                mAtomicInteger.decrementAndGet();
            } else {
                mHandler.post(mShow);
                mHandler.postDelayed(mHide, toast.mDurationMillis);
                mHandler.postDelayed(mActivate, toast.mDurationMillis);
            }

        }

        private void handleHide() {
            if (mView != null) {
                // note: checking parent() just to make sure the view has
                // been added...  i have seen cases where we get here when
                // the view isn't yet added, so let's try not to crash.
                if (mView.getParent() != null) {
                    mWM.removeView(mView);
                }

                mView = null;
                mQueue.poll();
            }
        }

        private void handleShow() {
            if (mView != mNextView) {
                // remove the old view if necessary
                handleHide();
                mView = mNextView;
                final int gravity = mGravity;
                mParams.gravity = gravity;
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                    mParams.horizontalWeight = 1.0f;
                }
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                    mParams.verticalWeight = 1.0f;
                }
                mParams.x = mX;
                mParams.y = mY;
                if (mView.getParent() != null) {
                    mWM.removeView(mView);
                }
                mWM.addView(mView, mParams);

            }
        }
    }
}
