package compx576.assignment.httydgame;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dialogue")
public class Dialogue implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int position;

    private String text;
    private String charName;
    private int bgImage;

    @ColumnInfo(name = "isCurrentPage")
    private boolean isCurrentPage;
    private Choice whatIf;

    public Dialogue(String text, String charName, int bgImage, boolean isCurrentPage, Choice whatIf) {
        this.text = text;
        this.charName = charName;
        this.bgImage = bgImage;
        this.isCurrentPage = isCurrentPage;
        this.whatIf = whatIf;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Dialogue(Parcel in) {
        this.charName = in.readString();
        this.text = in.readString();
        this.bgImage = in.readInt();
        this.position = in.readInt();
        this.isCurrentPage = in.readBoolean();
    }

    public static final Creator<Dialogue> CREATOR = new Creator<Dialogue>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Dialogue createFromParcel(Parcel in) {
            return new Dialogue(in);
        }

        @Override
        public Dialogue[] newArray(int size) {
            return new Dialogue[size];
        }
    };

    public void setBgImage(int bgImage) {
        this.bgImage = bgImage;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public void setCurrentPage(boolean currentPage) {
        this.isCurrentPage = currentPage;
    }

    @NonNull
    public String getText() {
        return this.text;
    }

    @NonNull
    public String getCharName() {
        return this.charName;
    }

    public int getBgImage() {
        return this.bgImage;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isCurrentPage() {
        return this.isCurrentPage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.charName);
        dest.writeString(this.text);
        dest.writeInt(this.bgImage);
        dest.writeInt(this.position);
        dest.writeBoolean(this.isCurrentPage);
    }

    public Choice getWhatIf() {
        return whatIf;
    }

    public void setWhatIf(Choice whatIf) {
        this.whatIf = whatIf;
    }
}
