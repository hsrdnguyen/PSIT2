package ch.avocado.share.model.data;

/**
 * All possible access levels.
 */
public enum AccessLevelEnum {
    NONE(0),
    READ(1),
    WRITE(3),
    OWNER(7);

    /**
     * The numeric level is only used to check if one level contains another.
     * Each level contains all levels with a lower numeric level.
     */
    private final int numericLevel;

    AccessLevelEnum(int numericLevel){
        this.numericLevel = numericLevel;
    }

    /**
     * Check if this level also allows includes the other level.
     * @param other The other level which should be included in this level.
     * @return True if this level contains the other. Otherwise false is returned.
     */
    public boolean containsLevel(AccessLevelEnum other) {
        if(other == null) {
            throw new IllegalArgumentException("other can't be null");
        }
        return (numericLevel >= other.numericLevel);
    }
}
