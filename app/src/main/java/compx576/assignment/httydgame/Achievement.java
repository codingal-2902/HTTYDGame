package compx576.assignment.httydgame;

public class Achievement {

    private int id;
    private String name;
    private String description;
    private String multiplier;
    private boolean isUnlocked;

    public Achievement(int id, String name, String description, String multiplier, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.multiplier = multiplier;
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

    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
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

    public String getMultiplier() {
        return this.multiplier;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }
}
