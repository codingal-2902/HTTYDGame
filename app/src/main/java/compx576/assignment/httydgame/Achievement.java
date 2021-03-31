package compx576.assignment.httydgame;

import android.os.Parcel;
import android.os.Parcelable;

// Define the table name as 'achievements'
public class Achievement implements Parcelable {

    // Specify some columns in the table
    private int id;
    private String name;
    private String description;
    private String multiplier;
    private boolean isUnlocked;

    public static final Creator<Achievement> CREATOR = new Creator<Achievement>() {
        @Override
        public Achievement createFromParcel(Parcel in) {
            return new Achievement(in);
        }

        @Override
        public Achievement[] newArray(int size) {
            return new Achievement[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(multiplier);
        parcel.writeByte((byte) (isUnlocked ? 1 : 0));
    }

    // Class constructor
    public Achievement(int id, String name, String description, String multiplier, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.multiplier = multiplier;
        this.isUnlocked = isUnlocked;
    }

    protected Achievement(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        multiplier = in.readString();
        isUnlocked = in.readByte() != 0;
    }

    // Getters and setters for class attributes
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
    }

    public void setUnlocked(boolean unlocked) {
        this.isUnlocked = unlocked;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMultiplier() {
        return this.multiplier;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }
}
