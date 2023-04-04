package adjustmentWork;

import java.util.ArrayList;
import java.util.List;

import AttackPathGenerator.AttackPath;
import AttackPathGenerator.Graph;
import AttackPathGenerator.Node;
import AttackPathGenerator.Vertex;
import EvaluationWork.connectAreaNum;
import controlNodeWork.setNewGraph;

public class DeleteOneControlNode {
	int DeleteSize;
	int one;
	
	/**
	 * 考虑了每条路径至少一个点，但是时间复杂度太高，应该是写法问题，或者用空间换时间
	 * @param G
	 * @param presentList
	 * @param paths
	 * @return
	 */
	public List<Node> delete(Graph G, List<Node> presentList, AttackPath paths){ //需要加入路径的概念
		DeleteSize = 0;
		one = 0;
		
		for(int i = 0; i < presentList.size(); i++) {

			//从G中删除该点
			Graph g = G;
			List<Node> list = new ArrayList();
			list.add(presentList.get(i));
			
			setNewGraph s = new setNewGraph();
			g = s.applyControlNode(g, list);
			
			//计算隔离域数量
			int num = 0;
			
			connectAreaNum c = new connectAreaNum();
			c.setConnectNum(g);
			num = c.getConnectNum();

			if(num > DeleteSize) {
				
				for(ArrayList<Vertex> v : paths.pathSet) { //每条路径
					int otherControlNodeNum = 0;
					for(Vertex n : v) { //路径上每个点
						if(n.getItself().getID().equals(presentList.get(i).getID())) { //包含该点
							for(Node node : presentList) {
								for(Vertex nn : v) {
									if(nn.getItself().getID().equals(node.getID())) { //路径是否包含其他点包含该点
										otherControlNodeNum++;
									}
								}
								
							}
						}
						
						
					}
					
					if(otherControlNodeNum > 1) one = i;
				}
				
				
				
//				one = i;
			}
		}
		
		presentList.remove(presentList.get(one));
		return presentList;
	}
	
	/**
	 * 没考虑每条路径至少有一个点
	 * @param G
	 * @param presentList
	 * @return
	 */
	public List<Node> delete(Graph G, List<Node> presentList, List<Node> basicList){ //需要加入路径的概念
		DeleteSize = 0;
		one = 0;
		
		for(int i = 0; i < presentList.size(); i++) {

			//从G中删除该点
			Graph g = G;
			List<Node> list = new ArrayList();
			list.add(presentList.get(i));
			
			setNewGraph s = new setNewGraph();
			g = s.applyControlNode(g, list);
			
			//计算隔离域数量
			int num = 0;
			
			connectAreaNum c = new connectAreaNum();
			c.setConnectNum(g);
			num = c.getConnectNum();

			if(!basicList.contains(presentList.get(one))) one = i;
		}
		
		presentList.remove(presentList.get(one));
		return presentList;
	}
}
