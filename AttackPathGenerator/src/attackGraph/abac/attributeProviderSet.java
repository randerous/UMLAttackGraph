package attackGraph.abac;

import java.util.ArrayList;
import java.util.List;

public class attributeProviderSet {
    public List<attributeProvider> providers;

    public List<attributeProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<attributeProvider> providers) {
        this.providers = providers;
    }

    public attributeProviderSet() {
        this.providers = new ArrayList<attributeProvider>();
    }

}
