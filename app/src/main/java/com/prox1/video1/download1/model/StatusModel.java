package com.prox1.video1.download1.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StatusModel implements Parcelable {
    public static final Creator<StatusModel> CREATOR = new Creator<StatusModel>() {
        /* class com.test.statussaver.model.StatusModel.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public StatusModel createFromParcel(Parcel parcel) {
            return new StatusModel(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public StatusModel[] newArray(int i) {
            return new StatusModel[i];
        }
    };
    private String filepath;
    public boolean selected = false;

    public int describeContents() {
        return 0;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public StatusModel(String str) {
        this.filepath = str;
    }

    protected StatusModel(Parcel parcel) {
        this.filepath = parcel.readString();
    }

    public String getFilePath() {
        return this.filepath;
    }

    public void setFilePath(String str) {
        this.filepath = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.filepath);
    }
}
