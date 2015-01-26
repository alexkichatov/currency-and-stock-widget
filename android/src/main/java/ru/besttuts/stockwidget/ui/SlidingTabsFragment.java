package ru.besttuts.stockwidget.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.besttuts.stockwidget.R;
import ru.besttuts.stockwidget.ui.view.SlidingTabLayout;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 24.01.2015.
 */
public class SlidingTabsFragment extends Fragment {

    private static final String TAG = makeLogTag(SlidingTabsFragment.class);

    /**
     * A custom {@link android.support.v4.view.ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link android.support.v4.view.ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    private static final String ARG_WIDGET_ID = "widgetId";
    private static int mWidgetId;

    public static SlidingTabsFragment newInstance(int widgetId) {
        SlidingTabsFragment fragment = new SlidingTabsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            mWidgetId = savedInstanceState.getInt(ARG_WIDGET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SlidingTabsPagerAdapter(getActivity().getSupportFragmentManager()));
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }

    class SlidingTabsPagerAdapter extends FragmentPagerAdapter {

        SlidingTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = PlaceStockItemsFragment.newInstance(mWidgetId, null);
                    break;
                case 1:
                    fragment = new ConfigPreferenceFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = String.valueOf(super.getPageTitle(position));
            switch (position) {
                case 0:
                    title = getString(R.string.action_quotes);
                    break;
                case 1:
                    title = getString(R.string.action_settings);
                    break;
            }
            return title;
        }
    }
}
