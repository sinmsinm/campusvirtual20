package es.udl.asic.component.app.directori;

import es.udl.asic.api.app.directori.Directori;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.Enumeration;
import javax.naming.*;
import javax.naming.directory.*;
import es.udl.utilities.ldap.*;

import org.w3c.dom.*;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.commons.lang.WordUtils;


/**
 * 
 * @author Alex - ASIC
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class DirectoriImpl implements Directori{
    private String ldapHost = ""; //Adreça del servidor LDAP
    private int ldapPort = 389; //Port del servei LDAP
    private String basePath = ""; // Path inicil
    private Hashtable env = new Hashtable(); //Taula on carregarem els parametres del LDAP
    String id = null; //Id de l'usuari
    String firstName = null; // Primer cognom
    String lastName = null; // Segon Cognom
    String tlf = null; // Telefon
    String mobile = null; // Mobil
    String fax = null; //Fax
    String email = null; //Correy
    String local = null; // Altres parametres per un futur
    String webpersonal=null;//Web personal
    String prov = null;
    String posadd = null;
    String codpos = null;
    String roomNumber = null;
    List llistaCentres = null;
    String centre = "";
    
    
    public void init() {
    	
    }

    public void destroy() {
    }

    public List getLlistaUsuaris(String cerca){
    	List resultat = new ArrayList(); // Llista amb el resultat
        
        //Carreguem els parametres de LDAP a la taula hast
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getLdapHost() + ":" + getLdapPort());

        
        if (!cerca.equals("")){
        try {
          

            //Establim el filtre de busqueda
            String filter = "(&(objectclass=posixAccount)(gecos=*"+cerca+"*))";

            DirContext ctx = new InitialDirContext(env);

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            //Especifiquem quin son els parametres que requerim
            searchControls.setReturningAttributes(new String[] {
                    "uid"
                });

            //Llancem la peticio per a a que carregui a results el resultat
            NamingEnumeration results = ctx.search(getBasePath(), filter,
                    searchControls);

            
            while (results.hasMore()) {
                //System.out.println("Tinc resutat");
            	
            	//Carreguem els diferent valors a les variables globals
                SearchResult result = (SearchResult) results.next();
                String dn = result.getName().toString() + "," + getBasePath();
                Attributes attrs = ctx.getAttributes(dn);

                String identificador = attrs.get("uid").get().toString();
                resultat.add (identificador);
            }

            results.close();
            ctx.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            
            return null;
        }
        }
        
        return resultat;
    }
    
    
    /**
     * Aquest metode busca l'informació de l'usuari a LDAP
     * 
     */
    
    public boolean getUserInf(String id) { 
        boolean resultat = false; // Indica si s'ha trobat resultat
        
        //Carreguem els parametres de LDAP a la taula hast
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getLdapHost() + ":" + getLdapPort());


        try {
            env.put(Context.SECURITY_PRINCIPAL, "");
            env.put(Context.SECURITY_CREDENTIALS, "");

            //Establim el filtre de busqueda
            String filter = "(&(objectclass=posixAccount)(uid=" + id + "))";

            DirContext ctx = new InitialDirContext(env);

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            //Especifiquem quin son els parametres que requerim
            searchControls.setReturningAttributes(new String[] {
                    "uid", "givenName", "sn", "l", "st", "postalAddress",
                    "postalCode", "telephoneNumber","mobile","facsimileTelephoneNumber","roomNumber","ou","registeredAddress"
                });

            //Llancem la peticio per a a que carregui a results el resultat
            NamingEnumeration results = ctx.search(getBasePath(), filter,
                    searchControls);

            
            while (results.hasMore()) {
                //System.out.println("Tinc resutat");
            	
            	//Carreguem els diferent valors a les variables globals
                SearchResult result = (SearchResult) results.next();
                String dn = result.getName().toString() + "," + getBasePath();
                Attributes attrs = ctx.getAttributes(dn);
                this.id = attrs.get("uid").get().toString();

                String cn = attrs.get("cn").get().toString();
                
                /* Canvi per a que agafi ja que ara ja està be en el ldap*/
                //firstName = cn.substring(0, cn.indexOf(" "));
                //lastName = cn.substring(cn.indexOf(" "));
                
                try{
                	firstName = attrs.get("givenName").get().toString();
                }
                catch(Exception ex){
                	firstName = "";
                }
                
                try{
                	lastName = attrs.get("sn").get().toString();
                }
                catch(Exception ex){
                	lastName = "";
                }
                
                firstName = WordUtils.capitalize(firstName.toLowerCase());
        		lastName = WordUtils.capitalize(lastName.toLowerCase());
                
                
                try{
                	email = attrs.get("mail").get().toString();
                }catch(Exception ex){
                	email = "";
                }
 
                //Provem si aquests parametres existeixen, i si es aixi els assignem 
                try {
                    tlf = attrs.get("telephoneNumber").get().toString();
                } catch (Exception ex) {
                    tlf = "";
                }
                
                try {
                    fax = attrs.get("facsimileTelephoneNumber").get().toString();
                } catch (Exception ex) {
                    fax = "";
                }
                
                try{
                	mobile = attrs.get("mobile").get().toString();
                }catch(Exception ex){
                	mobile = "";
                }
                
                try {
                    centre = attrs.get("employeeType").get().toString();
                } catch (Exception ex) {
                    centre = "";
                }

                try {
                    local = attrs.get("l").get().toString();
                } catch (Exception ex) {
                    local = "";
                }

                try {
                    prov = attrs.get("st").get().toString();
                } catch (Exception ex) {
                    prov = "";
                }

                try {
                    posadd = attrs.get("postalAddress").get().toString();
                } catch (Exception ex) {
                    posadd = "";
                }

                try {
                    codpos = attrs.get("postalCode").get().toString();
                } catch (Exception ex) {
                    codpos = "";
                }

                try {
                    roomNumber = attrs.get("roomNumber").get().toString();
                } catch (Exception ex) {
                    roomNumber = "";
                }
                
                try {
                    webpersonal = attrs.get("registeredAddress").get().toString();
                } catch (Exception ex) {
                    webpersonal = "";
                }
                
                
                resultat = true;
            }

            results.close();
            ctx.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            
            return false;
        }

        return resultat;
    }


    //Getter i Setter de les variable globals
    public String getId() {
        return id;
    }

    public String getNom() {
        return firstName;
    }

    public String getCognoms() {
        return lastName;
    }

    public String getTlf() {
        return tlf;
    }
    
    public String getMobile() {
        return mobile;
    }    
    
    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getLocal() {
        return local;
    }

    public String getProv() {
        return prov;
    }

    public String getWebpersonal(){
    	return webpersonal;
    }
    
    public String getposadd() {
        return posadd;
    }

    public String getcodpos() {
        return codpos;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public String getCentre() {
        return centre;
    }
    
    public String getLdapHost() {
        return ldapHost;
    }
    public int getLdapPort() {
        return ldapPort;
    }
    public String getBasePath() {
        return basePath;
    }

    public void setLdapHost(String ldapHost) {
        this.ldapHost = ldapHost;
    }

    public void setLdapPort(int ldapPort) {
        this.ldapPort = ldapPort;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}

