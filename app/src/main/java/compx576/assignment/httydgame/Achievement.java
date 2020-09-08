package compx576.assignment.httydgame;

public class Achievement {

    private int id;
    private String name;
    private String description;
    private int multiplier;
    private String functionString;
    private boolean isUnlocked;

    public Achievement(int id, String name, String description, int multiplier, String callFunction, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.multiplier = multiplier;
        this.functionString = callFunction;
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

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public void setFunctionString(String functionString) {
        this.functionString = functionString;
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

    public int getMultiplier() {
        return this.multiplier;
    }

    public String getFunctionString() {
        return this.functionString;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }
}
