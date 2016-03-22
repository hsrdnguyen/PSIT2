package ch.avocado.share.model.data;

/**
 * Created by bergm on 19/03/2016.
 */
public enum AccessLevelEnum {
    NONE(0),
    READ(1),
    WRITE(3),
    OWNER(7);

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
        return (numericLevel >= other.numericLevel);
    }
}
