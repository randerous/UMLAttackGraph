package Visualization;

import AttackPathGenerator.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class visualizeGraph {
	public static void dotGenerator(Graph g,List<Node> criticalNode,String filepath,String[] cmd) throws Exception {
		Map<String,Integer> vertexIndex=new HashMap<String,Integer>();
		int index=0;
		for (Map.Entry<String, Vertex> v : g.vertexes.entrySet()) {
			vertexIndex.put(v.getKey(),index++);
		}
	
//		File file=new File(filepath);
//		BufferedWriter wr=new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream(filepath),"UTF-8"));
		
		try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(filepath));
            wr.write("digraph test{\n");
    		for (Map.Entry<String, Vertex> v : g.vertexes.entrySet()) {
    			 String source=v.getKey();
    			 String sourceIndex=intToString(vertexIndex.get(source));
    			 Set<Vertex> nexts=g.vertexes.get(source).getNextV();
    			 
    			 source = v.getValue().getName();
    			 int type=v.getValue().getType();
    			 if(type==0)
    				 wr.append(sourceIndex+" [label=\""+source+"\",shape=\"doublecircle\"];\n");
    			 else if(type==2)
    				 wr.append(sourceIndex+" [label=\""+source+"\",shape=\"cylinder\"];\n");
    			 else
    				 wr.append(sourceIndex+" [label=\""+source+"\"];\n");
    				 
    			 for(Vertex each:nexts) {
    				 String target=each.getItself().getID();
    				 String targetIndex=intToString(vertexIndex.get(target));
    				 wr.append(sourceIndex+"->"+targetIndex+";\n");
    			 }           
    		 }
    		
    		
    		for(Node n:criticalNode) {
    			wr.append(intToString(vertexIndex.get(n.getID()))+" [color=\"red"+"\"];\n");
    		}
    		wr.write("}");		
    		wr.close();
            
    		executeCMD(cmd);
        } catch (IOException e) {
        }
		
		
			
	}

//	public static void draw(String inputDot) {
//		String command;
//		
//	}
	
	public static String intToString(int num) {
		StringBuilder res=new StringBuilder();
		if(num==0)
			return "0";
		while(num!=0) {
			int temp=num%10;
			res.append(temp);
			num=num/10;
		}
		return res.reverse().toString();
	}
	
	
	public static void executeCMD(String[] cmd) throws Exception{
		try {
			Process p=Runtime.getRuntime().exec(cmd);
			p.waitFor();
			p.destroy();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
