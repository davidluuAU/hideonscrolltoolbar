package com.whatsthegoss.hideonscrolltoolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by dluu on 3/29/15.
 */
public class ObservableListView extends ListView {

    private float delta;
    private float originTouch;
    private ScrollChangeListener deltaListener;

    public ObservableListView(Context context) {
        super(context);
    }

    public ObservableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (deltaListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    delta = originTouch - event.getRawY();
                    originTouch = event.getRawY();
                    deltaListener.onScrollDelta(delta);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    deltaListener.onScrollFinish(delta);
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (deltaListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    originTouch = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    originTouch = event.getRawY();
                    break;
            }
        }

        return super.onInterceptTouchEvent(event);
    }

    public void setOnScrollDelta(ScrollChangeListener listener) {
        deltaListener = listener;
    }
}
