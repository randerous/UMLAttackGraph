package attackGraph.abac;

import java.util.ArrayList;
import java.util.List;

import attackGraph.user_specification.user_specification;

public class rule {
    public List<user_specification> attribute;
    public List<String> entities;
    public boolean isPermit;
    public List<user_specification> getAttribute() {
        return attribute;
    }
    public void setAttribute(List<user_specification> attribute) {
        this.attribute = attribute;
    }
    public List<String> getEntities() {
        return entities;
    }
    public void setEntities(List<String> entities) {
        this.entities = entities;
    }
    public boolean isPermit() {
        return isPermit;
    }
    public void setPermit(boolean isPermit) {
        this.isPermit = isPermit;
    }
    public rule(boolean isPermit) {
        this.attribute = new ArrayList<user_specification>();
        this.entities = new ArrayList<String>();
        this.isPermit = isPermit;
    }

   

    
}
