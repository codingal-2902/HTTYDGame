package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface npcDAO {
    @Insert
    void insertCharacter(NPC character);

    @Query("SELECT * FROM characters WHERE name = :name")
    NPC findNPCByName(String name);

    @Query("UPDATE characters SET relLevel = :updatedValue WHERE name = :characterName]")
    void updateRelationship(String characterName, int updatedValue);
}
