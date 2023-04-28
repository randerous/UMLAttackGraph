package attackGraph.abac;

import java.util.ArrayList;
import java.util.List;

import attackGraph.attacker.attacker;
import attackGraph.user_specification.user_specification;

public class policy {
    public List<rule> rules;

    public List<rule> getRules() {
        return rules;
    }

    public void setRules(List<rule> rules) {
        this.rules = rules;
    }

    public policy() {
        this.rules = new ArrayList<rule>();
    }

    public boolean is_accessible(attacker attacker, String visitEntity)
    {
        rule r = findRule(visitEntity);
        if(r == null) return true;
        else{
            if(checkCondition(r, attacker)) return true;
        }
        return false;
    }

    public boolean checkCondition(rule r, attacker attacker)
    {
        int num = 0;
        int sum = r.attribute.size();
        for(user_specification u: r.attribute)
        {
            for(user_specification v: attacker.privileges)
            {
                if(u.role.equals(v.role))
                {
                    num++;
                }
            }
        }
        if(num == sum && r.isPermit) return true;
        return false;
    }

    public rule findRule(String entity)
    {
        for(rule r : rules)
        {
            for(String e: r.entities)
            {
                if(e.toLowerCase().equals(entity.toLowerCase()))
                {
                    return r;
                }
            }
        }
        return null;

    }


}
