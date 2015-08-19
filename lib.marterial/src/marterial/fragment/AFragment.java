package marterial.fragment;

import marterial.activities.AActivity;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.example.lib.marterial.R;

public abstract class AFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (getActivity() instanceof AActivity) {
            View shadowView = ((AActivity) getActivity()).getActionBarShadowView();
            if (shadowView != null) {
                if (this instanceof ViewPagerWithTabsFragment) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) shadowView
                            .getLayoutParams();
                    params.topMargin += ((int) getResources()
                            .getDimension(R.dimen.mdl_viewpager_with_tabs_height));
                    shadowView.setLayoutParams(params);
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity() instanceof AActivity) {
            View shadowView = ((AActivity) getActivity()).getActionBarShadowView();
            if (shadowView != null) {
                if (this instanceof ViewPagerWithTabsFragment) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) shadowView
                            .getLayoutParams();
                    params.topMargin -= ((int) getResources()
                            .getDimension(R.dimen.mdl_viewpager_with_tabs_height));
                    shadowView.setLayoutParams(params);
                }
            }
        }
    }


    public void showActionBarShadow() {
        if (getActivity() instanceof AActivity) ((AActivity) getActivity()).showActionBarShadow();
    }

    public void hideActionBarShadow() {
        if (getActivity() instanceof AActivity) ((AActivity) getActivity()).hideActionBarShadow();
    }

}
