package attackGraph.connection;

import java.util.HashSet;
import java.util.Set;

public class node {
    public String name;
    public String archType;
    public String type;
    public Set<String> conSet;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public node(String name, String archType) {
        this.name = name;
        this.archType = archType;
        this.conSet = new HashSet<String>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<String> getConSet() {
        return conSet;
    }
    public void setConSet(Set<String> conSet) {
        this.conSet = conSet;
    }

}
