package marterial.activities;

import marterial.handlers.ActionBarHandler;
import marterial.views.Toolbar;
import marterial.views.ToolbarDefault;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.lib.marterial.R;

public abstract class AActivity extends AppCompatActivity {

    private final static String TOOLBAR_SEARCH_CONSTRAINT_KEY = "ToolbarSearchConstraint";
    private final static String TOOLBAR_SEARCH_IS_SHOWN = "ToolbarSearchIsShown";

    private Toolbar mCustomToolbar;
    private View mShadowView;
    private View mCustomSearchButton;
    private ActionBarHandler mActionBarHandler;

    public void onCreate(Bundle savedInstanceState, int contentView) {
        super.onCreate(savedInstanceState);

        setContentView(contentView);

        // Toolbar Shadow View
        mShadowView = findViewById(R.id.toolbar_shadow);
        if (mShadowView != null && (this instanceof ViewPagerWithTabsActivity)) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mShadowView
                    .getLayoutParams();
            params.topMargin = (int) getResources()
                    .getDimension(R.dimen.mdl_viewpager_with_tabs_height);
        }
        if (enableActionBarShadow()) showActionBarShadow();

        mActionBarHandler = getActionBarHandler();
        if (mActionBarHandler == null) mCustomToolbar = new ToolbarDefault(this);
        else mCustomToolbar = mActionBarHandler.build();


        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content))
                .getChildAt(0);

        mCustomToolbar.getToolbar()
                .setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mCustomToolbar.getToolbar());
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }






    public View getActionBarShadowView() {
        return mShadowView;
    }




    public void showActionBarShadow() {
        if (mShadowView != null) mShadowView.setVisibility(View.VISIBLE);
    }

    public void hideActionBarShadow() {
        if (mShadowView != null) mShadowView.setVisibility(View.INVISIBLE);
    }

    protected abstract boolean enableActionBarShadow();

    protected abstract ActionBarHandler getActionBarHandler();

}
