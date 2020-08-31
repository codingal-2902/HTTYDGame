package compx576.assignment.httydgame;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Choice implements Parcelable {
    private String choice1;
    private String choice2;
    private List<NPC> affectedChar;
    private long[] levelChange;
    private String[] nextFile;

    protected Choice(Parcel in) {
        choice1 = in.readString();
        choice2 = in.readString();
        affectedChar = in.createTypedArrayList(NPC.CREATOR);
        levelChange = in.createLongArray();
        nextFile = in.createStringArray();
    }

    public Choice(String choice1, String choice2, List<NPC> affectedChars, long[] levelChange, String[] nextFile) {
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.affectedChar = affectedChars;
        this.levelChange = levelChange;
        this.nextFile = nextFile;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public void setAffectedChar(List<NPC> affectedChar) {
        this.affectedChar = affectedChar;
    }

    public void setLevelChange(long[] levelChange) {
        this.levelChange = levelChange;
    }

    public void setNextFile(String[] nextFile) {
        this.nextFile = nextFile;
    }

    public String getChoice1() {
        return this.choice1;
    }

    public String getChoice2() {
        return this.choice2;
    }

    public List<NPC> getAffectedChar() {
        return this.affectedChar;
    }

    public long[] getLevelChange() {
        return this.levelChange;
    }

    public String[] getNextFile() {
        return this.nextFile;
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
        parcel.writeString(choice1);
        parcel.writeString(choice2);
        parcel.writeTypedList(affectedChar);
        parcel.writeLongArray(levelChange);
        parcel.writeStringArray(nextFile);
    }
}
