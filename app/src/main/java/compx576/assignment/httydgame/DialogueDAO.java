package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DialogueDAO {
    @Insert
    void insertNewPage(Dialogue page);

    @Query("SELECT * FROM dialogue WHERE pk = :position")
    Dialogue getPage(int position);

    @Query("SELECT * FROM dialogue")
    List<Dialogue> getAll();
}
