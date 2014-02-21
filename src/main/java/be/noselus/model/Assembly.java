package be.noselus.model;

public class Assembly {

    public enum Level {
        DEPUTY_CHAMBER,
        SENAT,
        REGION,
        LOCAL,
        PROVINCE,
        EUROPE,
        FEDERAL,
        COMMUNITY
    }

    private final int id;
    private final String label;
    private final Level level;

    public Assembly(int id, String label, Level level) {
        this.id = id;
        this.label = label;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Level getLevel() {
        return level;
    }


}
