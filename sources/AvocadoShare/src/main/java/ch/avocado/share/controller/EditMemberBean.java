package ch.avocado.share.controller;

import ch.avocado.share.model.data.AccessLevelEnum;

/**
 * Bean to make editing of a bean easier.
 */
public class EditMemberBean {
    public static String getReadableLevelName(AccessLevelEnum level) {
        if(level == null) throw new IllegalArgumentException("level is null");
        switch (level) {
            case NONE:
                return "Kein Zugriffsrecht";
            case READ:
                return "Leserecht";
            case WRITE:
                return "Schreibrecht";
            case MANAGE:
                return "Verwaltungsrecht";
            case OWNER:
                return "Besitzrecht";
        }
        throw new RuntimeException("Unknwon level");
    }

    public String getLevelOptions(AccessLevelEnum currentLevel) {
        if(currentLevel == null) throw new IllegalArgumentException("currentLevel is null");
        StringBuilder result = new StringBuilder();
        if(currentLevel == AccessLevelEnum.OWNER) {
            return "<option>Besitzer</option>";
        }
        AccessLevelEnum[] levelsInOrder = new AccessLevelEnum[]{
                AccessLevelEnum.NONE,
                AccessLevelEnum.READ,
                AccessLevelEnum.WRITE,
                AccessLevelEnum.MANAGE
        };
        for(AccessLevelEnum printedLevel: levelsInOrder) {
            result.append("<option value=\"");
            result.append(printedLevel.name());
            if(printedLevel == currentLevel) {
                result.append("\" selected=\"selected");
            }
            result.append("\">");
            result.append(getReadableLevelName(printedLevel));
            result.append("</option>\n");
        }
        return result.toString();
    }
}
