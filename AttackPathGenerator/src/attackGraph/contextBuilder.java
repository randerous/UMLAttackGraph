package attackGraph;

import attackGraph.abac.attributeProvider;
import attackGraph.abac.attributeProviderSet;
import attackGraph.abac.policy;
import attackGraph.abac.rule;
import attackGraph.attacker.attack;
import attackGraph.attacker.attacker;
import attackGraph.connection.connectionBuilder;
import attackGraph.user_specification.user_specification;
import attackGraph.vulnerability.vulnerability;
import attackGraph.vulnerability.vulnerablityCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class contextBuilder {
	public attributeProviderSet attributeProviderSet;
	public policy policyset;
	public attacker attacker;
	public vulnerablityCollection vulSet;
	public Package p;
	public connectionBuilder conBuilder;

	public contextBuilder() {
		parseAttribute();
		parseAttacker();
		parsePolicy();
		parseUML("../test/test.uml");
		parseVulset();
		buildCon();
	}
	public String getId(String str) {
		String s = str.substring(str.indexOf("@") + 1, str.indexOf(" "));
		return s;
	}
	
	public String getName(Element e)
	{
		if(e instanceof NamedElement)
		{
			NamedElement na = (NamedElement) e;
			// System.out.println(na.getName());
			return na.getName();
		}
		return null;
	}
	public void buildCon()
	{
		this.conBuilder = new connectionBuilder();
		this.conBuilder.createConnection(this.p);
	}
	
	
	public void parseUML(String path) {
		URI uri = URI.createFileURI(path);
		ResourceSetImpl RESOURCE_SET = new ResourceSetImpl();
		UMLResourcesUtil.init(RESOURCE_SET);
		Resource resource = RESOURCE_SET.getResource(uri, true);
		this.p = (Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
	}

	void parseVulset() {
		this.vulSet = new vulnerablityCollection();
		for (NamedElement i : this.p.getMembers()) {
			if (i instanceof Component || i instanceof org.eclipse.uml2.uml.Node || i instanceof Device
					|| i instanceof ExecutionEnvironment) {

//				System.out.println(i.getName());
				if (!i.getOwnedComments().isEmpty()) {
					String info = i.getOwnedComments().get(0).getBody();
					// System.out.println(info+"fsaadsfafs");

					if (info.contains("vulnerabilities")) {
						JSONObject obj = JSON.parseObject(info);
						JSONArray vulList = obj.getJSONArray("vulnerabilities");
						ArrayList<vulnerability> vlist = new ArrayList<vulnerability>();

						for (int j = 0; j < vulList.size(); j++) {
							JSONObject vul = vulList.getJSONObject(j);
							vulnerability v = new vulnerability(vul.getString("CVEID"), vul.getString("CWEID"),
									vul.getString("AttackVector"), null, null, null,
									vul.getString("IsTakeOver").equals("true"), vul.getString("ConfidentialityImpact"));

							JSONArray elemArr = vul.getJSONArray("RequiredElements");
							JSONArray specArr = vul.getJSONArray("RequiredPrivileges");
							JSONArray gainArr = vul.getJSONArray("GainedPrivileges");

							ArrayList<String> elmList = new ArrayList<String>();
							ArrayList<user_specification> specList = new ArrayList<user_specification>();
							Set<user_specification> gainList = new HashSet<user_specification>();

							for (int k = 0; k < elemArr.size(); k++) {
								JSONObject obj_e = elemArr.getJSONObject(k);
								addElemList("node", elmList, obj_e);
								addElemList("device", elmList, obj_e);
								addElemList("executionEnvironment", elmList, obj_e);
								addElemList("component", elmList, obj_e);
								addElemList("node_con", elmList, obj_e);
							}

							for (int k = 0; k < specArr.size(); k++) {
								JSONObject obj_u = specArr.getJSONObject(k);
								String str = obj_u.getString("attributeValue");
								if (str != null)
									specList.add(new user_specification(str));
							}

							for (int k = 0; k < gainArr.size(); k++) {
								JSONObject obj_u = gainArr.getJSONObject(k);
								String str = obj_u.getString("attributeValue");
								if (str != null)
									gainList.add(new user_specification(str));
							}
							v.RequiredElements = elmList;
							v.RequiredPrivileges = specList;
							v.GainedPrivileges = gainList;
							vlist.add(v);

						}

						this.vulSet.vulSet.put(i.getName().toLowerCase(), vlist);
					}
				}
			}
		}

	}

	void addElemList(String archType, ArrayList<String> l, JSONObject obj) {
		String str = obj.getString(archType);
		if (str != null)
			l.add(str);
	}

	void addEntity(String archType, JSONObject obj, rule r) {
		String entity = obj.getString(archType);
		if (entity != null)
			r.entities.add(entity);
	}

	public void parsePolicy() {
		this.policyset = new policy();
		String path = "src/configJson/policySet.json";

		try {
			String file = Files.readString(Paths.get(path));
			JSONObject obj = JSON.parseObject(file);
			JSONObject policySet = obj.getJSONObject("PolicySet");
			JSONArray Policies = policySet.getJSONArray("Policies");

			for (int i = 0; i < Policies.size(); i++) {
				JSONObject policy = Policies.getJSONObject(i).getJSONObject("Policy");
				JSONArray Rules = policy.getJSONArray("Rules");

				for (int j = 0; j < Rules.size(); j++) {
					JSONObject rule = Rules.getJSONObject(j);
					JSONObject ruleContent = rule.getJSONObject("rule");

					Boolean isPermit = ruleContent.getString("decision").equals("permit");
					rule r = new rule(isPermit);

					JSONArray entities = ruleContent.getJSONArray("entities");
					for (int k = 0; k < entities.size(); k++) {
						addEntity("NodeMatch", entities.getJSONObject(k), r);
						addEntity("NodeCon_Match", entities.getJSONObject(k), r);
						addEntity("DeviceMatch", entities.getJSONObject(k), r);
						addEntity("ComponentMatch", entities.getJSONObject(k), r);
						addEntity("ExecutionEnvironmentMatch", entities.getJSONObject(k), r);
					}

					JSONArray Expression = ruleContent.getJSONArray("Expression");
					for (int k = 0; k < Expression.size(); k++) {
						String role = Expression.getJSONObject(k).getString("attributeValue");
						if (role != null) {
							user_specification u = new user_specification(role);
							r.attribute.add(u);
						}
					}

					this.policyset.rules.add(r);

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addUser(String provider, String pname, JSONArray arr) {
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = arr.getJSONObject(i).getJSONObject(provider);
			if (obj == null)
				return;
			String name = obj.getString(pname);

			attributeProvider attr = new attributeProvider(name);
			JSONArray users = arr.getJSONObject(i).getJSONObject(provider)
					.getJSONArray("UserSpecification");
			for (int j = 0; j < users.size(); j++) {
				user_specification u = new user_specification(users.getJSONObject(j).getString("attributeValue"));
				attr.user_specifications.add(u);
			}

			this.attributeProviderSet.providers.add(attr);
		}
	}

	public void parseAttribute() {
		this.attributeProviderSet = new attributeProviderSet();
		String path = "src/configJson/attributeProviders.json";

		try {
			String file = Files.readString(Paths.get(path));
			JSONObject obj = JSON.parseObject(file);

			JSONArray arr = obj.getJSONArray("AttributeProviderSet");
			this.attributeProviderSet = new attributeProviderSet();
			addUser("componentAttributeProvider", "component", arr);
			addUser("nodeAttributeProvider", "node", arr);
			addUser("node_ConAttributeProvider", "node_Con", arr);
			addUser("executionEnvironmentAttributeProvider", "executionEnvironment", arr);
			addUser("deviceAttributeProvider", "device", arr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void parseAttacker() {
		this.attacker = new attacker();
		String path = "src/configJson/attacker.json";
		// String path = "src/configJson/1.json";
		try {
			String file = Files.readString(Paths.get(path));
			JSONObject obj = JSON.parseObject(file);
			obj = obj.getJSONObject("attacker");

			JSONArray arrAttack = obj.getJSONArray("attack");
			JSONArray arrPrivileges = obj.getJSONArray("privileges");
			JSONArray arrElems = obj.getJSONArray("compromisedElements");

			// to collect attack
			for (int i = 0; i < arrAttack.size(); i++) {
				String cve = arrAttack.getJSONObject(i).getString("CVEID");
				String cwe = arrAttack.getJSONObject(i).getString("CWEID");
				attack a = new attack(cve, cwe);
				this.attacker.attacks.add(a);
			}

			// to collect user spec
			for (int i = 0; i < arrPrivileges.size(); i++) {
				String role = arrPrivileges.getJSONObject(i).getString("attributeValue");
				if (role != null) {
					user_specification u = new user_specification(role);
					attacker.privileges.add(u);
				}
			}

			// to collect elems
			for (int i = 0; i < arrElems.size(); i++) {
				JSONObject o = arrElems.getJSONObject(i);
				addElems("Node", o);
				addElems("Component", o);
				addElems("Node_Con", o);
				addElems("Device", o);
				addElems("ExecutionEnvironment", o);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void addElems(String archType, JSONObject obj) {
		String elem = obj.getString(archType);
		if (elem != null) {
			this.attacker.compromiseElements.add(elem);
		}
	}

}
