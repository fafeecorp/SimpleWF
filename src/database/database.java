package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Kasnyik János
 */
public class database {
    //private final String dbName = "ROOT";   
    private Connection con = null;  
    private static final String dbURL = "jdbc:derby://localhost:1527/test;create=true;user=root;password=root";
   
    public database() throws SQLException, ClassNotFoundException {
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        con = DriverManager.getConnection(dbURL);
    }
    
    //TÁBLÁK LÉRTEHOZÁSA
    public void createTables() throws SQLException {
        Statement stmt = null;
        //DocType tábla
        String createDocTypeTable =
            "CREATE TABLE " +
            "DOCTYPE " +
            "(DOC_ID varchar(20) NOT NULL, NAME varchar(45), PRIMARY KEY (DOC_ID))";
        //konstans tábla
        String createConstTable =
            "create table " +
            "CONSTANTS " +
            "(CONST_ID varchar(20) NOT NULL, " +
            "TYPE varchar(45) NOT NULL, " +   
            "VALUE varchar(45) , " +
            "PRIMARY KEY (CONST_ID))";
        //Node tábla
        String createNodeTable =
            "create table " +
            "NODE " +
            "(NODE_ID varchar(20) NOT NULL, " +
            "NAME varchar(30), " +
            "DOC_ID varchar(20) NOT NULL, " +
            "PRIMARY KEY (NODE_ID), " +
            "FOREIGN KEY (DOC_ID) REFERENCES " +
            "DOCTYPE (DOC_ID))";    
        //Link tábla
        String createLinkTable =
            "create table " +
            "LINK " +
            "(LINK_ID integer NOT NULL, " +
            "TARGET_NODE_ID varchar(20) NOT NULL, " +
            "SOURCE_NODE_ID varchar(20) NOT NULL, " +
            "CONDITION varchar(100) NOT NULL, " +
            "PRIMARY KEY (LINK_ID), " +
            "FOREIGN KEY (TARGET_NODE_ID) REFERENCES " +
            "NODE (NODE_ID), " +
            "FOREIGN KEY (SOURCE_NODE_ID) REFERENCES " +
            "NODE (NODE_ID))";
        //Attribútum tábla
        String createAttributeTable =
            "create table " +
            "ATTRIB " +
            "(ATTRIB_ID integer NOT NULL, " +
            "NAME varchar(45) NOT NULL, " +
            "TYPE varchar(45) NOT NULL, " +
            "DOC_ID varchar(20) NOT NULL, " +
            "PRIMARY KEY (ATTRIB_ID)," +
            "FOREIGN KEY (DOC_ID) REFERENCES " +
            "DOCTYPE (DOC_ID))";
        //Paraméter tábla
        String createParamTable =
            "create table " +
            "PARAM " +
            "(PAR_ID integer NOT NULL, " +
            "NODE_ID varchar(20) NOT NULL, " +
            "SOURCE varchar(45) NOT NULL, " +   
            "ATTRIB_ID integer, " +   
            "PRIMARY KEY (PAR_ID), "+
            "FOREIGN KEY (NODE_ID) REFERENCES " +
            "NODE (NODE_ID))";
        
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createDocTypeTable);
        } catch (SQLException e) {
            System.out.println("CREATE TABLE (DOCTYPE) "+ e.toString());
        } finally {
            try {
               stmt.executeUpdate(createNodeTable);
            } catch (SQLException e) {
               System.out.println("CREATE TABLE (NODE) "+ e.toString());
            } finally {
                try {
                    stmt.executeUpdate(createConstTable);
                } catch (SQLException e) {
                    System.out.println("CREATE TABLE (CONSTANTS) "+ e.toString());
                } finally {
                    try {
                        stmt.executeUpdate(createLinkTable);
                    } catch (SQLException e) {
                        System.out.println("CREATE TABLE (LINK) "+ e.toString());
                    } finally {
                        try {
                            stmt.executeUpdate(createAttributeTable);   
                        } catch (SQLException e) {
                            System.out.println("CREATE TABLE (ATTRIBUTE) "+ e.toString());
                        } finally {
                            try {
                               stmt.executeUpdate(createParamTable);
                            } catch (SQLException e) {
                               System.out.println("CREATE TABLE (PARAM) "+ e.toString());
                            } finally {
                                if (stmt != null) { stmt.close(); }
                            }
                        }
                    }
                }
            }
        }
    }
       
    //TÁBLÁK MEGNÉZÉSE
    public void viewDocTypeTable() throws SQLException{
        Statement stmt = null;
        String query_doctype =
            "select * from DOCTYPE";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_doctype);
            System.out.println("doctype");
            while (rs.next()) {
                String doc_id = rs.getString("DOC_ID");
                String name = rs.getString("NAME");
                System.out.println(doc_id + "\t" + name);
            }
        } catch (SQLException e ) {
            System.out.println("VIEW DOCTYPE TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    public void viewConstTable() throws SQLException{
        Statement stmt = null;
        String query_const =
            "select * from CONSTANTS";   
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_const);
            System.out.println("const");
            while (rs.next()) {
                String const_id = rs.getString("CONST_ID");
                String type = rs.getString("TYPE");
                String value = rs.getString("VALUE");
                System.out.println(const_id + "\t" + type + "\t" + value);
            }
        } catch (SQLException e ) {
            System.out.println("VIEW CONSTANTS TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    public void viewNodeTable() throws SQLException{
        Statement stmt = null;
        String query_node =
            "select * from NODE";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_node);
            System.out.println("node");
            while (rs.next()) {
                String node_id = rs.getString("NODE_ID");
                String name = rs.getString("NAME");
                String doc_id = rs.getString("DOC_ID");
                System.out.println(node_id + "\t" + name + "\t" + doc_id);
        }
        } catch (SQLException e ) {
            System.out.println("VIEW NODE TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    public void viewAtrribTable() throws SQLException{
        Statement stmt = null;
        String query_attrib =
            "select * from ATTRIB";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_attrib);
            System.out.println("attrib");
            while (rs.next()) {
                int attrib_id = rs.getInt("ATTRIB_ID");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String doc_id = rs.getString("DOC_ID");
                System.out.println(attrib_id + "\t" + name + "\t" + type + "\t" + doc_id);
            }
        } catch (SQLException e ) {
            System.out.println("VIEW ATTRIB TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    public void viewLinkTable() throws SQLException{
        Statement stmt = null;
        String query_link =
            "select * from LINK";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_link);
            System.out.println("link");
            while (rs.next()) {
                int link_id = rs.getInt("LINK_ID");
                String target_node_id = rs.getString("TARGET_NODE_ID");
                String source_node_id = rs.getString("SOURCE_NODE_ID");
                String condition = rs.getString("CONDITION");
                System.out.println(link_id + "\t" + target_node_id + "\t" + source_node_id + "\t" + condition);
            }
        } catch (SQLException e ) {
            System.out.println("VIEW LINK TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    public void viewParamTable() throws SQLException{
        Statement stmt = null;
        String query_param =
            "select * from PARAM";
        try {
            stmt = con.createStatement();
            ResultSet  rs = stmt.executeQuery(query_param);
            System.out.println("param");
            while (rs.next()) {
                int par_id = rs.getInt("PAR_ID");
                String node_id = rs.getString("NODE_ID");
                String source = rs.getString("SOURCE");
                int attrib_id = rs.getInt("ATTRIB_ID");
                System.out.println(par_id + "\t" + node_id + "\t" + source + "\t" + attrib_id );
            }
        } catch (SQLException e ) {
            System.out.println("VIEW PARAM TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    
    //TÁBLÁKBA BERAKÁS
    public void insertInConstTable(String id, String type,String value) throws SQLException {
        try (Statement stmt = con.createStatement()) {
          stmt.executeUpdate(
              "insert into " +
              "CONSTANTS " +
              "values( '" + id + "', '" + type + "', '" + value + "')");
        } catch (SQLException e) {
            System.out.println("INSERT_IN_CONST_TABLE: "+e);
        }
    }  
    public void insertInDocTypeTable(String id, String name) throws SQLException {
        try (Statement stmt = con.createStatement()) {
          stmt.executeUpdate(
              "insert into DOCTYPE " +
              "values('" + id + "', '" + name + "')");
        } catch (SQLException e) {
            System.out.println("INSERT_IN_DOCTYPE_TABLE: " + e);
        }
    }   
    public void insertInAttribTable(int attrib_id, String name, String type, String doc_id) throws SQLException {
        try (Statement stmt = con.createStatement()) {
          stmt.executeUpdate(
              "insert into ATTRIB " +
              "values(" + attrib_id + ", '"+ name + "', '" + type + "', '" + doc_id + "')");
        } catch (SQLException e) {
            System.out.println("INSERT_IN_ATTRIB_TABLE: " + e);
        }
    }  
    public void insertInNodeTable(String id, String name, String doc_id) throws SQLException {
        try (Statement stmt = con.createStatement()) {
          stmt.executeUpdate(
              "insert into NODE " +
              "values('" + id + "', '"+name + "', '" + doc_id + "')");
        } catch (SQLException e) {
            System.out.println("INSERT_IN_NODE_TABLE: " + e);
        }
    }
    public void insertInLinkTable(int link_id, String target_node_id,String source_node_id, String condition) throws SQLException {
        try (Statement stmt = con.createStatement()) {
          stmt.executeUpdate(
              "insert into LINK " +
              "values(" + link_id + ", '" + target_node_id + "', '" + source_node_id + "', '"+ condition + "')");
        } catch (SQLException e) {
            System.out.println("INSERT_IN_LINK_TABLE: " + e);
        }
    }      
    public void insertInParamTable(int par_id, String node_id, String source, int attrib_id) throws SQLException {
        try (Statement stmt = con.createStatement()) {
          stmt.executeUpdate(
              "insert into PARAM " +
              "values(" + par_id + ", '" + node_id + "', '" + source + "', " + attrib_id + ")");
        } catch (SQLException e) {
            System.out.println("INSERT_IN_PARAM_TABLE: " + e);
        }
    }
    
    //TÁBLÁK TÖRLÉSE
    public void deleteTables() throws SQLException {
        Statement stmt = null;
        //DocType tábla
        String deleteDocTypeTable =
            "DROP TABLE DOCTYPE";
        //konstans tábla
        String deleteConstTable =
            "DROP TABLE CONSTANTS";
        //Node tábla
        String deleteNodeTable =
            "DROP TABLE NODE";    
        //Link tábla
        String deleteLinkTable =
            "DROP TABLE LINK";
        //Attribútum tábla
        String deleteAttributeTable =
            "DROP TABLE ATTRIB";
        //Paraméter tábla
        String deleteParamTable =
            "DROP TABLE PARAM";
        
        try {
            stmt = con.createStatement();    
            stmt.executeUpdate(deleteParamTable);
            stmt.executeUpdate(deleteAttributeTable);
            stmt.executeUpdate(deleteLinkTable);
            stmt.executeUpdate(deleteConstTable);
            stmt.executeUpdate(deleteNodeTable);
            stmt.executeUpdate(deleteDocTypeTable);     
        } catch (SQLException e) {
            System.out.println("CREATE TABLE "+ e.toString());
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }
    
    //LEKÉRDEZÉSEK  
    public List<String> readDocTypeTabletoArray() throws SQLException{
        Statement stmt = null;
        String query_doctype =
            "select * from DOCTYPE";
        List<String> out = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_doctype);
            //System.out.println("doctype");
            while (rs.next()) {
                String doc_id = rs.getString("DOC_ID");
                String name = rs.getString("NAME");
                out.add(doc_id + "\t" + name);
                //System.out.println(doc_id + "\t" + name);
            }
        } catch (SQLException e ) {
            System.out.println("VIEW DOCTYPE TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return out;
    } 
    public List<String> readConstTableToArray() throws SQLException{
        Statement stmt = null;
        String query_const =
            "select * from CONSTANTS";   
        List<String> out = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_const);
            //System.out.println("const");
            while (rs.next()) {
                String const_id = rs.getString("CONST_ID");
                String type = rs.getString("TYPE");
                String value = rs.getString("VALUE");
                out.add(const_id + "\t" + type + "\t" + value);
                //System.out.println(const_id + "\t" + type + "\t" + value);
            }
        } catch (SQLException e ) {
            System.out.println("READ CONSTANTS TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return out;
    } 
    public List<String> readNodeTableToArray() throws SQLException{
        Statement stmt = null;
        String query_node =
            "select * from NODE";
        List<String> out = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_node);
            //System.out.println("node");
            while (rs.next()) {
                String node_id = rs.getString("NODE_ID");
                String name = rs.getString("NAME");
                String doc_id = rs.getString("DOC_ID");
                out.add(node_id + "\t" + name + "\t" + doc_id);
                //System.out.println(node_id + "\t" + name + "\t" + doc_id);
        }
        } catch (SQLException e ) {
            System.out.println("READ NODE TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return out;
    }
    public List<String> readAttribTableToArray() throws SQLException{
        Statement stmt = null;
        String query_attrib =
            "select * from ATTRIB";
        List<String> out = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_attrib);
            //System.out.println("attrib");
            while (rs.next()) {
                int attrib_id = rs.getInt("ATTRIB_ID");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String doc_id = rs.getString("DOC_ID");
                out.add(Integer.toString(attrib_id) + "\t" + name + "\t" + type + "\t" + doc_id);
                //System.out.println(attrib_id + "\t" + name + "\t" + type + "\t" + doc_id);
            }
        } catch (SQLException e ) {
            System.out.println("READ ATTRIB TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return out;
    }
    public List<String> readLinkTableToArray() throws SQLException{
        Statement stmt = null;
        String query_link =
            "select * from LINK";
        List<String> out = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query_link);
            //System.out.println("link");
            while (rs.next()) {
                int link_id = rs.getInt("LINK_ID");
                String target_node_id = rs.getString("TARGET_NODE_ID");
                String source_node_id = rs.getString("SOURCE_NODE_ID");
                String condition = rs.getString("CONDITION");
                out.add(Integer.toString(link_id) + "\t" + target_node_id + "\t" + source_node_id + "\t" + condition);
                //System.out.println(link_id + "\t" + target_node_id + "\t" + source_node_id + "\t" + condition);
            }
        } catch (SQLException e ) {
            System.out.println("READ LINK TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return out;
    }
    public List<String> readParamTableToArray() throws SQLException{
        Statement stmt = null;
        String query_param =
            "select * from PARAM";
        List<String> out = new ArrayList<>();
        try {
            stmt = con.createStatement();
            ResultSet  rs = stmt.executeQuery(query_param);
            //System.out.println("param");
            while (rs.next()) {
                int par_id = rs.getInt("PAR_ID");
                String node_id = rs.getString("NODE_ID");
                String source = rs.getString("SOURCE");
                int attrib_id = rs.getInt("ATTRIB_ID");
                out.add(Integer.toString(par_id) + "\t" + node_id + "\t" + source + "\t" + Integer.toString(attrib_id));
                //System.out.println(par_id + "\t" + node_id + "\t" + source + "\t" + attrib_id);
            }
        } catch (SQLException e ) {
            System.out.println("READ PARAM TABLE " + e);
        } finally {
            if (stmt != null) { stmt.close(); }
        }
        return out;
    }
}