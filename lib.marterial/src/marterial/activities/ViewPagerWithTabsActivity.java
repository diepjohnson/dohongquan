package marterial.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.example.lib.marterial.R;

import java.util.ArrayList;
import java.util.List;

import marterial.adapter.ViewPagerAdapter;
import marterial.handlers.ViewPagerHandler;
import marterial.model.ViewPagerItem;

public abstract class ViewPagerWithTabsActivity extends AActivity
        implements marterial.interfaces.ViewPager {

    protected ViewPager mViewPager;
    protected PagerSlidingTabStrip mViewPagerTabs;
    private List<ViewPagerItem> mViewPagerItems;
    private ViewPager.OnPageChangeListener mUserOnPageChangeListener;
    private ViewPagerAdapter mViewPagerAdapter;

    public ViewPagerWithTabsActivity() {
        mViewPagerItems = new ArrayList<ViewPagerItem>();
    }

    @Override
    public void selectPage(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener userOnPageChangeListener) {
        mUserOnPageChangeListener = userOnPageChangeListener;
    }

    @Override
    public void updateNavigationDrawerTopHandler(ViewPagerHandler viewPagerHandler,
                                                 int defaultViewPagerPageSelectedPosition) {
        if (viewPagerHandler == null) viewPagerHandler = new ViewPagerHandler(this);
        mViewPagerItems.clear();
        mViewPagerItems.addAll(viewPagerHandler.getViewPagerItems());
        mViewPagerAdapter.notifyDataSetChanged();

        selectPage(defaultViewPagerPageSelectedPosition);

        if (!mViewPagerItems.isEmpty()) showTabs(mViewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.mdl_activity_view_pager_with_tabs);

        ViewPagerHandler handler = getViewPagerHandler();
        if (handler != null && handler.getViewPagerItems() != null) {
            mViewPagerItems = handler.getViewPagerItems();
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mViewPagerItems);
        mViewPager.setAdapter(mViewPagerAdapter);

        int defaultViewPagerPageSelectedPosition = defaultViewPagerPageSelectedPosition();
        if (defaultViewPagerPageSelectedPosition >= 0 &&
                defaultViewPagerPageSelectedPosition < mViewPagerItems.size()) {
            selectPage(defaultViewPagerPageSelectedPosition);
        }

        mViewPagerTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        if (!mViewPagerItems.isEmpty()) showTabs(mViewPager);
    }


    private void showTabs(ViewPager pager) {
        mViewPagerTabs.setTextColor(getResources().getColor(android.R.color.white));
        mViewPagerTabs.setShouldExpand(expandTabs());
        mViewPagerTabs.setOnPageChangeListener(mUserOnPageChangeListener);
        mViewPagerTabs.setViewPager(pager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mViewPagerTabs.setTabBackground(android.R.attr.selectableItemBackground);
        }
    }

    protected abstract boolean expandTabs();

}
