package compx576.assignment.httydgame;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Specify the classes being used for tables in the database, and the version number
@Database(entities = {Dialogue.class, NPC.class, Achievement.class}, version = 1, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {
    public abstract DialogueDAO dialogueDAO();
    public abstract npcDAO npcDAO();
    public abstract AchievementDAO achievementDAO();
}

