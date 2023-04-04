package AttackPathGenerator;

import java.util.HashSet;
import java.util.Set;

/*
 * for fast find connected nodes in graph
 */
public class unionSetUtil {

	public  void mergeParent(int parent[], int i, int j) {
		int pa = getParent(parent, i);
		int pb = getParent(parent, j);
		if (pa != pb) {
			parent[pb] = pa;
		}
	}

	public void initParent(int parent[]) {
		for (int i = 0; i < parent.length; i++)
			parent[i] = i;
	}

	public void updateParent(int parent[]) {
		for (int i = 0; i < parent.length; i++)
			getParent(parent, i);
	}
	
	public  Set<Integer> getParentRes(int parent[])
	{
		Set<Integer> eq = new HashSet<Integer>();
		
		for(int i = 0; i < parent.length; i++)
		{
			int p = getParent(parent, i);
			if(!eq.contains(i))
				eq.add(i);
		}
		return eq;
	}

	public int getParent(int parent[], int index) {
		if (index == parent[index])
			return index;
		return parent[index] = getParent(parent, parent[index]);
	}


}
