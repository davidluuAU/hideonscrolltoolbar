package com.whatsthegoss.hideonscrolltoolbar;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PagerFragment extends Fragment {

    private static final String[] values = new String[] {
        "Line 1", "Line 2", "Line 3", "Line 4", "Line 5", "Line 6", "Line 7", "Line 8", "Line 9", "Line 10",
        "Line 11", "Line 12", "Line 13", "Line 14", "Line 15", "Line 16", "Line 17", "Line 18", "Line 19", "Line 20",
        "Line 21", "Line 22", "Line 23", "End",
    };

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView listView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ToolbarHelper toolbarHelper;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PagerFragment newInstance(int sectionNumber) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        initialiseListView();
        initialiseSwipeRefreshLayout();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        toolbarHelper = new ToolbarHelper();
        toolbarHelper.initToolBar(getActivity(), this);

        super.onActivityCreated(savedInstanceState);
    }

    private void initialiseListView() {

        ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1, values);

        listView.setAdapter(adapter);
    }

    private void initialiseSwipeRefreshLayout() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
