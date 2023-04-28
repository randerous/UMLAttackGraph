package attackGraph.graph;

public class attackVertex {
    public String type;// surface , asset , none
    public String name;
    public String archType;
    public attackVertex(String type, String name, String archType) {
        this.type = type;
        this.name = name;
        this.archType = archType;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getArchType() {
        return archType;
    }
    public void setArchType(String archType) {
        this.archType = archType;
    }
 
}
