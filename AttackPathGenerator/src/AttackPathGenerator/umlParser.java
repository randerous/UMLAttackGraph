package AttackPathGenerator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.CommunicationPath;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.ExecutionEnvironment;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Visualization.visualizeGraph;

public class umlParser {
	
	public static int it;
	public umlParser()
	{
		
	}
	
	public void graphTest(Graph G ) throws Exception
	{
//		visualize graph
		String inputDot="D://input.dot";
//		visualizeGraph.dotGenerator(G,criticalNode,inputDot);
		String[] cmd= {"dot","-Tsvg",inputDot,"-o","out" + it++ + ".svg"};
		List<Node> tmpList = new ArrayList<Node>() ;
		visualizeGraph.dotGenerator(G,tmpList,inputDot,cmd);
	}

	
	public Graph genGraph(String path)
	{
		Graph G;
		G = new Graph();
		URI uri = URI.createFileURI(path); 
		Package p = parseUML(uri);
		processNode(G, p, 0);
		processEdge(G, p);
		
		return G;
	}

	// parse UML to get root package
	public Package parseUML(URI uri) {
		// get the root package (a model).
		ResourceSetImpl RESOURCE_SET = new ResourceSetImpl();
		UMLResourcesUtil.init(RESOURCE_SET);

		Resource resource = RESOURCE_SET.getResource(uri, true);

		Package p = (Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
		return p;
	}

	// parse component diagram
	public void processNode(Graph G, Namespace p, int level) {

		// get all vertexes 
//		System.out.printf("level: %d name: %s\n", level, p.toString());
 
		for (NamedElement i : p.getMembers()) {
//			System.out.printf("level: %d name: %s\n", level, i.toString());

			if (i instanceof Component || i instanceof Interface || i instanceof Package || i instanceof Model
					|| i instanceof org.eclipse.uml2.uml.Node || i instanceof Device
					|| i instanceof ExecutionEnvironment || i instanceof Artifact || i instanceof Port) {
				createNode(G, i.getOwnedComments(), getId(i.toString()), i.getName(), level);
				if (level > 0) {
					G.addEdge(getId(p.toString()), getId(i.toString()));
					G.addEdge(getId(i.toString()), getId(p.toString()));
				}
				if (i instanceof Namespace && ((Namespace) i).getMembers().size() > 0) {
					processNode(G, ((Namespace) i), level + 1);
				}
			}

		}
	}

	public void processEdge(Graph G, Namespace p) {
 
		// get all edge

		for (NamedElement i : p.getMembers()) {
//			System.out.println(i.toString());
			if (i instanceof Component) {
				Component instance = (Component) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processConnector(G, instance.getOwnedConnectors());
				processDependency(G, instance.getClientDependencies());
				processGeneralization(G, getId(instance.toString()), instance.getGeneralizations());
				processAssociation(G, instance.getAssociations());
			}

			if (i instanceof Interface) {
				Interface instance = (Interface) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processDependency(G, instance.getClientDependencies());
				processGeneralization(G, getId(instance.toString()), instance.getGeneralizations());
				processAssociation(G, instance.getAssociations());
			}

			if (i instanceof Package) {
				Package instance = (Package) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				
				processDependency(G, instance.getClientDependencies());
			}

			if (i instanceof Model) {
				Model instance = (Model) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processDependency(G, instance.getClientDependencies());
			}

			if (i instanceof org.eclipse.uml2.uml.Node) {
				org.eclipse.uml2.uml.Node instance = (org.eclipse.uml2.uml.Node) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processDependency(G, instance.getClientDependencies());
				processGeneralization(G, getId(instance.toString()), instance.getGeneralizations());
				processAssociation(G, instance.getAssociations());
				processCommunicationPath(G, instance.getCommunicationPaths());
				processDeployment(G, getId(instance.toString()), instance.getDeployments());
			}

			if (i instanceof Device) {
				Device instance = (Device) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processDependency(G, instance.getClientDependencies());
				processGeneralization(G, getId(instance.toString()), instance.getGeneralizations());
				processAssociation(G, instance.getAssociations());
				processCommunicationPath(G, instance.getCommunicationPaths());
				processDeployment(G, getId(instance.toString()), instance.getDeployments());
			}

			if (i instanceof ExecutionEnvironment) {
				ExecutionEnvironment instance = (ExecutionEnvironment) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processDependency(G, instance.getClientDependencies());
				processGeneralization(G, getId(instance.toString()), instance.getGeneralizations());
				processAssociation(G, instance.getAssociations());
				processCommunicationPath(G, instance.getCommunicationPaths());
				processDeployment(G, getId(instance.toString()), instance.getDeployments());
			}

			if (i instanceof Artifact) {
				Artifact instance = (Artifact) i;
				if (instance.getMembers().size() > 0) {
					processEdge(G, instance);
				}
				processDependency(G, instance.getClientDependencies());
				processGeneralization(G, getId(instance.toString()), instance.getGeneralizations());
				processAssociation(G, instance.getAssociations());
			}
		}

	}

	public void processConnector(Graph G,  EList<Connector> connectors) {
		for (Connector i : connectors) {
			if(i.getEnds().size() > 1)
			{
				String sourceID = getId(i.getEnds().get(0).getRole().toString());
				String destID = getId(i.getEnds().get(1).getRole().toString());
				G.addEdge(sourceID, destID);
				G.addEdge(destID, sourceID);
			}
			
		}
	}
	
	static int nums;

	// parse connections in Component diagram
	public void processDependency(Graph G, EList<Dependency> dependencies) {
		for (Dependency i : dependencies) {
//			System.out.println(nums++);
//			System.out.println(i.toString());
			if(!i.getClients().isEmpty() && !i.getSuppliers().isEmpty())
			{
				String sourceID = getId(i.getClients().get(0).toString());
				String destID = getId(i.getSuppliers().get(0).toString());
				G.addEdge(sourceID, destID);
			}
			
		}
	}
	
//	public void process

	public void processGeneralization(Graph G, String sourceID, EList<Generalization> generalizations) {
		for (Generalization i : generalizations) {
			String destID = getId(i.getGeneral().toString());
			G.addEdge(sourceID, destID);
			G.addEdge(destID, sourceID);
		}
	}

	public void processAssociation(Graph G, EList<Association> associations) {
		for (Association i : associations) {
			if(i.getEndTypes().size() > 1)
			{
				
				String sourceID = getId(i.getEndTypes().get(0).toString());
				String destID = getId(i.getEndTypes().get(1).toString());

//				if(!G.vertexes.containsKey(sourceID) )
//				{
//					System.out.println("no source ass1: "+i.getMembers().get(0).getName());
//					System.out.println("ass2: "+i.getMembers().get(1).getName());
//				}
//				if(!G.vertexes.containsKey(destID) )
//				{
//					System.out.println("no dest ass1: "+i.getMembers().get(0).getName());
//					System.out.println("ass2: "+i.getMembers().get(1).getName());
//				}
				G.addEdge(sourceID, destID);
				G.addEdge(destID, sourceID);
			}
			
		}
	}

	public void processCommunicationPath(Graph G, EList<CommunicationPath> communicationPaths) {
		for (CommunicationPath i : communicationPaths) {
			if(i.getEndTypes().size() > 1)
			{
				String sourceID = getId(i.getEndTypes().get(0).toString());
				String destID = getId(i.getEndTypes().get(1).toString());
				G.addEdge(sourceID, destID);
				G.addEdge(destID, sourceID);
			}
			
		}
	}

	public void processDeployment(Graph G, String sourceID, EList<Deployment> deployments) {
		for (Deployment i : deployments) {
			if(!i.getTargets().isEmpty())
			{
				String destID = getId(i.getTargets().get(0).toString());
//				out(i.getTargets().get(0).toString());
				G.addEdge(sourceID, destID);
				G.addEdge(destID, sourceID);
			}
			
		}
	}

	// parse comments to create Node
	public void createNode(Graph G, EList<Comment> comments, String id, String name, int level) {
		Node node;
		if (!comments.isEmpty()) {
//			System.out.println(comments.toString());
			String info = comments.get(0).getBody();
//			out(info);
			JSONObject obj = JSON.parseObject(info);

			// Create Node
			if (info != null && info.contains("exposure")) {
				node = new Surface(id, name, level);
				((Surface) node).setPort(name);
			} else if (info != null && info.contains("value")) {
//				out(info); 
				int value = obj.getIntValue("value");
				node = new Asset(id, name, level);
				((Asset) node).SetValue(value);
			} else {
				node = new Node(id, name, level);
			}

			// add vulnerability info
			if (info != null && info.contains("vulnerabilities")) {
//				System.out.println(obj.getJSONArray("vulnerabilities").size());	
				for (Object j : obj.getJSONArray("vulnerabilities")) {
					if (j instanceof JSONObject) {
						JSONObject temp = (JSONObject) j;
						Vulnerability v = new Vulnerability(temp.getIntValue("VulunerabilityID"),
								temp.getString("name"), temp.getIntValue("damage"), temp.getString("PreCondition"),
								temp.getString("PostCondition"), temp.getIntValue("Complexity"));
//						v.showInfo();
						node.addVunlerability(v);
					}
				}
			}
		} else {
			node = new Node(id, name, level);
		}

		// type : 0: surface, 1: Node, 2: Asset
		Vertex v;
		if (node instanceof Surface) {
			v = new Vertex(0);
		} else if (node instanceof Asset) {
			v = new Vertex(2);
		} else
			v = new Vertex(1);
		v.itself = node;
		G.addElem(v);

	}

	// general iterator for EMF
	protected void iterator(Resource resource) {
		for (TreeIterator<EObject> i = resource.getAllContents(); i.hasNext();) {

			EObject current = i.next();
			if (!(current instanceof NamedElement)) {
				i.prune();
			} else {
				NamedElement asNamed = (NamedElement) current;
				System.out.println(asNamed.getQualifiedName() + " : " + asNamed.eClass().getName());
			}
		}

	}

	// tools for parse identifier for Node
	public String getId(String str) {
		String s = str.substring(str.indexOf("@") + 1, str.indexOf(" "));
		return s;
	}

}
