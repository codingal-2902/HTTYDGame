package compx576.assignment.httydgame;

import android.os.Parcel;
import android.os.Parcelable;

public class Choice implements Parcelable {
    private String text;
    private NPC affectedChar;
    private int levelChange;

    protected Choice(Parcel in) {
        this.text = in.readString();
        this.affectedChar = in.readParcelable(NPC.class.getClassLoader());
        this.levelChange = in.readInt();
    }

    public String getText() {
        return this.text;
    }

    public int getLevelChange() {
        return this.levelChange;
    }

    public NPC getAffectedChar() {
        return this.affectedChar;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLevelChange(int levelChange) {
        this.levelChange = levelChange;
    }

    public void setAffectedChar(NPC affectedChar) {
        this.affectedChar = affectedChar;
    }

    public static final Creator<Choice> CREATOR = new Creator<Choice>() {
        @Override
        public Choice createFromParcel(Parcel in) {
            return new Choice(in);
        }

        @Override
        public Choice[] newArray(int size) {
            return new Choice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.text);
        parcel.writeParcelable(this.affectedChar, i);
        parcel.writeInt(this.levelChange);
    }
}
