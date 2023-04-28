package attackGraph.graph;

public class attackEdge {
   public attackVertex from;
   public attackVertex to;
   public String cause;
public attackEdge(attackVertex from, attackVertex to, String cause) {
    this.from = from;
    this.to = to;
    this.cause = cause;
}
public attackVertex getFrom() {
    return from;
}
public void setFrom(attackVertex from) {
    this.from = from;
}
public attackVertex getTo() {
    return to;
}
public void setTo(attackVertex to) {
    this.to = to;
}
public String getCause() {
    return cause;
}
public void setCause(String cause) {
    this.cause = cause;
}
}
