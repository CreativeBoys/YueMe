package com.yueme.fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
<<<<<<< HEAD
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
=======
>>>>>>> 14ac26406c8c9ca09178df64db2a0d6f1066e4dc
import android.widget.TextView;

import com.yueme.R;
import com.yueme.fragment.base.BaseFragment;

public class TitleFragment extends BaseFragment{
<<<<<<< HEAD
	private String[] menus = new String[] {"约自习","约运动","约回家","其他"};
	private ImageView iv_menu;
	private ListView lv_popup;
	PopupWindow popupWindow;
=======
	private TextView text;
>>>>>>> 14ac26406c8c9ca09178df64db2a0d6f1066e4dc
	@Override
	protected void init() {
		lv_popup = (ListView) View.inflate(getActivity(), R.layout.popup_menu_list, null);
		iv_menu = (ImageView) findViewById(R.id.iv_menu);
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.fragment_title, null);
		text = (TextView) view.findViewById(R.id.title);
		return view;
	}

	@Override
	protected void setListenerAndAdapter() {
		lv_popup.setAdapter(new ArrayAdapter<String>(getActivity(),  R.layout.popup_menu_item,R.id.tv_menu, menus));
		iv_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (popupWindow == null) {
					popupWindow = new PopupWindow(getActivity());
					popupWindow.setContentView(lv_popup);
					popupWindow.setHeight(130);
					popupWindow.setWidth(100);
//					popupWindow.showAtLocation(getView(), 0, 0, 0);
					popupWindow.showAsDropDown(iv_menu, 0, 0);
				} else if(popupWindow.isShowing()){
					popupWindow.dismiss();
				} else{
					popupWindow.showAsDropDown(iv_menu, 0, 0);
				}
			}
		});
	}
<<<<<<< HEAD
	

=======
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
>>>>>>> 14ac26406c8c9ca09178df64db2a0d6f1066e4dc
}
