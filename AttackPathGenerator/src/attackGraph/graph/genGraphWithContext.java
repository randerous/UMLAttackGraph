package attackGraph.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import attackGraph.contextBuilder;
import attackGraph.abac.attributeProvider;
import attackGraph.abac.attributeProviderSet;
import attackGraph.abac.policy;
import attackGraph.connection.G;
import attackGraph.connection.node;
import attackGraph.user_specification.user_specification;
import attackGraph.vulnerability.vulnerability;
import attackGraph.vulnerability.vulnerablityCollection;

public class genGraphWithContext {
    public AttackGraph g;
    public contextBuilder context;

    public genGraphWithContext(contextBuilder context) {
        this.g = new AttackGraph();
        this.context = context;
    }

    public String findSurface() {
        for (String key : this.context.conBuilder.g.nSet.keySet()) {
            if (this.context.conBuilder.g.nSet.get(key).type.equals("surface"))
                return key;
        }
        return null;
    }

    public void gen() {
        boolean changed = true;
        G connected = this.context.conBuilder.g;
        String surface = findSurface();
        attackVertex v = new attackVertex(connected.nSet.get(surface).type, surface,
                connected.nSet.get(surface).archType);
        this.g.vertexs.add(v);

        HashSet<String> newCompromiseElems = new HashSet<String>(this.context.attacker.compromiseElements);
        newCompromiseElems.add(surface);
        // this.context.attacker.compromiseElements.add(surface);
        while (changed) {
            changed = false;
            attributeProviderSet aSet = this.context.attributeProviderSet;
            for (String str : newCompromiseElems) {
                for (attributeProvider aProvider : aSet.providers) {
                    if (str.toLowerCase().equals(aProvider.name.toLowerCase())) {
                        for (user_specification u : aProvider.user_specifications) {
                            boolean spec_exist = false;
                            Set<user_specification> tmp = new HashSet<user_specification>(
                                    this.context.attacker.privileges);

                            for (user_specification user : this.context.attacker.privileges) {
                                if (user.role.equals(u.role)) {
                                    spec_exist = true;
                                    tmp.remove(user);
                                    user.sources.add(str);
                                    tmp.add(user);
                                }
                                this.context.attacker.privileges = tmp;
                            }
                            if (!spec_exist) {
                                u.sources.add(str);
                                this.context.attacker.privileges.add(u);
                            }

                        }
                        changed = true;
                    }
                }
            }
            this.context.attacker.compromiseElements.addAll(newCompromiseElems);
            newCompromiseElems.clear();

            HashSet<String> connectElems = new HashSet<String>();
            getConnectElems(connectElems, this.context.attacker.compromiseElements);

            for (String str : connectElems) {
                if (attackUsingVulnerability(str, newCompromiseElems))
                    changed = true;
            }

            for (String str : connectElems) {
                if (newCompromiseElems.contains(str))
                    continue;
                attackUsingSpecification(str, newCompromiseElems);
            }

            if(attackUsingNetworkVul(newCompromiseElems)) changed = true;

            if (newCompromiseElems.size() > 0)
                changed = true;
        }

    }

    public void getConnectElems(HashSet<String> connectElems, Set<String> elems) {
        G collection = this.context.conBuilder.g;
        for (String str : elems) {
            for (String s : collection.nSet.get(str.toLowerCase()).conSet) {
                if (!elems.contains(s))
                    connectElems.add(s);
            }
        }
    }

    public boolean attackUsingNetworkVul(Set<String> newCompromiseElems) {
        vulnerablityCollection vlist = this.context.vulSet;
        G collection = this.context.conBuilder.g;
        boolean returnVal = false;
        for (String str : vlist.vulSet.keySet()) {
            if (!newCompromiseElems.contains(str) && !this.context.attacker.compromiseElements.contains(str)) {
                String cause = "";
                boolean newSpec = false;
                boolean isTakeOver = false;
                for (vulnerability vul : vlist.vulSet.get(str)) {
                    if (vul.AttackVector.toLowerCase().equals("network")) {
                        boolean canUsed = true;
                        for (String elem : vul.RequiredElements) {
                            if (!this.context.attacker.compromiseElements.contains(elem.toLowerCase())) {
                                canUsed = false;
                                break;
                            }
                        }

                        for (user_specification u : vul.RequiredPrivileges) {
                            boolean hasSpec = false;
                            for (user_specification spec : this.context.attacker.privileges) {
                                if (u.role.equals(spec.role)) {
                                    hasSpec = true;
                                    break;
                                }
                            }

                            if (!hasSpec) {
                                canUsed = false;
                                break;
                            }
                        }

                        for (user_specification u : vul.GainedPrivileges) {
                            boolean contains = false;
                            for (user_specification spec : this.context.attacker.privileges) {
                                if (u.role.equals(spec.role))
                                    contains = true;
                            }
                            if (!contains) {
                                newSpec = true;
                                break;
                            }
                        }

                        if (newSpec)
                            this.context.attacker.privileges.addAll(vul.GainedPrivileges);

                        if (canUsed && vul.IsTakeOver == true) {
                            isTakeOver = true;
                        }

                        if (cause.equals("")) {
                        } else if (!newSpec && isTakeOver == false) {
                        } else if (newSpec && isTakeOver == false) {
                            returnVal = true;
                        } else {

                            attackVertex dest = new attackVertex(collection.nSet.get(str).type, str,
                                    collection.nSet.get(str).archType);
                            this.g.vertexs.add(dest);
                            boolean connectedExist = false;
                            for (String elem : this.context.attacker.compromiseElements) {
                                if (collection.nSet.get(elem.toLowerCase()).conSet.contains(str)) {
                                    for (attackVertex v : this.g.vertexs) {
                                        if (v.name.equals(elem.toLowerCase())) {
                                            attackEdge edge = new attackEdge(v, dest, cause);
                                            this.g.edges.add(edge);
                                            connectedExist = true;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            if(!connectedExist)
                            {
                                attackEdge  edge = new attackEdge(this.g.vertexs.get(this.g.vertexs.size() - 1), dest, cause);
                                this.g.edges.add(edge);
                            }
                            newCompromiseElems.add(str);
                            returnVal = true;
                        }

                    }
                }

            }
        }
        return returnVal;

    }

    public void attackUsingSpecification(String s, Set<String> newCompromiseElems) {
        G collection = this.context.conBuilder.g;
        policy pSet = this.context.policyset;
        if (pSet.is_accessible(this.context.attacker, s)) {
            attackVertex dest = new attackVertex(collection.nSet.get(s).type, s, collection.nSet.get(s).archType);

            // attackVertex source = null;
            String cause = "";
            Set<String> jSet = new HashSet<String>(this.context.attacker.compromiseElements);
            if (pSet.findRule(s) != null) {
                cause += "User: ";
                for (user_specification attr : pSet.findRule(s).attribute) {
                    for (user_specification spec : this.context.attacker.privileges) {
                        if (spec.role.equals(attr.role)) {
                            jSet.retainAll(spec.sources);
                        }
                    }

                    cause += attr.role + " ";
                }
            }
            if (pSet.findRule(s) != null && !jSet.isEmpty()) {
                for (String str : jSet) {
                    for (attackVertex v : this.g.vertexs) {
                        if (v.name.equals(str)) {
                            attackEdge edge = new attackEdge(v, dest, cause);
                            this.g.edges.add(edge);
                        }
                    }
                }
            } else {
                for (String str : this.context.attacker.compromiseElements) {
                    if (collection.nSet.get(str.toLowerCase()).conSet.contains(s)) {
                        for (attackVertex v : this.g.vertexs) {
                            if (v.name.equals(str)) {
                                attackEdge edge = new attackEdge(v, dest, cause);
                                this.g.edges.add(edge);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            this.g.vertexs.add(dest);
            newCompromiseElems.add(s);

        }
    }

    public boolean attackUsingVulnerability(String s, Set<String> newCompromiseElems) {
        G collection = this.context.conBuilder.g;
        vulnerablityCollection vulSet = this.context.vulSet;
        if (!vulSet.vulSet.containsKey(s)) {
            return false;
        }

        // check exploit condition

        // check require elem
        List<vulnerability> vlist = vulSet.vulSet.get(s);
        String cause = "";
        boolean newSpec = false;
        boolean isTakeOver = false;
        for (vulnerability vul : vlist) {
            boolean canUsed = true;
            for (String elem : vul.RequiredElements) {
                if (!this.context.attacker.compromiseElements.contains(elem.toLowerCase())) {
                    canUsed = false;
                    break;
                }
            }

            for (user_specification u : vul.RequiredPrivileges) {
                boolean hasSpec = false;
                for (user_specification spec : this.context.attacker.privileges) {
                    if (u.role.equals(spec.role)) {
                        hasSpec = true;
                        break;
                    }
                }

                if (!hasSpec) {
                    canUsed = false;
                    break;
                }
            }

            if (vul.AttackVector.toLowerCase().equals("local")) {
                // find if attack compromised the node app is deployed
                String deployElem = null;
                for (String str : collection.nSet.get(s).conSet) {
                    if (collection.nSet.get(str).archType == "node") {
                        deployElem = str;
                    }
                }
                if (deployElem != null && !this.context.attacker.compromiseElements.contains(deployElem)) {
                    canUsed = false;
                }
            }

            if (canUsed) {
                if (vul.IsTakeOver == true)
                    isTakeOver = true;
                cause += vul.CVEID;
            }

            for (user_specification u : vul.GainedPrivileges) {
                boolean contains = false;
                for (user_specification spec : this.context.attacker.privileges) {
                    if (u.role.equals(spec.role))
                        contains = true;
                }
                if (!contains) {
                    newSpec = true;
                    break;
                }
            }
            if (newSpec)
                this.context.attacker.privileges.addAll(vul.GainedPrivileges);
        }
        if (cause.equals("")) {
            return false;
        } else if (!newSpec && isTakeOver == false) {
            return false;
        } else if (newSpec && isTakeOver == false) {
            return true;
        } else {

            attackVertex dest = new attackVertex(collection.nSet.get(s).type, s, collection.nSet.get(s).archType);
            this.g.vertexs.add(dest);
            for (String str : this.context.attacker.compromiseElements) {
                if (collection.nSet.get(str.toLowerCase()).conSet.contains(s)) {
                    for (attackVertex v : this.g.vertexs) {
                        if (v.name.equals(str.toLowerCase())) {
                            attackEdge edge = new attackEdge(v, dest, cause);
                            this.g.edges.add(edge);
                            break;
                        }
                    }
                    break;
                }
            }
            newCompromiseElems.add(s);
            return true;
        }

    }

    public void drawGraph() throws Exception {
        this.g.drawGraph();
    }

}
