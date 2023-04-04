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
package AttackPathGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.uml2.uml.ConnectorEnd;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.ExecutionEnvironment;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;

import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Port;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import UMLgenerator.UMLgenerator;

/**
 * A Java program that may be run stand-alone (with the required EMF and UML2
 * bundle JARs on the classpath) to create the example model illustrated in the
 * <em>Getting Started with UML2</em> article on the Wiki.
 * 
 * @see http://wiki.eclipse.org/MDT/UML2/Getting_Started_with_UML2
 */
public class Generator {

	public static boolean DEBUG = true;
	static int averagenums;
//	private static Graph G;
//	private static int global_id;

	public static void generator() throws IOException {
		// generator graph
		Graph G;
		umlParser umlParser = new umlParser();
//		G = umlParser.genGraph("../test/test.uml");
		G = umlParser.genGraph("D:\\×ÀÃæ\\»ªÎª\\proj\\UMLGraph\\UMLGraph\\AttackPathGenerator\\Example_UML.uml");

		// simplify graph;
//				G.showInfo();
//				G.showDetailInfo();

		out("---gen graph finished--");
		simplifier simplifier = new simplifier();
		G = simplifier.simplify(G);
		out("---gen simplified graph finished--");
				G.showInfo();
//				G.showDetailInfo();

//				G.showDetailInfo();
		AttackPath paths = new AttackPath();
		paths.genPath(G, 100);
		paths.showInfo();
		averagenums += paths.getNums()/10;

	}

	public static void main(String[] args) throws Exception {
 
//		for(int i = 0; i < 10; i++)
//		{
//			UMLgenerator.generator(100, 2);
//			generator();
//		}
//		UMLgenerator.generator(50, 2);
		generator();
//		System.out.println(averagenums);
		
		

	}

	// tools to simplify system.out, & for debug
	protected static void out(String format, Object... args) {
		if (DEBUG) {
			System.out.printf(format, args);
			if (!format.endsWith("%n")) {
				System.out.println();
			}
		}
	}

}
