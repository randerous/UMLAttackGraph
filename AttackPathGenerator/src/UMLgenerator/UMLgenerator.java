/*
 * Copyright (c) 2014, 2018 CEA and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Christian W. Damus (CEA) - initial API and implementation
 *   Kenn Hussey - 535301
 *
 */
package UMLgenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.modelmbean.ModelMBeanOperationInfo;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.common.util.UML2Util;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.ConnectableElement;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.DeploymentSpecification;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.ExecutionEnvironment;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import AttackPathGenerator.Graph;
import AttackPathGenerator.simplifier;
import AttackPathGenerator.umlParser;
import Visualization.visualizeGraph;

/**
 * A Java program that may be run stand-alone (with the required EMF and UML2
 * bundle JARs on the classpath) to create the example model illustrated in the
 * <em>Getting Started with UML2</em> article on the Wiki.
 * 
 * @see http://wiki.eclipse.org/MDT/UML2/Getting_Started_with_UML2
 */
public class UMLgenerator {

	public static boolean DEBUG = false;

	private static File outputDir;
	private static int nodes_num;
	private static int edges_num;

	private static ArrayList<Model> models;
	private static ArrayList<Component> components;
	private static ArrayList<org.eclipse.uml2.uml.Package> packages;
	private static ArrayList<Interface> interfaces;
	private static ArrayList<Node> nodes;
	private static ArrayList<Device> devices;
	private static ArrayList<ExecutionEnvironment> executionEnvironments;
	private static ArrayList<Artifact> artifacts;

	public void UMLgenerator() {

	}

	static void init() {
		models = new ArrayList<Model>();
		components = new ArrayList<Component>();
		packages = new ArrayList<org.eclipse.uml2.uml.Package>();
		interfaces = new ArrayList<Interface>();
		nodes = new ArrayList<Node>();
		devices = new ArrayList<Device>();
		executionEnvironments = new ArrayList<ExecutionEnvironment>();
		artifacts = new ArrayList<Artifact>();
	}

	/**
	 * The main program. It expects one argument, which is the local filesystem path
	 * of a directory in which to create the <tt>ExtendedPO2.uml</tt> file.
	 * 
	 * @param the program arguments, which must consist of a single filesystem path
	 */
	public static void main(String[] args) throws Exception {
		
		int sum = 0;
		int j = 1000;
		while(j > 0 )
		{
			j--;
			generator(100, 2);
			umlParser parser = new umlParser();
//			
			Graph G = parser.genGraph("Example_UML.uml");
//			parser.graphTest(G);
			simplifier simplifier = new simplifier();
			int size = G.getVertexes().size();
			System.out.println("initial  size : "+ size);
			simplifier.simplify(G);
			size = G.getVertexes().size();
			System.out.println("simplified  size : "+ size);
//			parser.graphTest(G);
			sum += size;
		}
		System.out.println(sum);
		

	}

	/*
	 * nums: all node nums in umlgraph average_edges: average num of edge in graph,
	 * set = 2
	 * 
	 */
	public static void generator(int nums, double average_edges) throws Exception {

//		
		outputDir = new File(".").getCanonicalFile();
		nodes_num = nums;
		edges_num = (int) ((int) nodes_num * average_edges);
		init();

		// Create the root package (a model).
		Model UML2Model = createModel("uml2");

		createNode(UML2Model, nodes_num);
		edges_num -= creatBasicAllConnect();
		out("all random" + edges_num);
		createRandomConnections(edges_num);

		// Save our model to a file in the user-specified output directory

		URI outputURI = URI.createFileURI(outputDir.getAbsolutePath()).appendSegment("Example_UML")
				.appendFileExtension(UMLResource.FILE_EXTENSION);

		banner("Example uml created, saved to %s.", outputURI.toFileString());
		save(UML2Model, outputURI);

	}

	/*
	 * create a graph with minimal limit connect, all node can reach in a graph, no
	 * alone node return used edges
	 */
	static int creatBasicAllConnect() {
		/*
		 * eight types of nodes dependencies can be applied to all node take nodes into
		 * three group by the type of connection Only dependencies: Model , Package
		 * Generalization, Association: Component, Interface, Artifacts Communication
		 * Path, Deployment: Node, Device,Execution Environment
		 */
		int usedEdges = 0;
		/*
		 * create three sub connected graph
		 */
		usedEdges += createModelAndpackage();
		usedEdges += createComIntArt();
		usedEdges += createNodeDevExec();

		// connect three graph
		Namespace a, b, c;
		a = components.get(0);
		b = models.get(0);
		c = nodes.get(0);
		createDependencies(a, b);
		createDependencies(a, c);

		usedEdges += 2;
		return usedEdges;

	}

	static int createModelAndpackage() {
		int usedEdges = 0;

		// subgraph including Model-Package
		ArrayList<Namespace> modelAndpackage = new ArrayList<Namespace>();
		ArrayList<Namespace> picked_den = new ArrayList<Namespace>();
		modelAndpackage.addAll(models);
		modelAndpackage.addAll(packages);

		usedEdges += modelAndpackage.size() - 1;
		picked_den.add(modelAndpackage.get(0));
		modelAndpackage.remove(0);
		while (!modelAndpackage.isEmpty()) {
			Namespace a, b;
			int pickedIndex = (int) (Math.random() * picked_den.size());
			a = picked_den.get(pickedIndex);
			b = modelAndpackage.get(0);
			modelAndpackage.remove(0);
			picked_den.add(b);
			createDependencies(a, b);
		}

		return usedEdges;
	}

	static int createComIntArt() {
		int usedEdges = 0;
		// subgraph including Component, Interface, Artifacts, create association
		ArrayList<Type> ComIntArt = new ArrayList<Type>();
		ArrayList<Type> picked_ass = new ArrayList<Type>();
		ComIntArt.addAll(components);
		ComIntArt.addAll(interfaces);
		ComIntArt.addAll(artifacts);

		usedEdges += ComIntArt.size() - 1;
		picked_ass.add(ComIntArt.get(0));
		ComIntArt.remove(0);
		while (!ComIntArt.isEmpty()) {
			Type a, b;
			int pickedIndex = (int) (Math.random() * picked_ass.size());
			a = picked_ass.get(pickedIndex);
//			System.out.println(a.getName() + picked_ass.size());
			b = ComIntArt.get(0);
			ComIntArt.remove(0);
			picked_ass.add(b);
			createAssociations(a, b);
		}
		return usedEdges;

	}

	static int createNodeDevExec() {
		int usedEdges = 0;
		// subgraph for Node, Device,Execution Environment, communication
		ArrayList<Node> NodeDevExec = new ArrayList<Node>();
		ArrayList<Node> picked_cmm = new ArrayList<Node>();
		NodeDevExec.addAll(nodes);
		NodeDevExec.addAll(devices);
		NodeDevExec.addAll(executionEnvironments);

		usedEdges += NodeDevExec.size() - 1;
		picked_cmm.add(NodeDevExec.get(0));
		NodeDevExec.remove(0);
		while (!NodeDevExec.isEmpty()) {
			Node a, b;
			int pickedIndex = (int) (Math.random() * picked_cmm.size());
			a = picked_cmm.get(pickedIndex);
			b = NodeDevExec.get(0);
			NodeDevExec.remove(0);
			picked_cmm.add(b);
			createCommunicationPaths(a, b);
		}
		return usedEdges;
	}

	/*
	 * Random connections will be created in basic connected graph
	 */
	static void createRandomConnections(int nums) {

		int each_num = nums / 5;
		ArrayList<Namespace> dependElems = new ArrayList<Namespace>();
		ArrayList<Classifier> generalizationElems = new ArrayList<Classifier>();
		ArrayList<Type> associateElems = new ArrayList<Type>();
		ArrayList<Node> nodeElems = new ArrayList<Node>();

		processTypedNode(dependElems, generalizationElems, associateElems, nodeElems);

		for (int i = 0; i < each_num; i++) {
			int j = (int) (Math.random() * dependElems.size());
			int k = (int) (Math.random() * dependElems.size());
			createDependencies(dependElems.get(j), dependElems.get(k));

//			System.out.println(j+k);

			j = (int) (Math.random() * generalizationElems.size());
			k = (int) (Math.random() * generalizationElems.size());
			createGeneralizations(generalizationElems.get(j), generalizationElems.get(k));

			j = (int) (Math.random() * associateElems.size());
			k = (int) (Math.random() * associateElems.size());
			createAssociations(associateElems.get(j), associateElems.get(k));

			j = (int) (Math.random() * nodeElems.size());
			k = (int) (Math.random() * nodeElems.size());
			createCommunicationPaths(nodeElems.get(j), nodeElems.get(k));
			createDeployments(nodeElems.get(j), nodeElems.get(k));

		}
	}

	/*
	 * 
	 */
	static void processTypedNode(ArrayList<Namespace> dependElems, ArrayList<Classifier> generalizationElems,
			ArrayList<Type> associateElems, ArrayList<Node> nodeElems) {
		for (Model m : models) {
			dependElems.add(m);
		}

		for (Component c : components) {
			dependElems.add(c);
			generalizationElems.add(c);
			associateElems.add(c);
		}

		for (org.eclipse.uml2.uml.Package p : packages) {
			dependElems.add(p);
		}

		for (Interface i : interfaces) {
			dependElems.add(i);
			generalizationElems.add(i);
			associateElems.add(i);
		}
		for (Node n : nodes) {
			dependElems.add(n);
			generalizationElems.add(n);
			associateElems.add(n);
			nodeElems.add(n);
		}
		for (Device d : devices) {
			dependElems.add(d);
			generalizationElems.add(d);
			associateElems.add(d);
			nodeElems.add(d);
		}

		for (ExecutionEnvironment e : executionEnvironments) {
			dependElems.add(e);
			generalizationElems.add(e);
			associateElems.add(e);
			nodeElems.add(e);
		}

		for (Artifact a : artifacts) {
			dependElems.add(a);
			generalizationElems.add(a);
			associateElems.add(a);
		}
//		ArrayList<Port> connectElems;//now there is no port
	}

	static void createDependencies(Namespace client, Namespace supplier) {
		client.createDependency(supplier);
	}

	static void createGeneralizations(Classifier specificClassifier, Classifier generalClassifier) {
		specificClassifier.createGeneralization(generalClassifier);
	}

	static void createAssociations(Type type1, Type type2) {
		type1.createAssociation(true, AggregationKind.NONE_LITERAL, type1.getName(), 0, 1, type2, true,
				AggregationKind.NONE_LITERAL, type2.getName(), 0, 1);
	}

	static void createCommunicationPaths(Node node1, Node node2) {
		node1.createCommunicationPath(true, AggregationKind.NONE_LITERAL, node1.getName(), 0, 1, node2, true,
				AggregationKind.NONE_LITERAL, node2.getName(), 0, 1);
	}

	static void createDeployments(Node node1, Node node2) {
		node1.createDeployment("").setLocation(node2);
	}

	static void createConnector(Component node, Port p1, Port p2) {
		Connector connector = node.createOwnedConnector("");
		connector.createEnd().setRole(p1);
		connector.createEnd().setRole(p2);
	}

	protected static Generalization createGeneralization(Classifier specificClassifier, Classifier generalClassifier) {

		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);

		out("Generalization %s --|> %s created.", specificClassifier.getQualifiedName(),
				generalClassifier.getQualifiedName());

		return generalization;
	}

	/*
	 * create all nodes and add comments to node
	 * 
	 */
	protected static void createNode(Model model, int nums) {
		int each_num = nums / 8;

//		a.create
		for (int i = 0; i < each_num; i++) {
			models.add((Model) create_model(model, "model" + Integer.toString(i)));
			components.add((Component) create_component(model, "component" + Integer.toString(i)));
			packages.add((org.eclipse.uml2.uml.Package) create_package(model, "package" + Integer.toString(i)));
			interfaces.add((Interface) create_interface(model, "interface" + Integer.toString(i)));
			devices.add((Device) create_device(model, "device" + Integer.toString(i)));
			nodes.add((Node) create_node(model, "node" + Integer.toString(i)));
			executionEnvironments.add((ExecutionEnvironment) create_execution_environment(model,
					"executionEnvironment" + Integer.toString(i)));
			artifacts.add((Artifact) create_artifact(model, "artifact" + Integer.toString(i)));

			creatComments(i);

		}
		for (int i = 0; i < nums - 8 * each_num; i++) {
			components.add((Component) create_component(model, "component" + Integer.toString(i + each_num)));
		}

	}

	static void creatComments(int i) {
		int j = (int) (Math.random() * 7);
		int selector = (int) (Math.random() * 2);
//		 System.out.println("selector"+selector);
		if (selector < 1) {
			switch (j) {
			case 0:
				creatExposureComments(models.get(i));
				break;
			case 1:
				creatExposureComments(components.get(i));
				break;
			case 2:
				creatExposureComments(packages.get(i));
				break;
			case 3:
				creatExposureComments(interfaces.get(i));
				break;
			case 4:
				creatExposureComments(devices.get(i));
				break;
			case 5:
				creatExposureComments(nodes.get(i));
				break;
			case 6:
				creatExposureComments(executionEnvironments.get(i));
				break;
			case 7:
				creatExposureComments(artifacts.get(i));
				break;
			default:
				break;

			}
		} else {
			switch (j) {
			case 0:
				creatAssetComments(models.get(i), i);
				break;
			case 1:
				creatAssetComments(components.get(i), i);
				break;
			case 2:
				creatAssetComments(packages.get(i), i);
				break;
			case 3:
				creatAssetComments(interfaces.get(i), i);
				break;
			case 4:
				creatAssetComments(devices.get(i), i);
				break;
			case 5:
				creatAssetComments(nodes.get(i), i);
				break;
			case 6:
				creatAssetComments(executionEnvironments.get(i), i);
				break;
			case 7:
				creatAssetComments(artifacts.get(i), i);
				break;
			default:
				break;

			}
		}

	}

	static void creatExposureComments(Namespace p) {
		p.createOwnedComment().setBody("{\"exposure\":true}");
	}

	static void creatAssetComments(Namespace p, int value) {
		p.createOwnedComment().setBody("{\n" + "\"value\":" + Integer.toString(value) + "\n" + "}");
	}

	protected static Model createModel(String name) {
		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);

		out("Model '%s' created.", model.getQualifiedName());

		return model;
	}

	protected static org.eclipse.uml2.uml.Package createPackage(org.eclipse.uml2.uml.Package nestingPackage,
			String name) {

		org.eclipse.uml2.uml.Package package_ = nestingPackage.createNestedPackage(name);
		out("Package '%s' created.", package_.getQualifiedName());
		return package_;
	}

	protected static PrimitiveType createPrimitiveType(org.eclipse.uml2.uml.Package package_, String name) {

		PrimitiveType primitiveType = package_.createOwnedPrimitiveType(name);

		out("Primitive type '%s' created.", primitiveType.getQualifiedName());

		return primitiveType;
	}

	protected static Enumeration createEnumeration(org.eclipse.uml2.uml.Package package_, String name) {

		Enumeration enumeration = package_.createOwnedEnumeration(name);

		out("Enumeration '%s' created.", enumeration.getQualifiedName());

		return enumeration;
	}

	protected static EnumerationLiteral createEnumerationLiteral(Enumeration enumeration, String name) {

		EnumerationLiteral enumerationLiteral = enumeration.createOwnedLiteral(name);

		out("Enumeration literal '%s' created.", enumerationLiteral.getQualifiedName());

		return enumerationLiteral;
	}
	/*
	 * Methods for creating nodes in diagrams
	 * 
	 */

	protected static org.eclipse.uml2.uml.PackageableElement create_model(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.MODEL;
		org.eclipse.uml2.uml.PackageableElement model_ = package_.createPackagedElement(name, eClass);

		out("Model %s created.", name);

		return model_;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_package(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.PACKAGE;
//		package_.createNestedPackage("fdsa");
		org.eclipse.uml2.uml.PackageableElement package_in_diagram = package_.createPackagedElement(name, eClass);

		out("Package %s created.", name);

		return package_in_diagram;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_artifact(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.ARTIFACT;
		org.eclipse.uml2.uml.PackageableElement artifact_ = package_.createPackagedElement(name, eClass);

		out("Artifact %s created.", name);

		return artifact_;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_device(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.DEVICE;
		org.eclipse.uml2.uml.PackageableElement device_ = package_.createPackagedElement(name, eClass);

		out("Device %s created.", name);

		return device_;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_execution_environment(
			org.eclipse.uml2.uml.Package package_, String name) {

		EClass eClass = UMLPackage.Literals.EXECUTION_ENVIRONMENT;
		org.eclipse.uml2.uml.PackageableElement execution_environment_ = package_.createPackagedElement(name, eClass);

		out("Execution environment %s created.", name);

		return execution_environment_;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_node(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.NODE;
		org.eclipse.uml2.uml.PackageableElement node_ = package_.createPackagedElement(name, eClass);

		out("Node %s created.", name);

		return node_;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_component(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.COMPONENT;
		org.eclipse.uml2.uml.PackageableElement component_ = package_.createPackagedElement(name, eClass);

		out("Component %s created.", name);

		return component_;
	}

	protected static org.eclipse.uml2.uml.PackageableElement create_interface(org.eclipse.uml2.uml.Package package_,
			String name) {

		EClass eClass = UMLPackage.Literals.INTERFACE;
		org.eclipse.uml2.uml.PackageableElement interface_ = package_.createPackagedElement(name, eClass);

		out("Interface %s created.", name);

		return interface_;
	}

	/*
	 * Methods for creating edges in diagrams
	 * 
	 */

	protected static Association createAssociation(Type type1, boolean end1IsNavigable, AggregationKind end1Aggregation,
			String end1Name, int end1LowerBound, int end1UpperBound, Type type2, boolean end2IsNavigable,
			AggregationKind end2Aggregation, String end2Name, int end2LowerBound, int end2UpperBound) {

		Association association = type1.createAssociation(end1IsNavigable, end1Aggregation, end1Name, end1LowerBound,
				end1UpperBound, type2, end2IsNavigable, end2Aggregation, end2Name, end2LowerBound, end2UpperBound);

		out("Association %s [%s..%s] %s-%s %s [%s..%s] created.", //
				UML2Util.isEmpty(end1Name)
						// compute a placeholder for the name
						? String.format("{%s}", type1.getQualifiedName()) //
						// user-specified name
						: String.format("'%s::%s'", type1.getQualifiedName(), end1Name), //
				end1LowerBound, // no special case for this
				(end1UpperBound == LiteralUnlimitedNatural.UNLIMITED) ? "*" // special case for unlimited upper bound
						: end1UpperBound, // finite upper bound
				end2IsNavigable ? "<" // indicate navigability
						: "-", // not navigable
				end1IsNavigable ? ">" // indicate navigability
						: "-", // not navigable
				UML2Util.isEmpty(end2Name)
						// compute a placeholder for the name
						? String.format("{%s}", type2.getQualifiedName()) //
						// user-specified name
						: String.format("'%s::%s'", type2.getQualifiedName(), end2Name), //
				end2LowerBound, // no special case for this
				(end2UpperBound == LiteralUnlimitedNatural.UNLIMITED) ? "*" // special case for unlimited upper bound
						: end2UpperBound);

		return association;
	}

	//
	// Program control
	//

	private static boolean processArgs(String[] args) throws IOException {

		if (args.length != 2) {
			err("Expected 2 argument.");
			err("Usage: java -jar ... %s <dir>", UMLgenerator.class.getSimpleName());
			err("where");
			err("<dir> - path to output folder in which to save the UML model");
			return false;
		}

		nodes_num = Integer.parseInt(args[1]);
		outputDir = new File(args[0]).getCanonicalFile();
		if (!outputDir.exists()) {
			err("No such directory: %s", outputDir.getAbsolutePath());
			return false;
		}

		if (!outputDir.isDirectory()) {
			err("Not a directory: %s", outputDir.getAbsolutePath());
			return false;
		}

		if (!outputDir.canWrite()) {
			err("Cannot create a file in directory: %s", outputDir.getAbsolutePath());
			return false;
		}

		return true;
	}

	protected static void save(org.eclipse.uml2.uml.Package package_, URI uri) {
		// Create a resource-set to contain the resource(s) that we are saving
		ResourceSet resourceSet = new ResourceSetImpl();

		// Initialize registrations of resource factories, library models,
		// profiles, Ecore metadata, and other dependencies required for
		// serializing and working with UML resources. This is only necessary in
		// applications that are not hosted in the Eclipse platform run-time, in
		// which case these registrations are discovered automatically from
		// Eclipse extension points.
		UMLResourcesUtil.init(resourceSet);

		// Create the output resource and add our model package to it.
		Resource resource = resourceSet.createResource(uri);
		resource.getContents().add(package_);

		// And save
		try {
			resource.save(null); // no save options needed
			out("Done.");
		} catch (IOException ioe) {
			err(ioe.getMessage());
		}
	}

	//
	// Logging utilities
	//

	protected static void banner(String format, Object... args) {
		System.out.println();
		hrule();

		System.out.printf(format, args);
		if (!format.endsWith("%n")) {
			System.out.println();
		}

		hrule();
		System.out.println();
	}

	protected static void hrule() {
		System.out.println("------------------------------------");
	}

	protected static void out(String format, Object... args) {
		if (DEBUG) {
			System.out.printf(format, args);
			if (!format.endsWith("%n")) {
				System.out.println();
			}
		}
	}

	protected static void err(String format, Object... args) {
		System.err.printf(format, args);
		if (!format.endsWith("%n")) {
			System.err.println();
		}
	}
}
