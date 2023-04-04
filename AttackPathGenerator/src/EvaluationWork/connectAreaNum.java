package EvaluationWork;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Queue;

import java.util.Iterator;

import AttackPathGenerator.Graph;
import AttackPathGenerator.Vertex;

public class connectAreaNum {
	public int connectNum;
	ArrayList<areaNumTargetStructure>  mark;
	
	Set<String> visit;
	
	int parent[];
	
    public int getConnectNum() {
        return connectNum;
    }

    /**
     * 计算连通子图数量，即隔离域数量
     * @param G
     */
    public void setConnectNum (Graph G) {
        //标识数组
        int num = G.vertexes.size();
        
        parent = new int[num];
		for(int j = 0; j < num; j++)
			parent[j] = j;
		
		
		int inum = 0;
		Map<String, Integer> conId = new HashMap<String, Integer>();
		
		Map<Integer, String> Idcon = new HashMap<Integer, String> ();
		for(Map.Entry<String, Vertex> entry: G.vertexes.entrySet())
		{
//			System.out.printf("%s\n", entry.getValue().getName());
			Idcon.put(inum,entry.getValue().getItself().getID());
			conId.put(entry.getValue().getItself().getID(), inum++);
		}
		//System.out.println("");
		for(Map.Entry<String, Vertex> entry: G.vertexes.entrySet())
		{
//			System.out.printf("%s\n", entry.getValue().getName());
			Vertex source = entry.getValue();
			for(Vertex v: source.getNextV())
			{
				int pa = getParent( conId.get(source.getItself().getID()));
				int pb = getParent( conId.get(v.getItself().getID()));
				if(pa != pb)
				{
					parent[pb] = pa;
//					System.out.printf("merge %s %s\n", entry.getValue().getName(), v.getName() );
				}	
			}
		}
		
		Set<Integer> connectNums = new HashSet<Integer>();
		
		//刷新并查集，使集合数更新
		for(int i = 0; i < num; i++)
		{
			getParent(i);
		}
		
		//统计并查集中的集合数
		for(int i = 0; i < num; i++)
		{
//			getParent(i);
			connectNums.add(parent[i]);
//			System.out.printf("parent : %d\n", parent[i]);
		}
		//查看并查集中的元素
//		for(Integer i: connectNums)
//		{
//			//System.out.printf("%d\n", i);
//			for(int j = 0; j < num; j++)
//			{
//				if(parent[j] == i)
//				{
//					String id = Idcon.get(j);
//					String name = G.vertexes.get(id).getName();
//					//System.out.printf("%s\n", name);
//				}
//			}
//		}
		//System.out.printf("liantongyu : %d\n", connectNums.size());

        this.connectNum =  connectNums.size();
    }
    
	private int getParent(int index) {
		if(index == parent[index])
			return index;
		return parent[index] = getParent(parent[index]);
	}

    /**
     * 深度优先遍历、广度优先遍历的效果是一样的
     * 但是问题在于怎么索引
     * @param G
     * @param s id为I的节点
     */
    private void VisitNode (Graph G) {
    	
    	
    	Set<String> keySett = G.vertexes.keySet();
    	Iterator<String> itt =keySett.iterator();
    	
    	String s = new String();
    	
    	while(itt.hasNext()) {
    		String keyy = itt.next();
    		
    		if(!visit.contains(keyy)) {
    			s = keyy;
    			break;
    		}
    	}

    	Queue<String> queue = new ArrayDeque();
    	
    	queue.add(s);
    	
    	//广度优先遍历
    	while(!queue.isEmpty()) {
    		//标记为1，已经访问过
    		String present = queue.poll();
    		visit.add(present);
    		
    		for(Vertex v : G.vertexes.get(present).getNextV()) {
    			if(!visit.contains(v.getItself().getID())) {
    				queue.add(v.getItself().getID());
    			}
    		}	
    	}
    }
}
