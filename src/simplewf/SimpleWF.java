package simplewf;
/**
 *
 * @author Farkas András
 * @co-author Kasnyik János
 */

public class SimpleWF {

    public static void main(String[] args) {
        
        workFlow WF = new workFlow();
        WF.loadFromXML("minta2.xml");
        //System.out.println("Yo dawg heard u like xml so we put some xml in your xml so you can xml while you xml");
        /*
        try{
            WF.veryLazyLoadFromXMLSavetoDB("minta2.xml");
        }
        catch(Exception e){
            System.out.println("Save to database error: " + e);
        }
        try{
            workFlow WF2 = new workFlow("out_db.xml");
            try{
                WF2.saveToXML("out_db.xml");
            }
            catch(Exception e){
                System.out.println("Save to xml error: " + e);
            }
        }catch(Exception e){
            System.out.println("Load from database error: " + e);
        }
        */
        System.out.println("Be vagyunk töltve, futhatna a program!");  
        System.out.println("Memoria tartalom:");
        WF.pasteWorkFlow();
        WF.saveToXML("out_mem.xml");
        
    }
    
}
