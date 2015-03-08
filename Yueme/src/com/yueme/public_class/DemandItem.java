package com.yueme.public_class;

import android.os.Parcel;
import android.os.Parcelable;

public class DemandItem implements Parcelable {

	public int icon_id;
	public String userName;
	public String classify;
	public String time;
	public String demandContent;
	public String restTime;

	public DemandItem(int icon_id, String userName, String classify, String time,
			String demandContent, String restTime) {
		this.icon_id = icon_id;
		this.userName = userName;
		this.classify = classify;
		this.time = time;
		this.demandContent = demandContent;
		this.restTime = restTime;
		
	}

	public DemandItem(){}
	//数据恢复
	private DemandItem(Parcel in) {
		icon_id = in.readInt();
		userName = in.readString();
		time = in.readString();
		demandContent = in.readString();
		restTime = in.readString();
	}

	public static final Parcelable.Creator<DemandItem> CREATOR = new Creator<DemandItem>() {

		@Override
		public DemandItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new DemandItem(source);
		}

		@Override
		public DemandItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new DemandItem[size];
		}

	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(icon_id);
		parcel.writeString(userName);
		parcel.writeString(time);
		parcel.writeString(demandContent);
		parcel.writeString(restTime);
	}

}