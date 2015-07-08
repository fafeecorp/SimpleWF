package simplewf;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */

public class Attrib{
    private String fieldName;
    private String type;
    private String value;
    
    private Param Source;
        
    public Attrib(){}
    public Attrib(String fieldName){ 
        this.fieldName = fieldName; 
        }
    public Attrib(String fieldName, String type){
        this.fieldName = fieldName;
        this.type = type;
    }
    
    public Attrib(Attrib o){
        this.fieldName = o.getFieldName();
        this.type = o.getType();
        this.value = o.getValue();
        this.Source = o.getSource();
    }
    
    @Override
    public String toString(){
        return "Attribute: "+ this.fieldName + "," + this.type + "," + this.value;
    }
    
    //GETTER
    public String getFieldName(){
        return this.fieldName;
    }
    public String getType(){
        return this.type;
    }
    public String getValue(){
        return this.value;
    }
    public Param getSource(){
        return this.Source;
    }
    
    //SETTER
    public void setFieldName(String fieldName){
        this.fieldName = fieldName;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setValue(String value){
        this.value = value;
    }
    public void setSource(Param Source){
        this.Source = Source;
    }
}
