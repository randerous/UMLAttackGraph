package attackGraph.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;

public class AttackGraph {
    public List<attackEdge> edges;
    public List<attackVertex> vertexs;
    
    public List<attackEdge> getEdges() {
        return edges;
    }
    public void setEdges(List<attackEdge> edges) {
        this.edges = edges;
    }
    public List<attackVertex> getVertexs() {
        return vertexs;
    }
    public void setVertexs(List<attackVertex> vertexs) {
        this.vertexs = vertexs;
    }
    public AttackGraph() {
        this.edges = new ArrayList<attackEdge>();
        this.vertexs = new ArrayList<attackVertex>();
    }

     
    public  void drawGraph() throws Exception {

        String filepath = "a.dot";
//        "cmd", "/c"
//        String[] cmd= {"dot.exe"};
        String[] cmd= {"C:\\Program Files\\Graphviz\\bin\\dot.exe","-Tsvg",filepath,"-o","out.svg"};
		try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(filepath));
            wr.write("digraph test{\n");
            wr.write("nodesep=1\n");
            
            for(attackEdge edge: this.edges)
            {
                String source = edge.from.name;
                String dest = edge.to.name;
                wr.append("\"" + source+ "\"" +"->"+ "\"" + dest+ "\"" + "[label= \""+ edge.cause +"\"];\n");
            }
    		// for (Map.Entry<String, Vertex> v : g.vertexes.entrySet()) {
    		// 	 String source=v.getKey();
    		// 	 String sourceIndex=intToString(vertexIndex.get(source));
    		// 	 Set<Vertex> nexts=g.vertexes.get(source).getNextV();
    			 
    		// 	 source = v.getValue().getName();
    		// 	 int type=v.getValue().getType();
    		// 	//  if(type==0)
    		// 	// 	 wr.append(sourceIndex+" [label=\""+source+"\",shape=\"doublecircle\"];\n");
    		// 	//  else if(type==2)
    		// 	// 	 wr.append(sourceIndex+" [label=\""+source+"\",shape=\"cylinder\"];\n");
    		// 	//  else
    		// 	// 	 wr.append(sourceIndex+" [label=\""+source+"\"];\n");
    				 
    		// 	 for(Vertex each:nexts) {
    		// 		 String target=each.getItself().getID();
    		// 		 String targetIndex=intToString(vertexIndex.get(target));
    		// 		 wr.append(sourceIndex+"->"+targetIndex+";\n");
    		// 	 }           
    		//  }    		 
    		wr.write("}");		
    		wr.close();
            
    		executeCMD(cmd);
        } catch (IOException e) {
        }
		
		
			
	}


    public  void executeCMD(String[] cmd) throws Exception{
		try {
			Process p=Runtime.getRuntime().exec(cmd);
//			p.waitFor();
//			p.destroy(); 
			System.out.println("draw over" + p.waitFor());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}


