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
public class Dialogue {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int position;

    private String text;
    private String charName;
    private int bgImage;

    @ColumnInfo(name = "isCurrentPage")
    private boolean isCurrentPage;
    private String whatIf;
    private String hasAchievement;

    public Dialogue(String text, String charName, int bgImage, boolean isCurrentPage, String whatIf, String hasAchievement) {
        this.text = text;
        this.charName = charName;
        this.bgImage = bgImage;
        this.isCurrentPage = isCurrentPage;
        this.whatIf = whatIf;
        this.hasAchievement = hasAchievement;
    }

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

    public void setWhatIf(String whatIf) {
        this.whatIf = whatIf;
    }

    public void setAchievement(String hasAchievement) {
        this.hasAchievement = hasAchievement;
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

    public String getWhatIf() {
        return this.whatIf;
    }

    public String hasAchievement() {
        return this.hasAchievement;
    }
}
