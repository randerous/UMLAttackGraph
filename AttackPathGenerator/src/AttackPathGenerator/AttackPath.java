package AttackPathGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import attackGraph.connection.G;
import attackGraph.connection.node;
import attackGraph.graph.AttackGraph;
import attackGraph.graph.attackEdge;

//import UMLgenerator.UMLgenerator;
public class AttackPath {
	//restruct graph for easy finding path
	public G g;
	public Map<String, String> causes;//(edge, causes),(fromName@toName, causes)

	//store attackPath
	public ArrayList <ArrayList<String>> pathSet;
	Stack<String> path;
	Set<String> visit;
	String surface;
	ArrayList <String> assets;


	public AttackPath(AttackGraph graph)
	{
		this.pathSet = new ArrayList<ArrayList<String>>();
		this.path = new Stack<String>();
		this.visit = new HashSet<String>();
		this.g = new G();
		this.causes = new HashMap<String, String>();
		this.assets = new ArrayList<String>();
		convert(graph);
		findInput();
		genPathSet();
	}

	public void convert(AttackGraph graph)
	{
		for(attackEdge edge: graph.edges)
		{
			String edgeString = edge.from.name + "@" + edge.to.name;
			causes.put(edgeString, edge.cause);

			if(! g.nSet.containsKey(edge.from.name.toLowerCase()))
			{
				node n = new node(edge.from.name.toLowerCase(), edge.from.archType);
				n.type = edge.from.type;
				this.g.nSet.put(n.name, n);
				
			}

			if(! g.nSet.containsKey(edge.to.name.toLowerCase()))
			{
				node n = new node(edge.to.name.toLowerCase(), edge.to.archType);
				n.type = edge.to.type;
				this.g.nSet.put(n.name, n);
			}
			this.g.nSet.get(edge.from.name).conSet.add(edge.to.name);			
		}

	}

	//find surface and asset
	public void findInput()
	{
		for(String str: this.g.nSet.keySet())
		{
			if(this.g.nSet.get(str).type.equals("surface"))
			{
				this.surface = str;
			}else if(this.g.nSet.get(str).type.equals("asset"))
			{
				this.assets.add(str);
			}
		}

	}
	
	public void addPath()
	{
		ArrayList<String> tmpPath = new ArrayList<String>(this.path);
		this.pathSet.add(tmpPath);
	}
	
	
	private void dfs(node cur, node dest, int maxSize)  
	{

		if(pathSet.size() >= maxSize) return;
		if(cur.name.equals(dest.name))
		{
			this.path.add(dest.name);
			addPath();
			this.path.remove(dest.name);
			return;
		}
		
		this.visit.add(cur.name);
		this.path.add(cur.name); 

		for(String next: cur.conSet)
		{
			if(!this.visit.contains(next))
			{
				dfs(this.g.nSet.get(next), dest, maxSize);
			}
		}

		this.visit.remove(cur.name);
		this.path.remove(cur.name);
 	
	}
	
	public int getNums()
	{
		return pathSet.size();
	}


	public void genPathSet()
	{
		for(String asset: this.assets)
		{
			dfs(this.g.nSet.get(surface), this.g.nSet.get(asset), this.g.nSet.size());
		}
	}
 
  
	
	public void showInfo()
	{
		if(pathSet.isEmpty()) {
			System.out.println("No path");
			return;
		}

		System.out.printf("path nums: %d\n\n", pathSet.size());

		for(String asset: this.assets)
		{
			System.out.println("asset: " + asset);
		}

		for(ArrayList<String> path: this.pathSet)
		{
			for(int i = 0; i < path.size(); i++)
			{
				System.out.print(path.get(i));
				if(i < path.size() - 1)
				{
					String cause = this.causes.get(path.get(i) + "@" + path.get(i + 1));
					System.out.print(" ; "+ cause + " ; ");
				}
			}
			System.out.println();
		}
		
 
	}
	

}
