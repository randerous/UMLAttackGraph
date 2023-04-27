package attackGraph.user_specification;

import java.util.ArrayList;
import java.util.List;

public class user_specification {
    public String role;
    public List<String> sources;

    public List<String> getSources() {
        return sources;
    }

    public void setSources() {
        this.sources = new ArrayList<String>();
    }

    public user_specification(String role ) {
        this.role = role;
        this.sources = new ArrayList<String>();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    
}
