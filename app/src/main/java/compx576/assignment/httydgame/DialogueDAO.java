package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// DAO for the Dialogue class/table
@Dao
public interface DialogueDAO {
    @Insert
    void insertNewPage(Dialogue page);

    // Get a specific page
    @Query("SELECT * FROM dialogue WHERE pk = :position")
    Dialogue getPage(int position);

    // Get all pages
    @Query("SELECT * FROM dialogue")
    List<Dialogue> getAll();
}
