package attackGraph.attacker;
 
import java.util.ArrayList;
import java.util.List;

import attackGraph.user_specification.user_specification;

public class attacker {
    public List<attack> attacks;
    public List<String> compromiseElements;
    public List<user_specification> privileges;
    public attacker(List<attack> attacks, List<String> compromiseElements, List<user_specification> privileges) {
        this.attacks = attacks;
        this.compromiseElements = compromiseElements;
        this.privileges = privileges;
    }
    public attacker() {
        this.attacks = new ArrayList<attack>();
        this.compromiseElements = new ArrayList<String>();
        this.privileges = new ArrayList<user_specification>();
    }
    public List<attack> getAttacks() {
        return attacks;
    }
    public void setAttacks(List<attack> attacks) {
        this.attacks = attacks;
    }
    public List<String> getCompromiseElements() {
        return compromiseElements;
    }
    public void setCompromiseElements(List<String> compromiseElements) {
        this.compromiseElements = compromiseElements;
    }
    public List<user_specification> getPrivileges() {
        return privileges;
    }
    public void setPrivileges(List<user_specification> privileges) {
        this.privileges = privileges;
    }

    
    
}
