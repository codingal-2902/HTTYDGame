package compx576.assignment.httydgame;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface DialogueDAO {
    @Insert
    void insertNewPage(Dialogue page);

}
