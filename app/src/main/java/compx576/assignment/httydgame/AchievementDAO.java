package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// DAO for Achievement class/table
@Dao
public interface AchievementDAO {
    // Return all achievements
    @Query("SELECT * FROM achievements")
    List<Achievement> getAllAchievements();

    // Set an achievement as unlocked
    @Query("UPDATE achievements SET isUnlocked = 1 WHERE achievementName = :aName")
    void setUnlocked(String aName);

    // Return an achievement object based on its name
    @Query("SELECT * FROM achievements WHERE achievementName = :text")
    Achievement getAchievementByName(String text);

    @Insert
    void insertAchievement(Achievement a);
}
