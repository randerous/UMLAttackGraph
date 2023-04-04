package AttackPathGenerator;

public class Surface extends Node{
	public Surface(String id, String name, int level) {
		super(id, name, level);
		// TODO Auto-generated constructor stub
	}

	String exposure_port;
	
	public void setPort(String port) {
		this.exposure_port = port;
	}
	
	public void showNodeInfo()
	{
		System.out.printf("exposure: %s\n", exposure_port);
		super.showNodeInfo();
	}
	
}
