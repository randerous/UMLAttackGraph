package attackGraph.attacker;

public class attack {
    public String CVEID;
    public String CWEID;
    public String getCVEID() {
        return CVEID;
    }
    public void setCVEID(String cVEID) {
        CVEID = cVEID;
    }
    public String getCWEID() {
        return CWEID;
    }
    public void setCWEID(String cWEID) {
        CWEID = cWEID;
    }
    public attack(String cVEID, String cWEID) {
        CVEID = cVEID;
        CWEID = cWEID;
    }

}
