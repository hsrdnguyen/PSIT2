package ch.avocado.share.controller;

import ch.avocado.share.model.data.AccessLevelEnum;
import ch.avocado.share.model.data.Members;
import ch.avocado.share.model.data.User;

/**
 * Created by coffeemakr on 21.04.16.
 */
public class EditMemberBean {
    private String getReadableLevelName(AccessLevelEnum level) {
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
                return "Besitzer";
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
