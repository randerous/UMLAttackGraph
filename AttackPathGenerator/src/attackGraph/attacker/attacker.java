package attackGraph.attacker;
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import attackGraph.user_specification.user_specification;

public class attacker {
    public List<attack> attacks;
    public Set<String> compromiseElements;
    public Set<user_specification> privileges;
    
    public attacker() {
        this.attacks = new ArrayList<attack>();
        this.compromiseElements = new HashSet<String>();
        this.privileges = new HashSet<user_specification>();
    }
    public List<attack> getAttacks() {
        return attacks;
    }
    public void setAttacks(List<attack> attacks) {
        this.attacks = attacks;
    }
 
    

    
    
}
