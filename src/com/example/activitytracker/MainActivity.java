package com.example.activitytracker;

import marterial.activities.ViewPagerWithTabsActivity;
import marterial.handlers.ActionBarDefaultHandler;
import marterial.handlers.ActionBarHandler;
import marterial.handlers.ViewPagerHandler;
import android.os.Bundle;

public class MainActivity extends ViewPagerWithTabsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public ViewPagerHandler getViewPagerHandler() {
		return new ViewPagerHandler(MainActivity.this)
				.addPage(
						"Section1",
						MainFragment
								.newInstance("Material Design ViewPager with Expanded Tabs"))
				.addPage(
						"Section2",
						MainFragment
								.newInstance("Material Design ViewPager with Expanded Tabs"))
				.addPage(
						"Section3",
						MainFragment
								.newInstance("Material Design ViewPager with Expanded Tabs"));
	}

	@Override
	public int defaultViewPagerPageSelectedPosition() {
		return 0;
	}

	@Override
	protected boolean expandTabs() {
		return true;
	}

	@Override
	protected boolean enableActionBarShadow() {
		return false;
	}

	@Override
	protected ActionBarHandler getActionBarHandler() {
		return new ActionBarDefaultHandler(this);
	}
}
