package controlNodeWork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import AttackPathGenerator.*;

public class setNewGraph {
	/**
     * 将韧性控制点集合应用到图中
     * @param G
     * @param I
     * @return
     */
    public Graph applyControlNode(Graph G, List<Node> I){
    	
    	for(Node i: I)
    	{
//    		if(!G.vertexes.containsKey(i.getID())) continue;
    		Vertex v = G.vertexes.get(i.getID());
    		for(Vertex j : v.getPreV())
    		{
    			j.rmNextNode(v);
    		}
    		
    		v.rmAllPre();
    	}
 
        return G;
    }
}
