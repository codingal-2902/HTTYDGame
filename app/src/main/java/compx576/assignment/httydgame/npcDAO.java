package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// DAO for the NPC class/table
@Dao
public interface npcDAO {
    @Insert
    void insertCharacter(NPC character);

    // Get an NPC with a specified name
    @Query("SELECT * FROM characters WHERE name = :name")
    NPC findNPCByName(String name);

    // Update relationship with a specified character
    @Query("UPDATE characters SET relLevel = :updatedValue WHERE name = :characterName")
    void updateRelationship(String characterName, float updatedValue);

    @Query("SELECT * FROM characters")
    List<NPC> getAllNPCs();
}
