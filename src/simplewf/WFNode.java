package simplewf;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */
public class WFNode{
    private String ID;
    private String Name;
    private docType dType;
    private List<Link> Links;
    //
    public WFNode(){
        this.Links = new ArrayList<>();
    }
    public WFNode(String ID, String Name){ 
        this.ID = ID;
        this.Name = Name;
        this.Links = new ArrayList<>();
    }
    public WFNode(WFNode node){
        this.ID = node.getID();
        this.Name = node.getName();
        this.dType = node.getDocType();
        this.Links = node.getLinks();
    }
    
    public Attrib findAttribute(int i){ 
        return dType.getAttribs().get(i); 
    }
    
    @Override
    public String toString(){
        return this.ID + ", " + this.Name + ",DT: " + this.dType.toString();
    }
    
    public void addToLinks(Link link){
        this.Links.add(link);
    }
    
    //GETTER
    public String getID(){
        return this.ID;
    }
    public String getName(){
        return this.Name;
    }
    public docType getDocType(){
        return this.dType;
    }
    public List<Link> getLinks(){
        return this.Links;
    }
    
    //SETTER
    public void setID(String ID){
        this.ID = ID;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    public void setDocType(docType dType){
        this.dType = dType;
    }
    public void setLinks(List<Link> Links){
        this.Links = Links;
    }
}