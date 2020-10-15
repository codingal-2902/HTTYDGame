package compx576.assignment.httydgame;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Define the table name as 'characters'
@Entity(tableName = "characters")
public class NPC {
    // Specify some columns
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;
    @ColumnInfo(name = "name")
    private String charName;
    @ColumnInfo(name = "relLevel")
    private float relationship;

    // Constructor for the NPC class
    public NPC(String charName, float relationship) {
        this.charName = charName;
        this.relationship = relationship;
    }

    // Getters and setters for the class attributes
    public int getPk() {
        return this.pk;
    }

    public String getCharName() {
        return this.charName;
    }

    public float getRelationship() {
        return this.relationship;
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
}
