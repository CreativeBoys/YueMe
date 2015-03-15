package com.yueme.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yueme.R;
import com.yueme.fragment.base.BaseFragment;

public class TitleFragment extends BaseFragment{
	private TextView text;
	@Override
	protected void init() {
		
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		
		View view = inflater.inflate(R.layout.fragment_title, null);
		text = (TextView) view.findViewById(R.id.title);
		return view;
	
	}

	@Override
	protected void setListenerAndAdapter() {
		// TODO Auto-generated method stub
		
	}
	public void changeTitle(int pos) {
		switch (pos) {
		case 0:
			text.setText("约起");
			break;
		case 1:
			text.setText("我");
			break;
		case 2:
			text.setText("发现");
			break;
		default:
			break;
		}
	}
}
