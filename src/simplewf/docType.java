package simplewf;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author faaqadi
 * @co-author kajtaai
 */
public class docType{
    private String ID;
    private String Name;
    private List<Attrib> Attribs;
    //
    public docType(){
        this.Attribs = new ArrayList<>();
    }
    public docType(String ID, String Name){ 
        this.ID = ID; 
        this.Name = Name; 
        this.Attribs = new ArrayList<>();
    }
    public docType(docType o){
        this.ID = o.getID();
        this.Name = o.getName(); 
        Attribs = new ArrayList<>();
        for (Attrib Attrib : o.getAttribs()) {
            this.Attribs.add(new Attrib(Attrib));
        }
        //this.Attribs = o.getAttribs();
        
    }
    public int getAttribIndex(Attrib a){
        int i = 0;
        while (i<Attribs.size()){
            if( a == Attribs.get(i)) return i;
            i++;
        }
        return 1337;
    }
    @Override
    public String toString(){
        return "I'm a docType! " + this.ID + "," + this.Name ;
    }
    
    public void addToAttribs(Attrib attr){
        this.Attribs.add(attr);
    }
    
    //GETTER
    public String getID(){
        return this.ID;
    }
    public String getName(){
        return this.Name;
    }
    public List<Attrib> getAttribs(){
        return this.Attribs;
    }
    
    //SETTER
    public void setID(String ID){
        this.ID = ID;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    public void setAttribs(List<Attrib> Attribs){
        this.Attribs = Attribs;
    }
}
