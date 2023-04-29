package attackGraph.connection;
import attackGraph.connection.G;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.CommunicationPath;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.ExecutionEnvironment;
import org.eclipse.uml2.uml.NamedElement;

import org.eclipse.uml2.uml.Package;
public class connectionBuilder {
    public G g;
    public Package p;

    public connectionBuilder() {
        this.g = new G();
    }

    public void  createConnection(Package p)
    {
    	this.p = p;
    	createNode();
    	createEdge();
    	
    }
    
    public String getName(Element e)
	{
		if(e instanceof NamedElement)
		{
			NamedElement na = (NamedElement) e;
//			System.out.println(na.getName());
			return na.getName().toLowerCase();
		}
		return null;
	}
    
    void createEdgehelper(String source, String end)
    {
    	source = source.toLowerCase();
    	end = end.toLowerCase();
    	if(source.equals(end)) return;
    	if(this.g.nSet.containsKey(source)) this.g.nSet.get(source).conSet.add(end);
    	else System.out.println("no node :" + source);
    	if(this.g.nSet.containsKey(end))this.g.nSet.get(end).conSet.add(source);
    	else System.out.println("no node :" + end);
    }
    void createEdge()
    {
    	for (NamedElement i : this.p.getMembers()) {
    		if (i instanceof Component) {
				Component instance = (Component) i;

				for (Connector con : instance.getOwnedConnectors()) {
					if (con.getEnds().size() > 1) {
						
						String source = getName(con.getEnds().get(0).getRole().getOwner()); 
						String dest = getName(con.getEnds().get(1).getRole().getOwner()); 
						if(!source.equals("system") && !dest.equals("system"))
						{
							createEdgehelper(source, dest); ;
						} 
					}
				}
			}

			if (i instanceof Node) {
				Node instance = (Node) i;
				String nodeName = instance.getName().toLowerCase();
				for(Deployment d: instance.getDeployments())
				{ 
					for(Element e : d.getTargets())
					{
						// System.out.println(nodeName + " " + getName(e));
						createEdgehelper(nodeName, getName(e)); 
					}
				}
				for(CommunicationPath c: instance.getCommunicationPaths())
				{ 
					
					for(Element e : c.getMembers())
					{
//						if(e instanceof NamedElement)
//						{
//							System.out.println("com" + ((NamedElement)e).getName());
//						}
						createEdgehelper(nodeName, getName(e));
					}
				}
			}
    	}
    }
    
    void createNode()
    {
    	for (NamedElement i : this.p.getMembers()) {
    	
    		
    	  if (i instanceof Component || i instanceof org.eclipse.uml2.uml.Node || i instanceof Device
					|| i instanceof ExecutionEnvironment) {
    		  String name = i.getName().toLowerCase();
//    		  if(i instanceof Component ) {
//    			  System.out.println("com" + name);
//    		  }
//
//			  if(i instanceof org.eclipse.uml2.uml.Node ) {
//				System.out.println("node" + name);
//			}
//
//			if(i instanceof ExecutionEnvironment ) {
//				System.out.println("ExecutionEnvironment" + name);
//			}
//
//			if(i instanceof Device ) {
//				System.out.println("device" + name);
//			}
				String type = "none";
			  if(!i.getOwnedComments().isEmpty()  )
			  {
				String info = i.getOwnedComments().get(0).getBody();
				if(info.contains("surface"))
				{
					type = "surface";
				}else if(info.contains("asset"))
				{
					type = "asset";
				}
			  }
    		  if(name.equals("system"))
    		  {
    			  for(Element e: i.getOwnedElements())
    			  {
    				  type = "none";
    				  if(!e.getOwnedComments().isEmpty()  )
    				  {
    					String info = e.getOwnedComments().get(0).getBody();
    					if(info.contains("surface"))
    					{
    						type = "surface";
    					}else if(info.contains("asset"))
    					{
    						type = "asset";
    					}
    				  }
    				  if(e instanceof Component) {
    					  node n = new node(getName(e), "component");
						  n.type = type;
        				  this.g.nSet.put(getName(e), n);
        				  getName(e);
    				  }
//    				  getName(e);
    			  }
    		  } 
    		  if(!name.equals("system") && !g.nSet.containsKey(name))
    		  {
    			  node n;
    			  if(i instanceof Component ) {
    				  n = new node(name, "component");
					  n.type = type;
    				  this.g.nSet.put(name, n);
    			  }else 
    			  {
    				  n = new node(name, "node");
					  n.type = type;
    				  this.g.nSet.put(name, n);
    			  }
    			  
    		  }
    	  }
    	}
    }
    
    
    void check() {
		for (NamedElement i : this.p.getMembers()) {
			// System.out.println(i.toString());
			if (i instanceof Component) {
				Component instance = (Component) i;

				for (Connector con : instance.getOwnedConnectors()) {
					if (con.getEnds().size() > 1) {
						
						String sourceID = getName(con.getEnds().get(0).getRole().getOwner()); 
						String destID = getName(con.getEnds().get(1).getRole().getOwner()); 
						 
//						System.out.println(instance.getName() + sourceID + "  " + destID );
					}
				}
			}

			if (i instanceof Node) {
				Node instance = (Node) i;
				 
				for(Deployment d: instance.getDeployments())
				{ 
					for(Element e : d.getTargets())
					{
						getName(e);
					}
				}
				for(CommunicationPath c: instance.getCommunicationPaths())
				{ 
					System.out.println("commni");
					for(Element e : c.getMembers())
					{
						getName(e);
					}
				}
				instance.getCommunicationPaths();

				 
			}
		}

	}

    
}
