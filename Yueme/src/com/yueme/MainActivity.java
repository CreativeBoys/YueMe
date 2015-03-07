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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.yueme.fragment.BottomFragment;
import com.yueme.fragment.DiscoveryFragment;
import com.yueme.fragment.HomeFragment;
import com.yueme.fragment.TitleFragment;
import com.yueme.fragment.UserFragment;
import com.yueme.fragment.base.BaseFragment;
import com.yueme.interfaces.OnBottomClickListener;


public class MainActivity extends FragmentActivity {
	private FrameLayout fl_title;
	private FrameLayout fl_bottom;
	private ViewPager vp_middle;
	private List<BaseFragment> fragments;
	private TitleFragment titleFragment;
	private BottomFragment bottomFragment;
	private HomePagerAdapter adapter;
	private HomeFragment homeFragment;
	private DiscoveryFragment discoveryFragment;
	private UserFragment userFragment;
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
		userFragment = new UserFragment();
		discoveryFragment = new DiscoveryFragment();
		homeFragment = new HomeFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fl_title, titleFragment);
		transaction.add(R.id.fl_bottom, bottomFragment);
		transaction.commit();
		fragments.add(homeFragment);
		fragments.add(userFragment);
		fragments.add(discoveryFragment);
	}

	private void setListenerAndAdapter() {
		vp_middle.setAdapter(adapter);
		bottomFragment.setOnBottomClickListener(new OnBottomClickListener() {
			
			@Override
			public void onBottomClick(int pos) {
				vp_middle.setCurrentItem(pos-1);
			}
		});
		vp_middle.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				((RadioButton)bottomFragment.rg_bottom.getChildAt(pos)).setChecked(true);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
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
