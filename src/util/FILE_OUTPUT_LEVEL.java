package util;

public enum FILE_OUTPUT_LEVEL {
    ALL(3),
    REASONABLE(2),
    MINIMAL(1),
    NONE(0);

    int level;

    FILE_OUTPUT_LEVEL(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
