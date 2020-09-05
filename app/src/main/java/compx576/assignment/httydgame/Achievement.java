package compx576.assignment.httydgame;

public class Achievement {

    int id;
    String name;
    String description;
    boolean isUnlocked;

    public Achievement(int id, String name, String description, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isUnlocked = isUnlocked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnlocked(boolean unlocked) {
        this.isUnlocked = unlocked;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }
}
