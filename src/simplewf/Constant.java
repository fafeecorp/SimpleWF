package simplewf;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */
public class Constant extends Param{
    private String ID;
    private String type;
    private String value;
    
    public Constant(){}
    public Constant(String ID, String type, String val){
        this.ID    = ID;
        this.type  = type;
        this.value = val;
    }
    public Constant(Constant c){
        this.ID    = c.getID();
        this.type  = c.getType();
        this.value = c.getValue();
    }
    
    @Override
    public String toString(){
        return "I'm a constant! " + this.ID + "," + this.type + "," + this.value;   
    }
    
    //GETTER
    public String getID(){
        return this.ID;
    }
    public String getType(){
        return this.type;
    }
    public String getValue(){
        return this.value;
    }
    
    //SETTER
    public void setID(String ID){
        this.ID = ID;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setValue(String value){
        this.value = value;
    }
}