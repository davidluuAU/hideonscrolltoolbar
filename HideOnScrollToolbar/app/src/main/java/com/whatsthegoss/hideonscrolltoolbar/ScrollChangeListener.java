package com.whatsthegoss.hideonscrolltoolbar;

/**
 * Created by dluu on 3/29/15.
 */
public interface ScrollChangeListener {
    void onScrollDelta(float delta);

    void onScrollFinish(float delta);
}