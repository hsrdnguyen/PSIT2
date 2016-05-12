package ch.avocado.share.model.data;

/**
 * All possible access levels.
 */
public enum AccessLevelEnum {
    /**
     * No access at all.
     */
    NONE(0),
    /**
     * Reading access. No modifications allowed.
     */
    READ(1),
    /**
     * Modification of the objects content allowed but no
     * modification of the access and not allowed to delete the object.
     */
    WRITE(3),
    /**
     * Manage rights
     */
    MANAGE(4),
    /**
     * Ownership of the object.
     */
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
        if(other == null) throw new NullPointerException("other can't be null");
        return (numericLevel >= other.numericLevel);
    }
}
