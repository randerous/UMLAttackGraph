package attackGraph.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

public class AttackGraph<attackEdge> {
    public attackVertex start;
    public attackVertex end;
    public List<attackVertex> vertexs;

    public Map<attackEdge, List<String>>  causes;
    
    public AttackGraph()
    {
        vertexs = new ArrayList<attackVertex>(); 
    }
    public void setStart(attackVertex vertex)
    {
        this.start = vertex;
    }
    public void setEnd(attackVertex vertex)
    {
        this.end = vertex;
    }
}


