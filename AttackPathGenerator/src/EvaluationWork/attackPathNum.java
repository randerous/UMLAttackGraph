package EvaluationWork;
import AttackPathGenerator.AttackPath;

import java.util.ArrayList;

public class attackPathNum {
	int attackPathNumber;
	
	public int getAttackPathNumber() {
        return attackPathNumber;
    }
	
	public void countInitialAttackPathNumber(AttackPath A){
        attackPathNumber = A.pathSet.size();
    }
}
