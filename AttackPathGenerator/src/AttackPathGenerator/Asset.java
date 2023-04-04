package AttackPathGenerator;

public class Asset extends Node{
	int Value;
	
	public Asset(String id, String name, int level) {
		super(id, name, level);
		// TODO Auto-generated constructor stub
	}
	public void SetValue(int value) {
		this.Value = value;
	}
	
	public void showNodeInfo() {
		System.out.printf("Asset Value:%d\n",Value);
		super.showNodeInfo();
    }
	public int getValue()
	{
		return this.Value;
	}
	
}
