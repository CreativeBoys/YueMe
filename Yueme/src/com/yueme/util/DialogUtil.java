package com.yueme.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yueme.MainActivity;
import com.yueme.PubRequireActivity;
import com.yueme.R;

public class DialogUtil {
	private static AlertDialog verticalDialog;
	private static boolean [] selectedItems;
	private static BaseAdapter adapter;

	public static void showConfirmDialog(Context context,String message, AlertDialog.OnClickListener listener) {
		AlertDialog.Builder builder = new Builder(context);
		AlertDialog dialog = builder.create();
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消" ,new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(message);
		dialog.setIcon(R.drawable.ic_launcher);
		dialog.show();
	}
	
	public static void showVerticalRadioDialog(OnClickListener positiveListener, OnClickListener negativeListener,String message,final Context context, final String...choices) {
		selectedItems = new boolean[choices.length];
		for(int i = 0; i < selectedItems.length; i++) {
			selectedItems[i]= false; 
		}
		AlertDialog.Builder builder = new Builder(context);
		View view = View.inflate(context, R.layout.dialog_radio_vertical, null);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		ListView lv_dialog = (ListView) view.findViewById(R.id.lv_dialog);
		adapter = new BaseAdapter() {
			
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				RadioButton r = (RadioButton) View.inflate(context, R.layout.lv_radio_dialog_item, null);
				r.setText(choices[position]);
				if(selectedItems[position]) r.setChecked(true);
				else r.setChecked(false);
				r.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						for(int i = 0; i < selectedItems.length; i++) {
							selectedItems[i]= false; 
						}
						selectedItems[position] = true;
						adapter.notifyDataSetChanged();					
						
					}
				});
				return r;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return position;
			}
			
			@Override
			public int getCount() {
				return choices.length;
			}
		};
		lv_dialog.setAdapter(adapter);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(negativeListener);
		btn_ok.setOnClickListener(positiveListener);
		TextView tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
		tv_dialog_title.setText(message);
		builder.setView(view);
		verticalDialog = builder.create();
		verticalDialog.setCancelable(false);
		verticalDialog.show();
	}
	
	public static void closeVerticalRadioDialog() {
		if(verticalDialog!=null) {
			verticalDialog.dismiss();
		}
	}
	
	public static void showChoosePubTypeDialog(final Context context) {
		AlertDialog.Builder builder = new Builder(context);
		View popView = LayoutInflater.from(context).inflate(
				R.layout.home_center_popwindow, null);
		Button yue_learningBtn = (Button) popView
				.findViewById(R.id.yue_learning);
		Button yue_homeBtn = (Button) popView.findViewById(R.id.yue_home);
		Button yue_moreBtn = (Button) popView.findViewById(R.id.yue_more);

		
		final AlertDialog dialog = builder.create();
		dialog.setView(popView, 0, 0, 0, 0);
		yue_learningBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						PubRequireActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("CLASSIFY", "约自习");
				intent.putExtras(bundle);
				((MainActivity)context).startActivityForResult(intent, 1);
				dialog.dismiss();
			}
		});

		yue_homeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						PubRequireActivity.class);
				intent.putExtra("CLASSIFY", "约回家");
				((MainActivity)context).startActivityForResult(intent, 1);
				dialog.dismiss();
			}
		});

		yue_moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						PubRequireActivity.class);
				intent.putExtra("CLASSIFY", "约其他");
				((MainActivity)context).startActivityForResult(intent, 1);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
