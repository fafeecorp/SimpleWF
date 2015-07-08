package simplewf;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */
public class Ref extends Param{
    private WFNode src;
    private Attrib attr;
    //
    
    public Ref(){}
    public Ref(WFNode src, Attrib attr){ 
        this.src = src; 
        this.attr = attr; 
    }
    public Ref(Ref r){
        this.src = r.getSrc(); 
        this.attr = r.getAttr(); 
    }
    
    @Override
    public String toString(){
        return "I'm a reference! " + src.toString() + "," + attr.toString();
    }
    
    //GETTER
    public WFNode getSrc(){
        return this.src;
    }
    public Attrib getAttr(){
        return this.attr;
    }
    
    //SETTER
    public void setSrc(WFNode src){
        this.src = src;
    }
    public void setAttr(Attrib attr){
        this.attr = attr;
    }
}
