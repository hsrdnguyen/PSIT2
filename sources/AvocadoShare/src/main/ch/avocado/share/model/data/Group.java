package ch.avocado.share.model.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public class Group extends AccessControlObjectBase implements Serializable {

    private String name;
    private List<String> memberIds;

    public Group(String id,
                 List<Category> categories,
                 Date creationDate,
                 float rating,
                 String ownerId,
                 String description,
                 String name,
                 List<String> memberIds) {
        super(id, categories, creationDate, rating, ownerId, description);
        if(name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
        if(memberIds == null) throw new IllegalArgumentException("member is null");
        this.memberIds = memberIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if(!this.name.equals(name)) {
            this.name = name;
            setDirty(true);
        }
    }

    public User[] getMemberIds() {
        User[] users = new User[memberIds.size()];
        return memberIds.toArray(users);
    }

    public void addMember(String memberId) {
        memberIds.add(memberId);
        setDirty(true);
    }

    public void removeMember(String memberId) {
        memberIds.remove(memberId);
        setDirty(true);
    }
}
