package EvaluationWork;

import java.util.List;

import AttackPathGenerator.Node;

public class controlNodeNum {
	int controlNodeNumber;

    public int getControlNodeNumber() {
        return controlNodeNumber;
    }

    public void countControlNodeNumber(List<Node> i){
        controlNodeNumber = i.size();
    }
}
