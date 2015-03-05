package com.yueme;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

import com.yueme.fragment.BottomFragment;
import com.yueme.fragment.TitleFragment;
import com.yueme.fragment.base.BaseFragment;


public class MainActivity extends FragmentActivity {
	private FrameLayout fl_title;
	private FrameLayout fl_bottom;
	private ViewPager vp_middle;
	private List<BaseFragment> fragments;
	private TitleFragment titleFragment;
	private BaseFragment bottomFragment;
	private HomePagerAdapter adapter;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		fragments = new ArrayList<BaseFragment>();
		fl_bottom = (FrameLayout) findViewById(R.id.fl_bottom);
		fl_title = (FrameLayout) findViewById(R.id.fl_title);
		vp_middle = (ViewPager) findViewById(R.id.vp_middle);
		adapter = new HomePagerAdapter(getSupportFragmentManager());
		initFragments();
		setListenerAndAdapter();
	}

	private void initFragments() {
		titleFragment = new TitleFragment();
		bottomFragment = new BottomFragment();
		
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fl_title, titleFragment);
		transaction.add(R.id.fl_bottom, bottomFragment);
		
		
		transaction.commit();
	}

	private void setListenerAndAdapter() {
		vp_middle.setAdapter(adapter);
	}
	
	private class HomePagerAdapter extends FragmentPagerAdapter {

		public HomePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}
}
