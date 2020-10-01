package compx576.assignment.httydgame;
import java.util.List;

public class Choice{
    private String choice1;
    private String choice2;
    private List<NPC> affectedChar;
    private long[] levelChange;
    private String[] nextFile;
    private boolean alreadySeen;

    public Choice(boolean alreadySeen, String choice1, String choice2, List<NPC> affectedChars, long[] levelChange, String[] nextFile) {
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.affectedChar = affectedChars;
        this.levelChange = levelChange;
        this.nextFile = nextFile;
        this.alreadySeen = alreadySeen;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public void setAffectedChar(List<NPC> affectedChar) {
        this.affectedChar = affectedChar;
    }

    public void setLevelChange(long[] levelChange) {
        this.levelChange = levelChange;
    }

    public void setNextFile(String[] nextFile) {
        this.nextFile = nextFile;
    }

    public void setAlreadySeen(boolean alreadySeen) {
        this.alreadySeen = alreadySeen;
    }

    public String getChoice1() {
        return this.choice1;
    }

    public String getChoice2() {
        return this.choice2;
    }

    public List<NPC> getAffectedChar() {
        return this.affectedChar;
    }

    public long[] getLevelChange() {
        return this.levelChange;
    }

    public String[] getNextFile() {
        return this.nextFile;
    }

    public boolean alreadyBeenSeen() {
        return this.alreadySeen;
    }
}
