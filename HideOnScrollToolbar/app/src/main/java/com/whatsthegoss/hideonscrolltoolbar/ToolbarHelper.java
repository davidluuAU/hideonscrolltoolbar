package com.whatsthegoss.hideonscrolltoolbar;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.Toolbar;
import com.astuetz.PagerSlidingTabStrip;


public class ToolbarHelper {

    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    private Toolbar toolbar;

    public ToolbarHelper initToolBar(Activity activity, Fragment fragment) {

        pager = (ViewPager) activity.findViewById(R.id.pager);
        toolbar = (Toolbar) activity.findViewById(R.id.tool_bar);
        tabs = (PagerSlidingTabStrip) activity.findViewById(R.id.tabs);

        // Connect the listview scroll listeners with the ToolbarHelper
        setListView((ListView) fragment.getView().findViewById(android.R.id.list));

        // Initialise the position of the pager so that it sits beneath the tabs
        final ViewTreeObserver vto = pager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                tabs.setY(toolbar.getY() + toolbar.getHeight());
                pager.setY(toolbar.getY() + toolbar.getHeight() + tabs.getHeight());
                pager.setPadding(0, 0, 0, tabs.getHeight());

                if (vto.isAlive()) {
                    vto.removeOnGlobalLayoutListener(this);
                }
            }
        });

        return this;
    }

    public ToolbarHelper setListView(ListView listView) {
        if (listView != null && listView instanceof ObservableListView) {
            ((ObservableListView) listView).setOnScrollDelta(new ListScrollListener());
        }

        return this;
    }

    /**
     * This method is used to scroll the toolbar upwards or downwards by the given delta amount. This involves moving
     * three independent views. They are the Toolbar, the Tabs and the Fragment content identified by the id scrollable_content.
     *
     * Rules:
     * The Toolbar should scroll up and down with a 1-to-1 relation with the users finger. It will scroll upwards and off
     * the screen until it is completely out of view. It will scroll downwards and into view up until its fully in view
     *
     * The Tabs sit beneath the Toolbar. They will scroll up and down with the Toolbar, always sitting beneath the
     * toolbar.
     *
     * The fragment content fills the entire screen, however it has padding which pushes its contents beneath
     * the Tabs. This gives an effect that it sits beneath the Tabs. The reason this is done is two fold:
     * 1. When the toolbar, tabs and fragment content are all in view, the fragment content is pushed down beneath the visible
     * screen. As the user scrolls upwards, the fragment content is translated upwards in the Y direction and the bottom
     * of the fragment content comes into view. If this wasn't done, then the fragment content height will be too short to take up
     * all the visible screen space and you would have a strange effect of the fragment content appearing as if it
     * cropped.
     * 2. The content of the fragment needs to begin after the tabs so that when a pull-to-refresh occurs, the
     * animating icon does not appear beneath the Tabs.
     *
     * @param delta The amount with which to scroll the toolbar
     * @return The amount the toolbar scrolled
     */
    protected void scrollToolbar(float delta) {

        final float toolBarY = toolbar.getY();
        final float toolBarHeight = toolbar.getHeight();
        final float tabsHeight = tabs.getHeight();

        if (delta == 0) {
            return;
        }

        // Toolbar positioning
        float newToolBarY;
        if (delta > 0) { // UP!
            newToolBarY = toolBarY - delta;
        } else {
            newToolBarY = toolBarY + (delta * -1);
        }

        if (newToolBarY < toolBarHeight * -1) {
            newToolBarY = toolBarHeight * -1;
        } else if (newToolBarY > 0) {
            newToolBarY = 0;
        }

        toolbar.setY(newToolBarY);

        //Tabs positioning
        final float newTabY = newToolBarY + toolBarHeight;

        // This is a simple fix for an issue on some devices where a gap is shown between the tabs and toolbar when
        // they are animating. What I do is overlap the two views by 1 pixel when they are animating
        if (newTabY != toolBarHeight && newTabY != 0) {
            tabs.setY(newTabY - 1.0f);
        } else {
            tabs.setY(newTabY);
        }

        // Fragment content positioning
        final float fragmentY = pager.getY();

        if (delta > 0) { // UP
            float newFragmentContentY = fragmentY - delta;

            // Limit it at the tabs height
            final float limit = tabsHeight;
            if (newFragmentContentY < limit) {
                newFragmentContentY = limit;
            }

            pager.setY(newFragmentContentY);
        } else { // DOWN

            float newFragmentContentY = fragmentY + (-1 * delta);

            // Limit the fragment Y position to beneath the toolbar and tabs
            final float limit = toolBarHeight + tabsHeight;
            if (newFragmentContentY > limit) {
                newFragmentContentY = limit;
            }

            pager.setY(newFragmentContentY);
        }
    }

    /**
     * This method is responsible for snapping the tool bar up or down. The toolbar and tabs will only snap upwards if
     * the toolbar has scrolled half or more its height off the screen, AND the pager position has reached the top of
     * the screen. This second condition is required so that the tabs and toolbar don't animate upwards and reveal empty
     * space beneath it.
     *
     * @param delta The current amount scrolled
     */
    protected void snapToolBar(float delta) {

        final int toolbarHeight = toolbar.getHeight();
        final int toolbarHalfHeight = toolbarHeight / 2;
        final int tabsHeight = tabs.getHeight();

        if (delta >= 0) { // UP
            if (toolbar.getY() < (-1 * toolbarHalfHeight) && pager.getY() == tabsHeight) {
                snapUp();
            } else if (tabs.getY() > 0.0f) {
                snapDown();
            }
        } else { // DOWN
            snapDown();
        }
    }

    /**
     * This method animates the toolbar and tabs upwards to hide the toolbar and only show the tabs
     */
    public void snapUp() {
        if (toolbar != null && tabs != null) {
            toolbar.animate().translationY(-1 * toolbar.getHeight()).setDuration(HEADER_HIDE_ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).start();
            tabs.animate().translationY(0.0f).setDuration(HEADER_HIDE_ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    /**
     * This method animates the toolbar and tabs downwards to show both the toolbar and the tabs
     */
    public void snapDown() {
        if (toolbar != null && tabs != null) {
            toolbar.animate().translationY(0.0f).setDuration(HEADER_HIDE_ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).start();
            tabs.animate().translationY(toolbar.getHeight()).setDuration(HEADER_HIDE_ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    protected class ListScrollListener implements ScrollChangeListener {
        @Override
        public void onScrollDelta(float delta) {
            scrollToolbar(delta);
        }

        @Override
        public void onScrollFinish(float delta) {
            snapToolBar(delta);
        }
    }
}
