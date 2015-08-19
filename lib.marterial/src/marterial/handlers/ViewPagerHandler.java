package marterial.handlers;

import java.util.ArrayList;
import java.util.List;

import marterial.model.ViewPagerItem;
import android.content.Context;
import android.support.v4.app.Fragment;

public class ViewPagerHandler {

    private final Context mContext;
    private List<ViewPagerItem> mItems;

    public ViewPagerHandler(Context context) {
        mContext = context;
        mItems = new ArrayList<ViewPagerItem>();
    }

    public ViewPagerHandler addPage(int titleResource, Fragment fragment) {
        ViewPagerItem item = new ViewPagerItem();
        item.setTitle(mContext, titleResource);
        item.setFragment(fragment);
        mItems.add(item);
        return this;
    }

    public ViewPagerHandler addPage(String title, Fragment fragment) {
        ViewPagerItem item = new ViewPagerItem();
        item.setTitle(title);
        item.setFragment(fragment);
        mItems.add(item);
        return this;
    }

    public List<ViewPagerItem> getViewPagerItems() {
        return mItems;
    }

}
