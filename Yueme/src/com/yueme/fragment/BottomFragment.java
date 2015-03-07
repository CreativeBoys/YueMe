package com.yueme.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yueme.R;
import com.yueme.fragment.base.BaseFragment;
import com.yueme.interfaces.OnBottomClickListener;

public class BottomFragment extends BaseFragment {
	public RadioGroup rg_bottom;
	public OnBottomClickListener onBottomClickListener;
	
	public void setOnBottomClickListener(OnBottomClickListener onBottomClickListener) {
		this.onBottomClickListener = onBottomClickListener;
	}

	@Override
	protected void init() {
		rg_bottom = (RadioGroup) findViewById(R.id.rg_bottom);
		((RadioButton)rg_bottom.getChildAt(0)).setChecked(true);
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_bottom, null);
	}

	@Override
	protected void setListenerAndAdapter() {
		rg_bottom.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(onBottomClickListener!=null)
				onBottomClickListener.onBottomClick(checkedId);
			}
		});
	}

}
