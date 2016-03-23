package ch.avocado.share.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public class Group extends AccessControlObjectBase implements Serializable {

    private String name;
    private List<User> members;

    public Group(String id,
                 List<Category> categories,
                 Date creationDate,
                 float rating,
                 String ownerId,
                 String description,
                 String name,
                 List<User> members) {
        super(id, categories, creationDate, rating, ownerId, description);
        if(name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
        if(members == null) throw new IllegalArgumentException("member is null");
        this.members = members;
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

    public User[] getMembers() {
        User[] users = new User[members.size()];
        return members.toArray(users);
    }

    public void addMember(User member) {
        members.add(member);
        setDirty(true);
    }

    public void removeMember(User removedMember) {
        members.remove(removedMember);
        setDirty(true);
    }
}
