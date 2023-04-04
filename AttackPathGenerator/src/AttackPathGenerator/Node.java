package AttackPathGenerator;

import java.util.ArrayList;

public class Node {
	String id;
    String name;
    int level;// arch level
    ArrayList<Vulnerability> vulnerabilities;
    
    public Node(Node n)
    {
    	this.id = n.id;
    	this.name = n.name;
    	this.level = n.level;
    	this.vulnerabilities = new ArrayList<Vulnerability>(n.getVulnerabilities());
    }
    public String getID()
    {
    	return this.id;
    }
    
    public String getName()
    {
    	return this.name;
    }
    
    public int getLevel()
    {
    	return this.level;
    }
    
    public  ArrayList<Vulnerability> getVulnerabilities()
    {
    	return this.vulnerabilities;
    }
    
    public Node(String id, String name, int level)
    {
    	this.id = id;
    	this.name = name;
    	this.level = level;
    	vulnerabilities = new ArrayList<Vulnerability>();
    }
    public void  addVunlerability(Vulnerability v) {
    	this.vulnerabilities.add(v);
    }
    
    public void showNodeInfo() {
    	System.out.printf("Node:\nid: %s\nname : %s\nlevel: %d\n\r", id, name, level);
    }
    
    public void showDetailNodeInfo() {
    	System.out.printf("Node:\nid: %s\nname : %s\nlevel: %d\n\r", id, name, level);
    	if(vulnerabilities.size() > 0) {
    		for(Vulnerability v: vulnerabilities) {
    			v.showInfo();
    		}
    	}
    }
    
}


