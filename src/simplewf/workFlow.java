package simplewf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//adatbázis csomag
import database.database;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author faaqadi
 * @co-author Kasnyik János
 */
public final class workFlow{
    private List<WFNode> Nds;
    private List<Constant> Cns;
    private List<docType> Dts;
    
    //private database D = new database();
    
    public workFlow(){ 
        Nds = new ArrayList<WFNode>(); 
        Cns = new ArrayList<Constant>(); 
        Dts = new ArrayList<docType>();
    }
    public void saveToXML(String fname){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Model");
            doc.appendChild(rootElement);
            // UserInput
            Element ui = doc.createElement("UserInput");
            ui.setAttribute("id", "ui");
            rootElement.appendChild(ui);
            // Constants
            Element cnsts = doc.createElement("Constants");
            for(int i = 0; i < Cns.size(); i++){
                Element akt = doc.createElement("Constant");
                akt.setAttribute("id", Cns.get(i).getID());
                akt.setAttribute("type", Cns.get(i).getType());
                akt.setAttribute("value", Cns.get(i).getValue() );
                cnsts.appendChild(akt);
            }
            rootElement.appendChild(cnsts);
            // DocTypes
            Element dctyps = doc.createElement("DocumentTypes");
            for(int i = 0; i < Dts.size(); i++){
                Element dctyp = doc.createElement("DocumentType");
                dctyp.setAttribute("id", Dts.get(i).getID());
                dctyp.setAttribute("name", Dts.get(i).getName());
                
                List<Attrib> A = Dts.get(i).getAttribs();
                for(int j = 0; j < A.size(); j++){
                    Element akt = doc.createElement("Attribute");
                    akt.setAttribute("name", A.get(j).getFieldName() );
                    akt.setAttribute("type", A.get(j).getType() );
                    dctyp.appendChild(akt);
                }
                dctyps.appendChild(dctyp);
            }
            rootElement.appendChild(dctyps);
            // Nodes
            Element nds = doc.createElement("Nodes");
            for(int i = 0; i < Nds.size(); i++){
                Element nd = doc.createElement("Node");
                nd.setAttribute("id", Nds.get(i).getID());
                nd.setAttribute("name", Nds.get(i).getName());
                nd.setAttribute("dt", Nds.get(i).getDocType().getID());
                
                List<Attrib> A =  Nds.get(i).getDocType().getAttribs();
                for(int j = 0; j < A.size(); j++){
                    Element akt = doc.createElement("Param");
                    //Param P = A.get(j).getSource();
                    if (A.get(j).getSource() instanceof userInput){ akt.setAttribute("source", "ui"); }
                    if (A.get(j).getSource() instanceof Constant){ akt.setAttribute("source", ((Constant)A.get(j).getSource()).getID() ); }
                    if (A.get(j).getSource() instanceof Ref){ akt.setAttribute("source", ((Ref)A.get(j).getSource()).getSrc().getID() );
                                                            akt.setAttribute("attrib", Integer.toString(((Ref)A.get(j).getSource()).getSrc().getDocType().getAttribIndex(
                                                            ((Ref)A.get(j).getSource()).getAttr()) +1) ); }
                    nd.appendChild(akt);
                }
                nds.appendChild(nd);
            }
            rootElement.appendChild(nds);
            // Links
            /* LENGYELFORMÁVAL
            Element lnks = doc.createElement("Links");
            for(int i = 0; i < Nds.size(); i++){
                Element lnk = doc.createElement("Link");
                lnk.setAttribute("source", "n" + i );
                for(int j = 0; j < Nds.get(i).Links.size(); j++){
                    lnk.setAttribute("target", Nds.get(i).Links.get(j).Target.ID );
                    lnk.setAttribute("condition", Nds.get(i).Links.get(j).Cond.lengyelForma );
                    lnks.appendChild(lnk);
                }
            }
            rootElement.appendChild(lnks);
            */
            // Links
            // *SZÉPEN*
            Element lnks = doc.createElement("Links");
            for(int i = 0; i < Nds.size(); i++){ 
                List<Link> Ls = Nds.get(i).getLinks();
                for(int j = 0; j < Ls.size(); j++){
                    Element lnk = doc.createElement("Link");
                    lnk.setAttribute("source", "n" + (i+1) );
                    lnk.setAttribute("target", Ls.get(j).getTarget().getID() );
                    String lf = Ls.get(j).getCondExpr().getLengyelForma();
                    lf = lf.trim();
                    if ( !lf.contains(" ") ){
                        //System.out.println("EGYELEMU");
                        switch (lf){
                                        case "$T":
                                            lnk.appendChild(doc.createElement("true"));
                                            break;
                                        case "$F":
                                            lnk.appendChild(doc.createElement("false"));
                                            break;
                        }
                        lnks.appendChild(lnk);
                    }else{
                        String[] tkns = lf.split(" ");
                        Stack<String> S = new Stack<>();
                        for(int k = 0; k < tkns.length ; k++){
                            S.push(tkns[k]);
                        }
                        Stack<Element> EleS = new Stack<>();
                        EleS.push(lnk);
                        Element temp;
                        int typ = 0;
                        while(!S.empty()){
                            String str = S.pop();
                            switch (str){
                                case "<":
                                    temp = doc.createElement("lt");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case "<=":
                                    temp = doc.createElement("lte");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case ">":
                                    temp = doc.createElement("gt");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case ">=":
                                    temp = doc.createElement("gte");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case "==":
                                    temp = doc.createElement("eq");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case "!=":
                                    temp = doc.createElement("neq");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case "&":
                                    temp = doc.createElement("and");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case "|":
                                    temp = doc.createElement("or");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 2;
                                    break;
                                case "!":
                                    temp = doc.createElement("not");
                                    EleS.peek().appendChild(temp);
                                    EleS.push(temp);
                                    typ = 1;
                                    break;
                                default:{
                                    for(int k = 1; k <= typ; k++){
                                        switch (str){
                                            case "$T":
                                                temp = doc.createElement("true");
                                                EleS.peek().appendChild(temp);
                                                break;
                                            case "$F":
                                                temp = doc.createElement("false");
                                                EleS.peek().appendChild(temp);
                                                break;
                                            default:
                                                if(str.startsWith("#")){
                                                    str = (String)str.subSequence(1, str.length());
                                                    temp = doc.createElement("var");
                                                    temp.setAttribute("attrib", str);
                                                    EleS.peek().appendChild(temp);
                                                }else{
                                                    temp = doc.createElement("const");
                                                    temp.setAttribute("value", str);
                                                    EleS.peek().appendChild(temp);
                                                }
                                        }
                                        if(k+1 <= typ) str = S.pop();
                                    }
                                    EleS.pop();
                                }
                            }
                        }
                    lnks.appendChild(lnk);
                    }
                }
            }
            rootElement.appendChild(lnks);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fname));
            transformer.transform(source, result);
            System.out.println("XML file saved!");
        } catch (ParserConfigurationException pce) {
		System.out.println("Save from XML parser error: " + pce);
	} catch (TransformerException tfe) {
		System.out.println("Save from XML transformer error: " + tfe);
	}
    }
    public void loadFromXML(String fname){
        try {
 
	File fXmlFile = new File(fname);
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
 
	doc.getDocumentElement().normalize();
 
	NodeList XMLConstants = doc.getElementsByTagName("Constant");
        NodeList XMLDocTypes = doc.getElementsByTagName("DocumentType");
        NodeList XMLWFNodes = doc.getElementsByTagName("Node");
        NodeList XMLLinks = doc.getElementsByTagName("Link");

        // CONSTANTS :::
        for (int i = 0; i < XMLConstants.getLength(); i++) {
            Node aktXMLnode = XMLConstants.item(i);
            Element aktXMLele = (Element) aktXMLnode;
            // Constant maga:
            Constant C = new Constant(aktXMLele.getAttribute("id"),aktXMLele.getAttribute("type"),aktXMLele.getAttribute("value"));
            this.Cns.add(C);
        }
        // DOCTYPES :::
        
        for (int i = 0; i < XMLDocTypes.getLength(); i++) {
            Node aktXMLnode = XMLDocTypes.item(i);
            Element aktXMLele = (Element) aktXMLnode;
            // DocType maga:
            docType akt = new docType( aktXMLele.getAttribute("id"),
                                       aktXMLele.getAttribute("name") );
            NodeList aktAttribs = aktXMLnode.getChildNodes();
            // hozzátartozó Rendezett N-es:
            for (int j = 0; j < aktAttribs.getLength(); j++){
                Node aktAttrib = aktAttribs.item(j);
                if (aktAttrib.getNodeType() == Node.ELEMENT_NODE) {
                    Element aktAttribEle = (Element) aktAttrib;
                    akt.addToAttribs( new Attrib(aktAttribEle.getAttribute("name"),aktAttribEle.getAttribute("type")) );
                }
            }
            this.Dts.add(akt);
        }
        // NODES :::
	for (int i = 0; i < XMLWFNodes.getLength(); i++) {
            Node aktXMLnode = XMLWFNodes.item(i);
            Element aktXMLele = (Element) aktXMLnode;
            // Node maga:
            WFNode akt = new WFNode(aktXMLele.getAttribute("id"),
                                    aktXMLele.getAttribute("name"));
            akt.setDocType(new docType(this.findDocType(aktXMLele.getAttribute("dt"))));

            
            NodeList aktParams = aktXMLnode.getChildNodes();
            // hozzátartozó Rendezett N-es:
            int k = 0;
            for (int j = 0; j < aktParams.getLength(); j++){
                Node aktParam = aktParams.item(j);
                if (aktParam.getNodeType() == Node.ELEMENT_NODE) {
                    Element aktParamEle = (Element) aktParam;
                    String S = aktParamEle.getAttribute("source");
                    Attrib A = akt.getDocType().getAttribs().get(k);
                    if("ui".equals(S)){
                        A.setSource(new userInput());
                    }
                    else if(S.startsWith("c")){
                        A.setSource(this.findConstant(S));
                    }
                    else if(S.startsWith("n")){
                        A.setSource(new Ref(this.findNode(S),this.findNode(S).findAttribute(
                                        Integer.parseInt(aktParamEle.getAttribute("attrib"))-1) ));
                    }
                    k++;
                }
            }
            this.Nds.add(akt);
	}
        
        // LINKS :::
        for (int i = 0; i < XMLLinks.getLength(); i++) {
            Node aktXMLnode = XMLLinks.item(i);
            Element aktXMLele = (Element) aktXMLnode;
            int wi = 0;
            char type = 's';
            // Link maga:
            Link akt = new Link ( this.findNode(aktXMLele.getAttribute("target")) );
            Stack<String> S = new Stack<>();
            Stack<Node> NLS = new Stack<>();
            NodeList templist = aktXMLnode.getChildNodes();
            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
            NLS.push( templist.item(wi) );
            //System.out.println("i = " + i);
            while (!NLS.empty()){
                //System.out.println("not_empty - WHILE fut");
                //if ( NLS.peek() != null )System.out.println( NLS.peek().getNodeName() );
                if (type == 'h'){ // h = HASONLÍTÁS -- kell két <var> v <const>, más meg tilos
                    templist = NLS.peek().getChildNodes();
                    wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                    NLS.push( templist.item(wi) );
                    switch (NLS.peek().getNodeName()){
                        case "var":{
                            Element temp = (Element) NLS.peek();
                            S.push("#" + temp.getAttribute("attrib"));
                            break;
                        }
                        case "const":{
                            Element temp = (Element) NLS.peek();
                            S.push(temp.getAttribute("value"));
                            break;
                        }
                    }
                    NLS.push( NLS.pop().getNextSibling() );
                    NLS.push( NLS.pop().getNextSibling() );
                    switch (NLS.peek().getNodeName()){
                        case "var":{
                            Element temp = (Element) NLS.peek();
                            S.push("#" + temp.getAttribute("attrib"));
                            break;
                        }
                        case "const":{
                            Element temp = (Element) NLS.peek();
                            S.push(temp.getAttribute("value"));
                            break;
                        }
                        default: System.out.println("HALÁLOS EXCEPTION TÖRTÉNEDETT!!! (nem <var> vagy <const> jött)");
                    }
                    type = 's';
                    NLS.pop();
                    if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                    //if ( NLS.peek() != null )System.out.println( "H VISSZAALLT S-RE ES :: " + NLS.peek().getNodeName() );
                }
                if( NLS.peek() != null && NLS.peek().getNodeType() == Node.ELEMENT_NODE ){
                    switch (NLS.peek().getNodeName()){
                        case "true":
                            S.push("$T");
                            NLS.pop();
                            if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                            break;
                        case "false":
                            S.push("$F");
                            NLS.pop();
                            if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                            break;
                        case "not":
                            S.push("!");
                            templist = NLS.peek().getChildNodes();
                            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                            NLS.push( templist.item(wi) );
                            break;
                        case "and":
                            S.push("&");
                            templist = NLS.peek().getChildNodes();
                            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                            NLS.push( templist.item(wi) );
                            break;
                        case "or":
                            S.push("|");
                            templist = NLS.peek().getChildNodes();
                            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                            NLS.push( templist.item(wi) );
                            break;
                        case "lt":
                            type = 'h';
                            S.push("<");
                            break;
                        case "lte":
                            type = 'h';
                            S.push("<=");
                            break;
                        case "gt":
                            type = 'h';
                            S.push(">");
                            break;
                        case "gte":
                            type = 'h';
                            S.push(">=");
                            break;
                        case "eq":
                            type = 'h';
                            S.push("==");
                            break;
                        case "neq":
                            type = 'h';
                            S.push("!=");
                            break;
                    }
                }else{
                    NLS.pop();
                    if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                }
            }
            ConditionExpr uj = new ConditionExpr();
            String LF = new String();
            //uj.lengyelForma = "";
            while (!S.empty()){
                //uj.lengyelForma = uj.lengyelForma + S.pop() + " ";
                //LF = LF + S.pop() + " ";
                LF = LF.concat(S.pop() + " ");
            }
            //uj.lengyelForma = uj.lengyelForma.trim();
            LF = LF.trim();
            uj.setLengyelForma(LF);
            akt.setCondExpr(uj);
            akt.setCondition(LF);
            //Link akt = new Link( this.findNode(aktXMLele.getAttribute("target")), aktXMLele.getAttribute("condition") );
            // Csatolás:
            this.findNode(aktXMLele.getAttribute("source")).addToLinks(akt);
           /* List<Link> Ls = this.findNode(aktXMLele.getAttribute("source")).getLinks();
            Ls.add(akt);
            this.findNode(aktXMLele.getAttribute("source")).setLinks(Ls);*/
            //this.findNode(aktXMLele.getAttribute("source")).Links.add(akt);
        }
        
        } catch (Exception e) {
            System.out.println("Load from XML error: " + e);
        }
        
        System.out.println("Loaded from XML!");
    }   
    
    public void pasteWorkFlow(){
        for(int i = 0; i < Nds.size(); i++){
            System.out.println("{Node name: " + Nds.get(i).getName());
            System.out.println(" DocType: " + Nds.get(i).getDocType().getName());
            System.out.println(" Attributes:");
            List<Attrib> attrs = Nds.get(i).getDocType().getAttribs();
            for(int j = 0; j < attrs.size(); j++){
                String src = "";
                if (attrs.get(j).getSource() instanceof userInput){ src = "User Input"; }
                if (attrs.get(j).getSource() instanceof Constant){ src = "Constant ID: " + ((Constant)attrs.get(j).getSource()).getID(); }
                if (attrs.get(j).getSource() instanceof Ref){ src = "Load from Node: " + ((Ref)attrs.get(j).getSource()).getSrc().getName() + " - Attribute: " + 
                    ((Ref)attrs.get(j).getSource()).getAttr().getFieldName(); }
                System.out.println(" - (Name: " + attrs.get(j).getFieldName() + " ) - (Source: " + src + " )");
            }
            System.out.println(" Links: ");
            List<Link> lnks = Nds.get(i).getLinks();
            for(int j = 0; j < lnks.size(); j++){
                System.out.println(" - To: " + lnks.get(j).getTarget().getName()+ " if: " + lnks.get(j).getCondExpr().getLengyelForma() );
            }
            System.out.println("}");
        }
    }
    
    public void veryLazyLoadFromXMLSavetoDB(String fname){
        try {
 
	File fXmlFile = new File(fname);
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
 
	doc.getDocumentElement().normalize();
 
	NodeList XMLConstants = doc.getElementsByTagName("Constant");
        NodeList XMLDocTypes = doc.getElementsByTagName("DocumentType");
        NodeList XMLWFNodes = doc.getElementsByTagName("Node");
        NodeList XMLLinks = doc.getElementsByTagName("Link");

        //database
        database D = new database();
        D.deleteTables();
        D.createTables();
        int index;       
        // CONSTANTS :::
        for (int i = 0; i < XMLConstants.getLength(); i++) {
            Node aktXMLnode = XMLConstants.item(i);
            Element aktXMLele = (Element) aktXMLnode;

            D.insertInConstTable(aktXMLele.getAttribute("id"), aktXMLele.getAttribute("type"), aktXMLele.getAttribute("value"));
            
        }
        // DOCTYPES :::
        index=1;
        for (int i = 0; i < XMLDocTypes.getLength(); i++) {
            Node aktXMLnode = XMLDocTypes.item(i);
            Element aktXMLele = (Element) aktXMLnode;

            String aktDocID = aktXMLele.getAttribute("id");
            D.insertInDocTypeTable(aktXMLele.getAttribute("id"), aktXMLele.getAttribute("name"));

            NodeList aktAttribs = aktXMLnode.getChildNodes();
            // hozzátartozó Rendezett N-es:
            for (int j = 0; j < aktAttribs.getLength(); j++){
                Node aktAttrib = aktAttribs.item(j);
                if (aktAttrib.getNodeType() == Node.ELEMENT_NODE) {
                    Element aktAttribEle = (Element) aktAttrib;
                    
                    D.insertInAttribTable(index,aktAttribEle.getAttribute("name"), aktAttribEle.getAttribute("type"), aktDocID);
                    index++;
                }
            }

        }
        // NODES :::
        index=1;
	for (int i = 0; i < XMLWFNodes.getLength(); i++) {
            Node aktXMLnode = XMLWFNodes.item(i);
            Element aktXMLele = (Element) aktXMLnode;
            // Node maga:
            
            String ID = aktXMLele.getAttribute("id");       
            D.insertInNodeTable(ID, aktXMLele.getAttribute("name"), aktXMLele.getAttribute("dt"));
            
// TODO :: MIVAN HA NINCS
            
            NodeList aktParams = aktXMLnode.getChildNodes();
            // hozzátartozó Rendezett N-es:
            //int k = 0;
            for (int j = 0; j < aktParams.getLength(); j++){
                Node aktParam = aktParams.item(j);
                if (aktParam.getNodeType() == Node.ELEMENT_NODE) {
                    Element aktParamEle = (Element) aktParam;
                    String S = aktParamEle.getAttribute("source");
                    if( "ui".equals(S) ){
                        //ui param adatbázisba
                        D.insertInParamTable(index, ID , S, -1);
                        index++;
                    }
                    else if ( S.startsWith("c") ){
                        //const param adatbázisba
                        D.insertInParamTable(index, ID , S, -1);
                        index++;
                    }
                    else if ( S.startsWith("n") ){
                        //ref param adatbázisba
                        D.insertInParamTable(index, ID , S, Integer.parseInt(aktParamEle.getAttribute("attrib")));
                        index++;
                    }                       
                    //k++;
                }
            }
	}
        
        // LINKS :::
        for (int i = 0; i < XMLLinks.getLength(); i++) {
            Node aktXMLnode = XMLLinks.item(i);
            Element aktXMLele = (Element) aktXMLnode;
            int wi = 0;
            char type = 's';
            // Link maga:
            //Link akt = new Link ( this.findNode(aktXMLele.getAttribute("target")) );
            Stack<String> S = new Stack<>();
            Stack<Node> NLS = new Stack<>();
            NodeList templist = aktXMLnode.getChildNodes();
            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
            NLS.push( templist.item(wi) );
            while (!NLS.empty()){
                //System.out.println("not_empty - WHILE fut");
                //if ( NLS.peek() != null )System.out.println( NLS.peek().getNodeName() );
                if (type == 'h'){ // h = HASONLÍTÁS -- kell két <var> v <const>, más meg tilos
                    templist = NLS.peek().getChildNodes();
                    wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                    NLS.push( templist.item(wi) );
                    switch (NLS.peek().getNodeName()){
                        case "var":{
                            Element temp = (Element) NLS.peek();
                            S.push("#" + temp.getAttribute("attrib"));
                            break;
                        }
                        case "const":{
                            Element temp = (Element) NLS.peek();
                            S.push(temp.getAttribute("value"));
                            break;
                        }
                    }
                    NLS.push( NLS.pop().getNextSibling() );
                    NLS.push( NLS.pop().getNextSibling() );
                    switch (NLS.peek().getNodeName()){
                        case "var":{
                            Element temp = (Element) NLS.peek();
                            S.push("#" + temp.getAttribute("attrib"));
                            break;
                        }
                        case "const":{
                            Element temp = (Element) NLS.peek();
                            S.push(temp.getAttribute("value"));
                            break;
                        }
                        default: System.out.println("HALÁLOS EXCEPTION TÖRTÉNEDETT!!! (nem <var> vagy <const> jött)");
                    }
                    type = 's';
                    NLS.pop();
                    if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                    //if ( NLS.peek() != null )System.out.println( "H VISSZAALLT S-RE ES :: " + NLS.peek().getNodeName() );
                }
                if( NLS.peek() != null && NLS.peek().getNodeType() == Node.ELEMENT_NODE ){
                    switch (NLS.peek().getNodeName()){
                        case "true":
                            S.push("$T");
                            NLS.pop();
                            if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                            break;
                        case "false":
                            S.push("$F");
                            NLS.pop();
                            if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                            break;
                        case "not":
                            S.push("!");
                            templist = NLS.peek().getChildNodes();
                            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                            NLS.push( templist.item(wi) );
                            break;
                        case "and":
                            S.push("&");
                            templist = NLS.peek().getChildNodes();
                            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                            NLS.push( templist.item(wi) );
                            break;
                        case "or":
                            S.push("|");
                            templist = NLS.peek().getChildNodes();
                            wi = 0; while ( templist.item(wi).getNodeType() != Node.ELEMENT_NODE ) { wi++; }
                            NLS.push( templist.item(wi) );
                            break;
                        case "lt":
                            type = 'h';
                            S.push("<");
                            break;
                        case "lte":
                            type = 'h';
                            S.push("<=");
                            break;
                        case "gt":
                            type = 'h';
                            S.push(">");
                            break;
                        case "gte":
                            type = 'h';
                            S.push(">=");
                            break;
                        case "eq":
                            type = 'h';
                            S.push("==");
                            break;
                        case "neq":
                            type = 'h';
                            S.push("!=");
                            break;
                    }
                }else{
                    NLS.pop();
                    if(!NLS.empty()) { NLS.push( NLS.pop().getNextSibling() ); NLS.push( NLS.pop().getNextSibling() ); }
                }
            }

            String lengyelForma = new String();
            while (!S.empty()){
                lengyelForma = lengyelForma.concat(S.pop() + " ");
            }
            lengyelForma = lengyelForma.trim();

            D.insertInLinkTable(i+1, aktXMLele.getAttribute("target"), aktXMLele.getAttribute("source"), lengyelForma);            
            }
        } catch (Exception e) {
            System.out.println("Save to database error: " + e);
        }
        
        System.out.println("Loaded from XML and saved to database!");
    }
    
    //LOAD FROM DATABASE TO MEMORY
    public workFlow(String fname) throws SQLException, ClassNotFoundException{
        this.Nds = new ArrayList<>(); 
        this.Cns = new ArrayList<>(); 
        this.Dts = new ArrayList<>();
        
        database D = new database();
        
        List<String> consts = D.readConstTableToArray();
        Iterator<String> constIterator = consts.iterator();
        while (constIterator.hasNext()) {
            String[] splited = constIterator.next().split("\t");
            Constant C = new Constant(splited[0],splited[1],splited[2]);
            this.Cns.add(C);
        }
            
        //doctype olvasása adatbázisból
        List<String> docs = D.readDocTypeTabletoArray();
        Iterator<String> docsIterator = docs.iterator();
        while (docsIterator.hasNext()) {
	    String[] splited = docsIterator.next().split("\t");
            docType DOC = new docType( splited[0], splited[1]);
            this.Dts.add(DOC);
        }
            
            //attribútumok olvasása
        List<String> attribs = D.readAttribTableToArray();
        Iterator<String> attribsIterator = attribs.iterator();
        while (attribsIterator.hasNext()) {
            String[] splited = attribsIterator.next().split("\t");
            Attrib A = new Attrib(splited[1],splited[2]);
            docType dt = this.findDocType(splited[3]);
            dt.addToAttribs(A);
        }
        //nodeok olvasása
        List<String> nodes = D.readNodeTableToArray();
        Iterator<String> nodesIterator = nodes.iterator();
        while (nodesIterator.hasNext()) {      
            String[] splited = nodesIterator.next().split("\t");
            WFNode WFN = new WFNode(splited[0],splited[1]);
            WFN.setDocType(new docType(this.findDocType(splited[2])));                
            int k = 0;
            //paraméterek olvasása
            List<String> params = D.readParamTableToArray();
            Iterator<String> paramsIterator = params.iterator();
            while (paramsIterator.hasNext()) {  
                String[] splited2 = paramsIterator.next().split("\t");
                List<Attrib> Attribs = WFN.getDocType().getAttribs();
                if(splited2[1].equals(splited[0])){
                    if ("ui".equals(splited2[2])){
                        Attribs.get(k).setSource(new userInput());
                        //WFN.dType.Attribs.get(k).Source = new userInput();
                    }
                    else if(splited2[2].startsWith("c")){
                        Attribs.get(k).setSource(this.findConstant(splited2[2]));
                        //WFN.dType.Attribs.get(k).Source = this.findConstant(splited2[2]);        
                    }        
                    else if(splited2[2].startsWith("n")){
                        Attribs.get(k).setSource( new Ref(this.findNode(splited2[2]), this.findNode(splited2[2]).findAttribute(
                            Integer.parseInt(splited2[3])-1)) );                       
                       /* WFN.dType.Attribs.get(k).Source = 
                            new Ref(this.findNode(splited2[2]), this.findNode(splited2[2]).findAttribute(
                            Integer.parseInt(splited2[3])-1) );*/
                    }
                    k++;    
                }  
            }  
            this.Nds.add(WFN);
            }  
            //linkek olvasása
            List<String> links = D.readLinkTableToArray();
            Iterator<String> linksIterator = links.iterator();
            while (linksIterator.hasNext()) {
                String[] splited = linksIterator.next().split("\t");
		Link L = new Link( this.findNode(splited[1]), splited[3]);
                L.addCond(new ConditionExpr());
                splited[3] = splited[3].trim();
                L.getCondExpr().setLengyelForma(splited[3]);
                
                this.findNode(splited[2]).addToLinks(L);
                /*L.Cond = new ConditionExpr();
                L.Cond.lengyelForma = splited[3];
                L.Cond.lengyelForma = L.Cond.lengyelForma.trim();*/               
                
                //this.findNode(splited[2]).Links.add(L);               
            }   
         /* //Ellenőrző eljárások      
            //CONST IN MEMORY
            List<Constant> cs = this.Cns;
            Iterator<Constant> csIt = cs.iterator();
            while (csIt.hasNext()) {
		System.out.println(csIt.next().toString());
            }
            //DOCTYPE IN MEMORY
            List<docType> ds = this.Dts;
            Iterator<docType> dsIt = ds.iterator();
            while (dsIt.hasNext()) {
                docType curr = dsIt.next();
                System.out.println(curr.toString());
                List<Attrib> as = curr.Attribs;
                Iterator asIt = as.iterator();
                while (asIt.hasNext()) {
                    System.out.println(asIt.next().toString());
                }
            }
            //NODES IN MEMORY
            List<WFNode> abcdef = this.Nds;
            Iterator<WFNode> nsIt = abcdef.iterator();
            while(nsIt.hasNext()){
                WFNode curr = nsIt.next();
                System.out.println(curr.toString());
                List<Link> ls = curr.Links;
                Iterator lsIt = ls.iterator();
                while (lsIt.hasNext()) {
                    System.out.println("Links: " + lsIt.next().toString());
                }  
                List<Attrib> atr = curr.dType.Attribs;
                Iterator atrIt = atr.iterator();
                while (atrIt.hasNext()) {
                    Attrib currattr = (Attrib) atrIt.next();
                    System.out.println(currattr.Source.toString());
                }  
            } */
    }
    
    public WFNode findNode(String id){
        int wi = 0;
        boolean l = false;
        while ( wi < Nds.size() && !l ){
            if( id.equals(Nds.get(wi).getID()) ){
                l = true;
            }
            else{ 
                wi++;
            }
        }
        return Nds.get(wi);
    }
    public Constant findConstant(String id){
        int wi = 0;
        boolean l = false;
        while ( wi < Cns.size() && !l ){
            if( id.equals(Cns.get(wi).getID()) ){
                l = true;
            }
            else{ 
                wi++; 
            }
        }
        return Cns.get(wi);
    }
    public docType findDocType(String id){
        int wi = 0;
        boolean l = false;
        while ( wi < Dts.size() && !l ){
            if( id.equals(Dts.get(wi).getID()) ){
                l = true;
            }
            else{ 
                wi++; 
            }
        }
        return Dts.get(wi);
    }
}
