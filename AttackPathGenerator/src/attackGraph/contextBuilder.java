package attackGraph;

import attackGraph.abac.attributeProvider;
import attackGraph.abac.attributeProviderSet;
import attackGraph.abac.policy;
import attackGraph.attacker.attacker;
import attackGraph.user_specification.user_specification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class contextBuilder {
    public attributeProviderSet attributeProviderSet;
    public policy policyset;
    public attacker attacker;

    public contextBuilder() {
    	parseAttribute();
    }
    public void addUser(String provider, String pname, JSONArray arr)
    {
        for(int i = 0; i < arr.size(); i++)
			{
        	 	JSONObject obj = arr.getJSONObject(i).getJSONObject(provider);
        	 	if(obj == null) return;
				String name = obj.getString(pname);
				
				attributeProvider attr = new attributeProvider(name);
				JSONArray users = arr.getJSONObject(i).getJSONObject(provider)
						.getJSONArray("UserSpecification");
				for(int j = 0; j < users.size(); j++) 
				{
					user_specification u = new user_specification(users.getJSONObject(j).getString("attributeValue"));
					attr.user_specifications.add(u);
				}
				
				this.attributeProviderSet.providers.add(attr); 	
			}
    }

    public void parseAttribute()
    {
        this.attributeProviderSet = new attributeProviderSet();
        String path = "src/configJson/attributeProviders.json"; 
//        String path = "src/configJson/1.json"; 
        String file;
		try {
			file = Files.readString(Paths.get(path));
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

    
}
