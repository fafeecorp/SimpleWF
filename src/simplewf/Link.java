package simplewf;

import java.util.Stack;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */

public class Link{
    private WFNode Target;
    private String Condition;
    private ConditionExpr Cond;
    //
   
    public Link(){}
    public Link(WFNode Target){ 
        this.Target = Target;
    }   
    public Link(WFNode Target, String Condition){ 
        this.Target = Target;
        this.Condition = Condition;
    }
    public Link(Link l){
        this.Target = l.getTarget();
        this.Condition = l.getCondition();
        this.Cond = l.getCondExpr(); 
    }
    
    public void addCond(ConditionExpr Cond){
        this.Cond = Cond; 
    }
    public boolean evalCond(docType Adatok){
        String[] tokens = this.Cond.getLengyelForma().split(" ");
        int n = tokens.length;
        int i = 0;
        for(int j = 0; j<n; j++){
            if( tokens[j].startsWith("#") ){
                tokens[j] = (String)tokens[j].subSequence(1, tokens[j].length());
                tokens[j] = "" + Adatok.getAttribs().get(Integer.valueOf(tokens[j]));
            }
        }
        Stack<String> S = new Stack<>();
        S.push(tokens[i]); i++;
        boolean rdy = false;
        while(rdy){
            if( ConditionExpr.isBinPred(S.peek()) ){
                switch (S.pop()){
                case "<":
                    if (Integer.valueOf(S.pop()) < Integer.valueOf(S.pop())) S.push("$T");
                    else S.push("$F");
                    break;
                case "<=":
                    if (Integer.valueOf(S.pop()) <= Integer.valueOf(S.pop())) S.push("$T");
                    else S.push("$F");
                    break;
                case ">":
                    if (Integer.valueOf(S.pop()) > Integer.valueOf(S.pop())) S.push("$T");
                    else S.push("$F");
                    break;
                case ">=":
                    if (Integer.valueOf(S.pop()) >= Integer.valueOf(S.pop())) S.push("$T");
                    else S.push("$F");
                    break;
                case "==":
                    if( ConditionExpr.isInteger(S.peek()) ){
                        if (Integer.valueOf(S.pop()) == Integer.valueOf(S.pop())) S.push("$T");
                        else S.push("$F");
                    }else{
                        if (S.pop().equals(S.pop()) ) S.push("$T");
                        else S.push("$F");
                    }
                    break;
                case "!=":
                    if( ConditionExpr.isInteger(S.peek()) ){
                        if (Integer.valueOf(S.pop()) != Integer.valueOf(S.pop())) S.push("$T");
                        else S.push("$F");
                    }else{
                        if (!S.pop().equals(S.pop()) ) S.push("$T");
                        else S.push("$F");
                    }
                    break;
                default:
                    System.out.println("Exception : bad binary predicate during evaluation");
                }
            }else if( ConditionExpr.isLogOp(S.peek()) ){
                String muvelet = S.pop();    
                boolean a, b;
                    switch (muvelet){
                    case "&":
                        if( "$T".equals(S.pop())) a = true;
                        else a = false;
                        if( "$T".equals(S.pop())) b = true;
                        else b = false;
                        if( a && b ) S.push("$T");
                        else S.push("$F");
                        break;
                    case "|":
                        if( "$T".equals(S.pop())) a = true;
                        else a = false;
                        if( "$T".equals(S.pop())) b = true;
                        else b = false;
                        if( a || b ) S.push("$T");
                        else S.push("$F");
                        break;
                    case "!":
                        if( "$T".equals(S.pop())) a = true;
                        else a = false;
                        if( !a ) S.push("$T");
                        else S.push("$F");
                        break;
                    default:
                        System.out.println("Exception : bad log op during evaluation");
                }
            }
            if (i < n) { S.push(tokens[i]); i++; }
            else{ rdy = true; }
        }
        if ( "$T".equals(S.peek()) ) return true;
        else return false;
    }
    
    @Override
    public String toString(){
        return "Target:"+ this.Target.toString() + "," + this.Condition;
    }

    //GETTER
    public WFNode getTarget(){
        return this.Target;
    }
    public String getCondition(){
        return this.Condition;
    }
    public ConditionExpr getCondExpr(){
        return this.Cond;
    }
    
    //SETTER
    public void setTarget(WFNode Target){
        this.Target = Target;
    }
    public void setCondition(String Condition){
        this.Condition = Condition;
    }
    public void setCondExpr(ConditionExpr Cond){
        this.Cond = Cond;
    }
}