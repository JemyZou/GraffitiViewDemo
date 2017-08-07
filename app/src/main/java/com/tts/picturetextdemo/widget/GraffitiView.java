package com.tts.picturetextdemo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tts.picturetextdemo.R;

/**
 * Author: Jemy
 **/
public class GraffitiView extends View {
    private static final String TAG = "GraffitiView";
    private Bitmap defaultBitmap;
    private Bitmap backgroundBitmap;
    private float downX;//触摸点X坐标
    private float downY;//触摸点Y坐标
    private float startX;//开始绘制的X坐标
    private float startY;//开始绘制的Y坐标
    private Paint paint;

    public GraffitiView(Context context) {
        this(context, null);
    }

    public GraffitiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GraffitiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GraffitiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //get default background picture
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GraffitiView);
        int resourceId = typedArray.getResourceId(R.styleable.GraffitiView_defaultBackground, -1);
        if (resourceId == -1) {
            throw new IllegalArgumentException("background resource can't be null");
        }
        defaultBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        scaleBitmap();
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
    }

    private void scaleBitmap() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (defaultBitmap != null) {
            Log.d(TAG, "Bitmap width:"+defaultBitmap.getWidth()+",height:"+defaultBitmap.getHeight());
            Log.d(TAG, "Screen width:"+displayMetrics.widthPixels+",height:"+displayMetrics.heightPixels);
            if (defaultBitmap.getWidth()*1.0f / defaultBitmap.getHeight() < displayMetrics.widthPixels*1.0f / displayMetrics.heightPixels) {
                //宽高比小，缩放到高度占满屏幕
                defaultBitmap = Bitmap.createScaledBitmap(defaultBitmap, Math.round(defaultBitmap.getWidth() * displayMetrics.heightPixels*1.0f / defaultBitmap.getHeight()), displayMetrics.heightPixels, true);
            } else {
                //宽高比大，缩放到宽度占满屏幕
                defaultBitmap = Bitmap.createScaledBitmap(defaultBitmap, displayMetrics.widthPixels, Math.round(defaultBitmap.getHeight() * displayMetrics.widthPixels*1.0f / defaultBitmap.getWidth()), true);
            }
            backgroundBitmap = defaultBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: width"+defaultBitmap.getWidth()+",height:"+getHeight());
        canvas.drawBitmap(getBitmap(), 0, 0, null);
    }

    private Bitmap getBitmap() {
        Canvas canvas = new Canvas(backgroundBitmap);
        canvas.drawLine(startX,startY,downX,downY,paint);
        Log.d(TAG, "getBitmap drawLine:startX "+startX+",startY:"+startY+",downX:"+downX+",downY:"+downY);
        startX = downX;
        startY = downY;
        return backgroundBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        downX = event.getX();
        downY = event.getY();
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            startX = downX;
            startY =downY;
            return true;
        } else if (MotionEvent.ACTION_MOVE == event.getAction()) {
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
