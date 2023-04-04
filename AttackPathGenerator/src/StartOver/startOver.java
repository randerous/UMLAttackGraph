package StartOver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import AttackPathGenerator.AttackPath;
import AttackPathGenerator.Graph;
import AttackPathGenerator.Node;
import AttackPathGenerator.simplifier;
import AttackPathGenerator.umlParser;
import CriticalNodesGenerator.CriticalNodes;
import EvaluationWork.connectAreaNum;
import EvaluationWork.totalEvaluation;
import UMLgenerator.UMLgenerator;
import Visualization.visualizeGraph;
import adjustmentWork.DeleteOneControlNode;
import controlNodeWork.setNewGraph;

public class startOver {
	
	public static void main(String[] args) throws Exception {
		
		Graph G = new Graph(); //存储经过处理的图
		
		AttackPath paths = new AttackPath();
		List<Node> criticalNode = new ArrayList(); //存放所有的韧性控制点
		List<Node> basicNode = new ArrayList(); //存放在化简过程（第一阶段）里选出的韧性控制点
		int Max = 1000000; //可以接受的最大路径数量
		
		boolean AttackPathTarget = true; //从G生成攻击路径数量Num小于1w，则AttackPathTarget=false.
		int N = 2;
//		System.out.println("请输入每条依赖路径上需要的韧性控制点数量：");
//		Scanner in = new Scanner(System.in);
//    	int num = in.nextInt();
		
		
		//国涛算法1：生成连通图G
		//样例生成
		UMLgenerator.generator(100, 2);
		
		umlParser umlParser = new umlParser();
		simplifier  simplifier = new simplifier();
		
		G = umlParser.genGraph("Example_UML.uml");	
		
		G = simplifier.simplify(G); //图G是可能会变化的
		
		Graph handledGraph = new Graph(G); //存储抽取出来的最原始图,图handledGraph始终不会变化
		
		//雅妮算法1：从图G提取N个韧性控制点
		CriticalNodes cNode =new CriticalNodes(G);
		List<Node> NodeS = cNode.getCriticalNodes();
		
		int t = 0;
		while(AttackPathTarget && NodeS.size() > 1) {
			//国涛算法2：从G生成攻击路径数量
			paths.genPath(G, Max); 
			paths.showInfo();
				
			if(paths.pathSet.size() <= Max) {
				AttackPathTarget = false;
				paths.writeIntoFile();
			}	

			if(AttackPathTarget) {	
				//将这N个韧性控制点加入List
				if(NodeS.size() > 1)
				{
					criticalNode.add(NodeS.get(0));
					criticalNode.add(NodeS.get(1));
					basicNode.add(NodeS.get(0));
					basicNode.add(NodeS.get(1));
					NodeS.remove(0);
					NodeS.remove(0);
					System.out.println("critical node selected: "+NodeS.get(0).getName());
					System.out.println("critical node selected: "+NodeS.get(1).getName());
				}
				
				setNewGraph s = new setNewGraph();
				//从图G中删去这N个韧性控制点的入度连接：adjustment
				G = s.applyControlNode(G, criticalNode);
				
				t++;
			}
		}
		
		//将剩余韧性控制点集合加入list
		//雅妮算法2：从G和攻击路径paths选择剩余韧性控制点集合
//		CriticalNodes cNodes=new CriticalNodes(G);
//		List<Node> criticalNodeS = cNodes.GetTwoNodesInOnePath(paths.pathSet);
		List<Node> criticalNodeS = cNode.GetTwoNodesInOnePath2(paths.pathSet,criticalNode,1);
		
		for(Node n : criticalNodeS) {  
			criticalNode.add(n);
		}
		
		System.out.print("\n一共简化了" + t + "次，生成了" + 2*t + "个初始韧性控制点，");
		System.out.print("后续韧性控制点数量为"+criticalNodeS.size() + "个，共" + criticalNode.size() + "个韧性控制点\n");
		System.out.println("生成的韧性控制点如下：");
		
		for(Node node:criticalNode)
//			System.out.println(node.getID());
			System.out.println(node.getName());
		System.out.println();
//		visualize graph
		String inputDot="D://input.dot";
//		visualizeGraph.dotGenerator(G,criticalNode,inputDot);
		String[] cmd= {"dot","-Tsvg",inputDot,"-o","out.svg"};
		visualizeGraph.dotGenerator(G,criticalNode,inputDot,cmd);
		//评估
		
		totalEvaluation evaluator = new totalEvaluation();
		evaluator.showEvaluationResult(handledGraph, paths, criticalNode);
		
		//接收选择意见
        System.out.println("您是否满意当前隔离域数量？如满意请输入y，否则请输入n");
        char choice = ' ';
        
        choice = (char)System.in.read();

        //优化处理
        if (choice == 'y') { //接受
        	for(int i = 0; i < criticalNode.size(); i++) {
//        		System.out.println(criticalNode.get(i).getID());
        		System.out.println(criticalNode.get(i).getName());
        	}
        	
        	System.out.println("Over!");
        }
        else { //不接受
            System.out.println("请输入隔离域数量上限");
        	int areaNum = 0; //隔离域数量上限
        	
        	Scanner in = new Scanner(System.in);
        	areaNum = in.nextInt();
        	  
            //删减韧性点，判断连通域数量
            connectAreaNum con = new connectAreaNum();
            setNewGraph set = new setNewGraph();
            DeleteOneControlNode oneNode = new DeleteOneControlNode();

        	boolean target = true;
            while (target) {
                /**
                 * 假定最佳韧性控制点数量m>最少数量n（实际上也是>=）
                 * 从m开始，算隔离域数量，所有m的都不行，就m-1，直到n
                 */
        		Graph basicGraph = new Graph(handledGraph);
            	
            	basicGraph = set.applyControlNode(basicGraph, criticalNode); //将新的控制点集合应用到图上
            	con.setConnectNum(basicGraph);
//            	System.out.print(con.getConnectNum()); 
            	
        		con.setConnectNum(basicGraph); //计算图的隔离域数量
            	int numsize = con.getConnectNum(); //获取隔离域数量
        		
        		if(numsize <= areaNum) { //隔离域数量小于areaNum
            		target = false;
            		System.out.print("以下控制点集合达到要求：\n");
            		for(int i = 0; i < criticalNode.size(); i++) {
                		System.out.println(criticalNode.get(i).getID());
                	}
                	
                	System.out.println("共"+ criticalNode.size() + "个\nOver!");
                	break;
            	}
            	
        		if(criticalNode.size() == basicNode.size() && numsize > areaNum) {
            		System.out.print("无法达到要求..\nOver!");
            		break;
            	}
        		
//            	criticalNode = oneNode.delete(G, criticalNode, paths); //从控制点集合删去一个点
        		criticalNode = oneNode.delete(G, criticalNode, basicNode); //从控制点集合删去一个点
            }
        }   
        
	}
	
}
