package compx576.assignment.httydgame;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "characters")
public class NPC implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;
    @ColumnInfo(name = "name")
    private String charName;
    @ColumnInfo(name = "relLevel")
    private float relationship;
    @ColumnInfo(name = "isSpeaker")
    private boolean currentSpeaker;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected NPC(Parcel in) {
        pk = in.readInt();
        charName = in.readString();
        relationship = in.readFloat();
        currentSpeaker = in.readBoolean();
    }

    public NPC(String name, float initialRelLevel, boolean isSpeaker) {
        this.charName = name;
        this.relationship = initialRelLevel;
        this.currentSpeaker = isSpeaker;
    }

    public int getPk() {
        return this.pk;
    }

    public String getCharName() {
        return this.charName;
    }

    public float getRelationship() {
        return this.relationship;
    }

    public boolean isCurrentSpeaker() {
        return this.currentSpeaker;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public void setRelationship(float relationship) {
        this.relationship = relationship;
    }

    public void setCurrentSpeaker(boolean currentSpeaker) {
        this.currentSpeaker = currentSpeaker;
    }

    public static final Creator<NPC> CREATOR = new Creator<NPC>() {
        @Override
        public NPC createFromParcel(Parcel in) {
            return new NPC(in);
        }

        @Override
        public NPC[] newArray(int size) {
            return new NPC[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(pk);
        parcel.writeString(charName);
        parcel.writeFloat(relationship);
        parcel.writeBoolean(currentSpeaker);
    }
}
