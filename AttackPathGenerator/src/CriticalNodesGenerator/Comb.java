package CriticalNodesGenerator;

import java.util.ArrayList;
import java.util.List;

public class Comb {
    public static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> ret = new ArrayList();
        List<Integer> lst = new ArrayList();
        helper(ret, lst, n, k);
        return ret;
    }

    static void helper(List<List<Integer>> ret, List<Integer> lst, int n, int k){
        if (k == 0) {
            ret.add(new ArrayList(lst));
            return;
        }
        int first = lst.size() == 0 ? 1 : lst.get(lst.size() - 1) + 1;
        int last = n - k + 1;

        for(int i = first; i <= last; i++){
            lst.add(i);
            helper(ret, lst, n, k - 1);
            lst.remove(lst.size() - 1);
        }
    }
}
