package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AchievementDAO {
    @Query("SELECT * FROM achievements")
    List<Achievement> getAllAchievements();

    @Query("UPDATE achievements SET isUnlocked = 1 WHERE achievementName = :aName")
    void setUnlocked(String aName);

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    List<Achievement> getUnlockedAchievements();

    @Insert
    void insertAchievement(Achievement a);
}
