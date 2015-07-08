package simplewf;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */
public class ConditionExpr {
    private String lengyelForma;
    
    public ConditionExpr(){}
    public ConditionExpr(String lengyelforma){
        this.lengyelForma = lengyelforma;
    }
    public ConditionExpr(ConditionExpr c){
        this.lengyelForma = c.getLengyelForma();
    }
    
    public static boolean isLogOp(String str){
        switch (str){
            case "&":
                return true;
            case "|":
                return true;
            case "!":
                return true;
            default:
                return false;
        }
    }
    public static boolean isBinPred(String str){
        switch (str){
            case "<":
                return true;
            case "<=":
                return true;
            case ">":
                return true;
            case ">=":
                return true;
            case "==":
                return true;
            case "!=":
                return true;
            default:
                return false;
        }
    }
    public static boolean isConst(String str){
        switch (str){
            case "$T":
                return true;
            case "$F":
                return true;
            default:
                return false;
        }
    }
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
    
    //GETTER
    public String getLengyelForma(){
        return this.lengyelForma;
    }
    
    //SETTER
    public void setLengyelForma(String lengyelforma){
        this.lengyelForma = lengyelforma;
    }
}
