package EvaluationWork;

import java.util.Map;

import AttackPathGenerator.Graph;
import AttackPathGenerator.Vertex;

public class surfaceNum {
	int surfaceNumber;

    public int getSurfaceNumber() {
        return surfaceNumber;
    }

    public void countInitialSurfaceNum(Graph G){
        surfaceNumber = 0;
        for(Map.Entry<String, Vertex> v : G.vertexes.entrySet())
        {
            if(v.getValue().getType() == 0) surfaceNumber++;
        }
    }
}
