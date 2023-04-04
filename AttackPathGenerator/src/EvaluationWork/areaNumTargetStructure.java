package EvaluationWork;

public class areaNumTargetStructure {
	private String id; //节点的id
	private int target; //1：被访问过；0：未被访问过；
	
	public String getId() {
		return id;
	}
	
	public int getTarget() {
		return target;
	}
	
	public void setId(String s) {
		id = s;
	}
	
	public void setTarget(int i) {
		target = i;
	}
}
