package attackGraph.graph;

public class attackVertex {
    public int type;// surface 1, asset 2;
    public String name;
    public String archType;

    public attackVertex(int t, String name, String archType)
    {
        this.type = t;
        this.name = name;
        this.archType = archType;
    }

}
