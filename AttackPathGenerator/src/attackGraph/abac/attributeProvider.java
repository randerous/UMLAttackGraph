package attackGraph.abac;

import java.util.ArrayList;
import java.util.List;

import attackGraph.user_specification.user_specification;

public class attributeProvider {
    public String name;
    public List<user_specification> user_specifications;
    public attributeProvider(String name ) {
        this.name = name;
        this.user_specifications = new ArrayList<user_specification>();

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<user_specification> getUser_specifications() {
        return user_specifications;
    }
    public void setUser_specifications(List<user_specification> user_specifications) {
        this.user_specifications = user_specifications;
    }
    
}
