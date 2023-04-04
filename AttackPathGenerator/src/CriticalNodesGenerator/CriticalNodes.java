package CriticalNodesGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import AttackPathGenerator.*;

public class CriticalNodes {
	    Graph G;
	    int n;
	    String[] vertexes;//锟斤拷鸥锟斤拷诘锟絠d
	    LinkedList<Vertex> Vertexes=new LinkedList();//锟斤拷选锟斤拷慕诘慵拷锟斤拷锟饺ワ拷锟铰讹拷锟斤拷锟绞诧拷

	    public CriticalNodes(Graph g) {
	        this.G = g;
	        this.n = this.G.vertexes.size();
	        this.vertexes = new String[n];


	        int i = 0;
	        for (Map.Entry<String, Vertex> v : G.vertexes.entrySet()) {
	            this.vertexes[i++] = new String(v.getKey());
//	            if(v.getValue().getType()==1)//锟斤拷锟斤拷锟斤拷锟节碉拷锟斤拷锟斤拷选锟斤拷诘慵�
	            	Vertexes.add(v.getValue());
	        }
	    }

	    public List<Node> getCriticalNodes(){
	    	/*
	    	 * 锟斤拷锟斤拷图锟斤拷一锟斤拷锟斤拷通图
	    	 * @return锟斤拷锟斤拷锟酵佳★拷目锟斤拷频锟�
	    	 */
	        List<Node> res=new ArrayList();
	        LinkedList<String> G0=new LinkedList();

	        for(Vertex v:Vertexes) {
	        	G0.add(v.getItself().getID());
	        }
	        String minDegreeNode=G0.get(0);
	        Graph newG = new Graph(G);
	        Vertex minV=newG.vertexes.get(minDegreeNode);
	        int minD=minV.getNextV().size()+minV.getPreV().size();

	        while(!G0.isEmpty()){
	            //锟斤拷G0锟叫讹拷锟斤拷小锟侥节碉拷
	        	minD = Integer.MAX_VALUE;
	            for(String nodeId:G0){
	            	Vertex v=newG.vertexes.get(nodeId);
	            	int D=v.getNextV().size()+v.getPreV().size();
	                if(D<minD){
	                    minDegreeNode=nodeId;
	                    minV=v;
	                    minD=D;
	                }
	            }
	            
	            if(minD==0) {
//	            	res.add(minV.getItself());
	            	Vertexes.remove(minV);
	            	G0.remove(minV.getItself().getID());
	            	newG.rmNode(minV);
//	            	System.out.println(G0.size());
	            	continue;
	            }

	            //锟斤拷锟窖★拷锟絤inDegreeNode锟斤拷一锟斤拷锟节接节点，锟斤拷锟斤拷res
	            int rand=(int)Math.random()*minD;
//	            int i=0;
	            List<Vertex> adjVertexes=new ArrayList<Vertex>(minV.getPreV());
	            adjVertexes.addAll(minV.getNextV());
	            Node controlledNode=adjVertexes.get(rand).getItself();
//	            Node controlledNode=G.vertexes.get(minDegreeNode).getNext_vertexes().get(rand);
//	            Node controlledNode=null;
//	            for(Vertex v:G.vertexes.get(minDegreeNode).getNextV()) {
//	            	if(i==rand) controlledNode=v.getItself();
//	            	i++;
//	            }
	            res.add(controlledNode);
	            G0.remove(controlledNode.getID());
	            newG.rmNode(newG.getVertexes().get(controlledNode.getID()));

	            //删去锟斤拷minDegreeNode锟斤拷锟斤拷锟侥节碉拷
	            for(Vertex v: adjVertexes){
	                        	
	            	if(G0.contains(v.getItself().getID()))
	            	{
	            		G0.remove(v.getItself().getID());
	            		newG.rmNode(v);
	            	}
	
	            		
	            }
//	            System.out.println("df+"+newG.getVertexes().size());
//            	System.out.println("df+"+G0.size()+G0.isEmpty());
	        }
	        return res;
	    }

	    public List<Node> GetTwoNodesInOnePath(ArrayList<ArrayList<Vertex>> pathset,int num){
	    	/*
	    	 * @Vertexes:锟斤拷选锟斤拷慕诘锟�
	    	 * @pathSet:路锟斤拷锟斤拷锟斤拷每锟斤拷路锟斤拷锟侥憋拷识为index锟斤拷锟斤拷
	    	 * @return:锟斤拷锟狡点集
	    	 */
	        List<Integer> pathState=new ArrayList();//pathState[i]==2锟斤拷锟斤拷锟斤拷为i锟斤拷路锟斤拷锟斤拷前锟斤拷状态为2锟斤拷锟斤拷锟斤拷锟节革拷路锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷锟斤拷锟节碉拷锟斤拷
	        for(int i=0;i<pathset.size();i++)
	        	pathState.add(0);
	        ArrayList<Node> res=new ArrayList();
	        int count=pathset.size();//路锟斤拷锟斤拷

	        for(Vertex v:Vertexes) {
	        	v.InitPaths(pathset);
	        }

	        while(count!=0){
	            Vertex p=Vertexes.getFirst();
	            for(Vertex q:Vertexes){
	                if(q.pathNums>p.pathNums) p=q;
	            }
	            res.add(p.getItself());
	            ArrayList<Integer> myPaths=p.getPaths();
	            for(int each:myPaths){
	                int k=pathState.get(each);
	                if(k==num) continue;
	                k++;
	                pathState.set(each,k); 
	                if(k==num) {
	                   count--;
	                   for(Vertex v:pathset.get(each)){
	                       v.pathNums--;
	                   }
	                }
	            }
	            Vertexes.remove(p);
	        }
	        return res;

	    }	    

	    public List<Node> GetTwoNodesInOnePath2(ArrayList<ArrayList<Vertex>> pathset,List<Node> PriCriticalNode,int num){
	    	/*
	    	 * 锟斤拷锟姐法CriticalNodes锟侥伙拷锟斤拷锟斤拷锟斤拷
	    	 * @Vertexes:锟斤拷选锟斤拷慕诘锟�
	    	 * @pathSet:路锟斤拷锟斤拷锟斤拷每锟斤拷路锟斤拷锟侥憋拷识为index锟斤拷锟斤拷
	    	 * @return:锟斤拷锟狡点集
	    	 */
	    	List<Node> res=new ArrayList<Node>();
	    	List<Integer> pathState=new ArrayList();//pathState[i]==2锟斤拷锟斤拷锟斤拷为i锟斤拷路锟斤拷锟斤拷前锟斤拷状态为2锟斤拷锟斤拷锟斤拷锟节革拷路锟斤拷锟斤拷选锟斤拷锟斤拷锟斤拷锟斤拷锟节碉拷锟斤拷
	        for(int i=0;i<pathset.size();i++)
	        	pathState.add(0);
	        int count=pathset.size();//未锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷路锟斤拷锟斤拷

	        for(Vertex v:Vertexes) {
	        	v.InitPaths(pathset);
	        }
	    	
	        for(Node n:PriCriticalNode) {
	        	Vertex v=G.vertexes.get(n.getID());
	        	Vertexes.remove(v);
	        	for(int path:v.getPaths()) {
	        		int k=pathState.get(path);
	        		if(k==num) continue;
	        		else {
	        			k++;
	        			pathState.set(path, k);
	        			if(k==num) {
	        				count--;
	        				for(Vertex vv:pathset.get(path)){
	 	                       vv.pathNums--;
	 	                   }
	        			}
	        		}
	        	}        	
	        }
	        
	        if(count==0)//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	        	return res;
	        else {
	        	while(count!=0) {
	        		Vertex p=Vertexes.getFirst();
		            for(Vertex q:Vertexes){
		                if(q.pathNums>p.pathNums) p=q;
		            }
		            res.add(p.getItself());
		            ArrayList<Integer> myPaths=p.getPaths();
		            for(int each:myPaths){
		                int k=pathState.get(each);
		                if(k==num) continue;
		                k++;
		                pathState.set(each,k); 
		                if(k==num) {
		                   count--;
		                   for(Vertex v:pathset.get(each)){
		                       v.pathNums--;
		                   }
		                }
		            }
		            Vertexes.remove(p);
	        	}
	        }
	        
	    	return res;
	    	
	    }
}
