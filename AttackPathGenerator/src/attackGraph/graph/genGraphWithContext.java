package attackGraph.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import attackGraph.contextBuilder;
import attackGraph.abac.attributeProvider;
import attackGraph.abac.attributeProviderSet;
import attackGraph.abac.policy;
import attackGraph.connection.G;
import attackGraph.connection.node;
import attackGraph.user_specification.user_specification;
import attackGraph.vulnerability.vulnerability;
import attackGraph.vulnerability.vulnerablityCollection;

public class genGraphWithContext {
    public AttackGraph g;
    public contextBuilder context;
    public genGraphWithContext( contextBuilder context) {
        this.g = new AttackGraph();
        this.context = context;
    }


    public String findSurface()
    {
    	for(String key: this.context.conBuilder.g.nSet.keySet())
    	{
    		if(this.context.conBuilder.g.nSet.get(key).type.equals("surface"))
    			return key;
    	}
        return null;
    }
    public void gen()
    {
        boolean changed = true;
        G connected = this.context.conBuilder.g;
        String surface = findSurface();
        attackVertex v = new attackVertex(connected.nSet.get(surface).type, surface, connected.nSet.get(surface).archType);
        this.g.vertexs.add(v);
        
        HashSet<String> newCompromiseElems = new HashSet<String>( this.context.attacker.compromiseElements);
        newCompromiseElems.add(surface);
        // this.context.attacker.compromiseElements.add(surface);
        while(changed)
        {
            changed = false;
            attributeProviderSet aSet = this.context.attributeProviderSet;
            for(String str : newCompromiseElems)
            {
                for(attributeProvider aProvider: aSet.providers)
                {
                    if(str.toLowerCase().equals( aProvider.name.toLowerCase()))
                    {
                        for(user_specification u: aProvider.user_specifications)
                        {
                            boolean spec_exist = false;
                            ArrayList<user_specification> tmp = new  ArrayList<user_specification>(this.context.attacker.privileges);
                            
                            for(user_specification user: this.context.attacker.privileges)
                            {
                                if(user.role.equals(u.role)){
                                    spec_exist = true; 
                                    tmp.remove(user);
                                    user.sources.add(str);
                                    tmp.add(user);
                                }
                                this.context.attacker.privileges = tmp;
                            }
                            if(!spec_exist)
                            {
                                u.sources.add(str);
                                this.context.attacker.privileges.add(u);
                            }
                            
                        }
                        changed = true;
                    }
                }
            }
            this.context.attacker.compromiseElements.addAll(newCompromiseElems);
            newCompromiseElems.clear();

            HashSet<String> connectElems = new HashSet<String>();
            getConnectElems(connectElems, this.context.attacker.compromiseElements); 

            
            for(String str: connectElems)
            {
                attackUsingVulnerability(str,newCompromiseElems);                 
            }
            
            for(String str: connectElems)
            {
            	if(newCompromiseElems.contains(str)) continue;
                attackUsingSpecification(str,newCompromiseElems);
            }
            

            if(newCompromiseElems.size() > 0) changed = true;
        }

    }
    public void getConnectElems(HashSet<String> connectElems, Set<String> elems)
    {
        G collection = this.context.conBuilder.g; 
        for(String str : elems)
        {
            for(String s: collection.nSet.get(str.toLowerCase()).conSet)
            {
                if(!elems.contains(s))
                    connectElems.add(s);
            }
        }
    }

    public void attackUsingSpecification(String s, Set<String> newCompromiseElems)
    {
        G collection = this.context.conBuilder.g; 
        policy pSet = this.context.policyset;
        if(pSet.is_accessible(this.context.attacker, s))
        {
            attackVertex dest = new attackVertex(collection.nSet.get(s).type, s, collection.nSet.get(s).archType);

            // attackVertex source = null;
            String cause = ""; 
            Set<String> jSet = new HashSet<String>( this.context.attacker.compromiseElements);
            if(pSet.findRule(s) != null)
            {
                for(user_specification attr: pSet.findRule(s).attribute)
                {
                    jSet.retainAll(attr.sources);
                    cause += attr.role + " ";
                }
            }
            if(pSet.findRule(s) != null && !jSet.isEmpty())
            {
                for(String str: jSet)
                {
                    for(attackVertex v: this.g.vertexs)
                    {
                        if(v.name.equals(str))
                        {
                            attackEdge edge = new attackEdge(v, dest, cause);
                            this.g.edges.add(edge);
                        }
                    }
                }
            }else
            {
                for(String str: this.context.attacker.compromiseElements)
                {
                    if(collection.nSet.get(str.toLowerCase()).conSet.contains(s))
                    {
                        for(attackVertex v: this.g.vertexs)
                        {
                            if(v.name.equals(str))
                            {
                                attackEdge edge = new attackEdge(v, dest, cause);
                                this.g.edges.add(edge);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            this.g.vertexs.add(dest);
            newCompromiseElems.add(s);

        }
    }

    public void attackUsingVulnerability(String s, Set<String> newCompromiseElems)
    {
        vulnerablityCollection vulSet = this.context.vulSet;
        if(!vulSet.vulSet.containsKey(s))
        {
            return;
        }

        //check exploit condition
        
        //check require elem
        List<vulnerability> vlist = vulSet.vulSet.get(s);
        String cause = "";
        for(vulnerability vul : vlist)
        {
            boolean canUsed = true;
            for(String elem: vul.RequiredElements)
            {
                if(!this.context.attacker.compromiseElements.contains(elem.toLowerCase()))
                {
                    canUsed = false;
                    break;
                }
            }

            for(user_specification u: vul.RequiredPrivileges)
            {
                boolean hasSpec = false;
                for(user_specification spec: this.context.attacker.privileges)
                {
                    if(u.role.equals(spec.role))
                    {
                        hasSpec = true;
                        break;
                    }
                }

                if(!hasSpec)
                {
                    canUsed = false;
                    break;
                }
            }

            if(canUsed)
            {
                cause += vul.CVEID;
            }
            this.context.attacker.privileges.addAll(vul.GainedPrivileges);            
        }
        if(cause.equals(""))return;
        else
        {
            G collection = this.context.conBuilder.g; 
            attackVertex dest = new attackVertex(collection.nSet.get(s).type, s, collection.nSet.get(s).archType);
            this.g.vertexs.add(dest);
            for(String str: this.context.attacker.compromiseElements)
                {
                    if(collection.nSet.get(str.toLowerCase()).conSet.contains(s))
                    {
                        for(attackVertex v: this.g.vertexs)
                        {
                            if(v.name.equals(str.toLowerCase()))
                            {
                                attackEdge edge = new attackEdge(v, dest, cause);
                                this.g.edges.add(edge);
                                break;
                            }
                        }
                        break;
                    }
                }
            newCompromiseElems.add(s);
        }


        

    }

    public void drawGraph() throws Exception
    {
        this.g.drawGraph();
    }

    
    

    
}
