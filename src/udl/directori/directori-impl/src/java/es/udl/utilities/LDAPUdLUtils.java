package es.udl.utilities.ldap;

import javax.naming.*;
import javax.naming.directory.*;

import java.io.*;
import java.util.*;

import es.udl.utilities.jcrypt.*;
import es.udl.utilities.lmhash.*;

/*This class was developed by Fermin Molina (ASIC) and adapted for sakai use*/

public class LDAPUdLUtils
{
	// Config

	private String ldapserver_rw      = "";            // per escriure
	private String ldapserver_ro      = "";            // per llegir nom�s
	private String securityPrincipal  = "";				//user administrador 
	private String securityCredentials= "";				//passwd del usuari administrador
	
	// end Config

	
	
	/*Main getter and Setters*/

	public String getLdapserver_ro() {
		return ldapserver_ro;
	}

	public void setLdapserver_ro(String ldapserver_ro) {
		this.ldapserver_ro = ldapserver_ro;
	}

	public String getLdapserver_rw() {
		return ldapserver_rw;
	}

	public void setLdapserver_rw(String ldapserver_rw) {
		this.ldapserver_rw = ldapserver_rw;
	}

	public String getSecurityCredentials() {
		return securityCredentials;
	}

	public void setSecurityCredentials(String securityCredentials) {
		this.securityCredentials = securityCredentials;
	}

	public String getSecurityPrincipal() {
		return securityPrincipal;
	}

	public void setSecurityPrincipal(String securityPrincipal) {
		this.securityPrincipal = securityPrincipal;
	}

	/**
	 * Canvia password al LDAP.
	 */
	public int canviar_passwd(String login, String passwd, String base) throws Exception
	{
		Attribute  classes;
		Attributes ats;

		NamingEnumeration results = searchLDAP("(uid="+login+")", base);
		if(results == null)  // no l'ha trobat
			return 1;

		if(results.hasMore())
                {
                	SearchResult sr  = (SearchResult) results.next();
                        ats = sr.getAttributes();
                        classes = (Attribute)ats.get("objectClass");

                        if(classes == null)
                        	return 2;
                }
		else
		{
			return 3;
		}

		// Hi ha mes d'un resultat... no pot ser!
		if(results.hasMore())
			return 4;

		String gecos       = (String)((Attribute)ats.get("gecos")).get();
		String uidNumber   = (String)((Attribute)ats.get("uidnumber")).get();
		String gidNumber   = (String)((Attribute)ats.get("gidnumber")).get();

		Random randGenerator = new Random(System.currentTimeMillis());
		String salt = randomChars(2, 0, 0, true, true, null, randGenerator);
		String cryptPasswd = JCrypt.crypt(salt, passwd);

		int uidn = Integer.parseInt(uidNumber);
		int gidn = Integer.parseInt(gidNumber);

		int urid   = (uidn*2)+1000;
                int grid   = (gidn*2)+1001;
                String Sid;

                if(base.indexOf("alumnes") != -1)
            	        Sid = "S-1-5-21-1568183986-798173397-850986457-";
            	else
            		Sid = "S-1-5-21-282834553-3996377918-1595070095-";

		String lmp = LMHash.toHexString(LMHash.lmHash(passwd), false);
		String ntp = LMHash.toHexString(LMHash.ntlmHash(passwd), false);


		if(!classes.contains("sambaSamAccount")) // si no t� objecte samba, afegir-lo
		{
			Attributes attrs = new BasicAttributes(false);
			attrs.put("objectclass",          "sambaSamAccount");
			attrs.put("sambapwdlastset",      "1010179230");
			attrs.put("sambaacctflags",       "[UX         ]");
			attrs.put("sambasid",             Sid+urid);
			attrs.put("sambaprimarygroupsid", Sid+grid);
			attrs.put("sambalmpassword",      "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			attrs.put("sambantpassword",      "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			if(!modifyLDAP("uid="+login+","+base, attrs, DirContext.ADD_ATTRIBUTE))
				return 5;

			attrs = new BasicAttributes(false);
			attrs.put("displayname", gecos);
			if(!modifyLDAP("uid="+login+","+base, attrs, DirContext.REPLACE_ATTRIBUTE))
                    		return 6;
		}

		Attributes attrs = new BasicAttributes(false);
		attrs.put("userPassword",    "{crypt}"+cryptPasswd);
		attrs.put("sambalmpassword", lmp);
		attrs.put("sambantpassword", ntp);
		if(!modifyLDAP("uid="+login+","+base, attrs, DirContext.REPLACE_ATTRIBUTE))
                        return 7;

		return 0;
	}


     /**
       * Bloqueja compte LDAP.
       */
	
	public int bloqueja_compte(String login, String passwd, String base) throws Exception
        {
                Attribute  classes;

                NamingEnumeration results = searchLDAP("(uid="+login+")", base);
                if(results == null)  // no l'ha trobat
                        return 1;

                if(results.hasMore())
                {
                        SearchResult sr  = (SearchResult) results.next();
                        Attributes ats = sr.getAttributes();
                        classes = (Attribute)ats.get("objectClass");

                        if(classes == null)
                                return 2;
                }
                else
                {
                        return 3;
                }

                // Hi ha mes d'un resultat... no pot ser!
                if(results.hasMore())
                        return 4;

		Attributes attrs = new BasicAttributes(false);
		attrs.put("userPassword", "{crypt}*");

                if(classes.contains("sambaSamAccount")) // si t� objecte samba, treu LM & NT passwds
                {
                        attrs.put("sambalmpassword", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                        attrs.put("sambantpassword", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                }

                if(!modifyLDAP("uid="+login+","+base, attrs, DirContext.REPLACE_ATTRIBUTE))
                        return 5;

                return 0;
        }
                                 

	/**
	 *  Modifica els atributs donats a una entrada: Afegeix, canvia o esborra atribut
	 *  segons el par�metre "action", que ser�:
	 *		DirContext.ADD_ATTRIBUTE, REPLACE_ATTRIBUTE, REMOVE_ATTRIBUTE
	 *  respectivament.
	 */
	public boolean modifyLDAP(String dn, Attributes attrs, int action)
	{
		// Initial context implementation (LDAP)
		String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
		String MY_HOST = "ldap://"+ldapserver_rw;

		//Hashtable for environmental information
		Hashtable env = new Hashtable();

		//Specify which class to use for our JNDI filter
		env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
		env.put(Context.PROVIDER_URL, MY_HOST);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, securityCredentials);

		try {
			//Get a reference to a directory context
			DirContext ctx = new InitialDirContext(env);
			ctx.modifyAttributes(dn, action, attrs); // ADD_ATTRIBUTE, REPLACE_ATTRIBUTE, REMOVE_ATTRIBUTE
			ctx.close();
			return true;
		} catch(Exception e) {
			System.out.println("Excepci� modificant entrada LDAP ("+dn+"):\n"+e);
		}
		return false;
	}


        /**
         * Fa una cerca segons "filtre" i retorna el primer camp "field" especificat.
         * Retorna <null> si no troba res.
         */
	public String searchLDAPattribute(String filtre, String base, String field)
        {
                // Initial context implementation (LDAP)
                String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
                String MY_HOST = "ldap://"+ldapserver_ro;

                //Hashtable for environmental information
                Hashtable env = new Hashtable();

                //Specify which class to use for our JNDI filter
                env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
                env.put(Context.PROVIDER_URL, MY_HOST);
                env.put("java.naming.batchsize", "5000"); // Si no, pot petar per falta de memoria

                try {
                        //Get a reference to a directory context
                        DirContext ctx = new InitialDirContext(env);
                        SearchControls constraints = new SearchControls();
                        constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
                        NamingEnumeration results = ctx.search(base, filtre, constraints);

                        if(results != null && results.hasMore())
                        {
                                SearchResult sr  = (SearchResult) results.next();
                                Attributes attrs = sr.getAttributes();
                                Attribute  attr  = (Attribute)attrs.get(field);

                                if(attr != null)
                                        return (String)attr.get();
                        }
                        ctx.close();
                } catch(Exception e) {
                        System.out.println("Excepci� realitzant cerca:\n");
                        e.printStackTrace();
                }

                return null;
        }

        public boolean authenticateUser(String userLogin, String password,String base){
        
        Hashtable env = new Hashtable();
		InitialDirContext ctx;
		
		String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
        String MY_HOST = "ldap://"+ldapserver_ro;
		//
		String cn;
		boolean returnVal=false;	
		
		if (!password.equals("")){
			
			String[] returnAttribute = {"ou"};
	        SearchControls srchControls = new SearchControls();
	        srchControls.setReturningAttributes(returnAttribute);
	        srchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String searchFilter = "(&(objectclass=posixAccount)(uid="+userLogin+"))";
	       
			try{                    
					 env = new Hashtable();

		             //Specify which class to use for our JNDI filter
		             env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
		             env.put(Context.PROVIDER_URL, MY_HOST);
		             env.put("java.naming.batchsize", "5000");
		        	
		        	ctx = new InitialDirContext(env);
				    NamingEnumeration answer = ctx.search(base,searchFilter,srchControls);
				    
				    while (answer.hasMore() && returnVal==false){
	  				      	
	   				    SearchResult sr = (SearchResult)answer.next();
	   				    String dn=sr.getName().toString()+"," + base;
	
	   				    //Second binding
		                try{
		                	
		                	env.put(Context.SECURITY_AUTHENTICATION, "simple");
		                	env.put(Context.SECURITY_PRINCIPAL,sr.getName()+","+ base);
		                	env.put(Context.SECURITY_CREDENTIALS,password);
		                	
		                	try{ 
		                		DirContext authContext = new InitialDirContext(env);  
		                		returnVal=true;	
	                			authContext.close();
		                	}catch(AuthenticationException ae){	
		                			returnVal=false;
		                	}
						}catch (NamingException namEx){
							 returnVal=false;
							 namEx.printStackTrace();
		                }
				    }
				} 
				   
		        catch(NamingException namEx){
		        	returnVal = false;
		    	}//while
			}//if
		return returnVal;
        }
        
        
        
        /**
         * Fa una cerca segons "filtre" i retorna el result-set.
         * Retorna <null> si no troba res.
         */
        public NamingEnumeration searchLDAP(String filtre, String base){
        	return searchLDAP(filtre,base,false);
        }
        
        public NamingEnumeration searchLDAPManager(String filtre, String base){
        	return searchLDAP(filtre,base,true);
        }
                
        
        public NamingEnumeration searchLDAP(String filtre, String base,boolean isManager)
        {
                // Initial context implementation (LDAP)
                String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
                String MY_HOST = "ldap://"+ldapserver_ro;

                //Hashtable for environmental information
                Hashtable env = new Hashtable();

                //Specify which class to use for our JNDI filter
                env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
                env.put(Context.PROVIDER_URL, MY_HOST);
                
                if (isManager){
                	env.put(Context.SECURITY_AUTHENTICATION, "simple");
            		env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            		env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
                }
                
                env.put("java.naming.batchsize", "5000"); // Si no, pot petar per falta de memoria

                try {
                        //Get a reference to a directory context
                        DirContext ctx = new InitialDirContext(env);
                        SearchControls constraints = new SearchControls();
                        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
                        
                        NamingEnumeration results = ctx.search(base, filtre, constraints);
                        ctx.close();
                        return results;

                } catch(Exception e) {
                        System.out.println("Excepció realitzant cerca:\n");
                        e.printStackTrace();
                }

                return null;
        }

    /**
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     *
     * <p>If start and end are both <code>0</code>, start and end are set
     * to <code>' '</code> and <code>'z'</code>, the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * <code>false</code>, in which case, start and end are set to
     * <code>0</code> and <code>Integer.MAX_VALUE</code>.
     *
     * <p>If set is not <code>null</code>, characters between start and
     * end are chosen.</p>
     *
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single 
     * {@link Random} instance with a fixed seed and using it for each call,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.</p>
     *
     * @param count  the length of random string to create
     * @param start  the position in set of chars to start at
     * @param end  the position in set of chars to end before
     * @param letters  only allow letters?
     * @param numbers  only allow numbers?
     * @param chars  the set of chars to choose randoms from.
     *  If <code>null</code>, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *  <code>(end - start) + 1</code> characters in the set array.
     * @throws IllegalArgumentException if <code>count</code> &lt; 0.
     * @since 2.0
     */
        public String randomChars(int count, int start, int end, boolean letters, boolean numbers,
                                char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        StringBuffer buffer = new StringBuffer();
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if ((letters && numbers && Character.isLetterOrDigit(ch))
                || (letters && Character.isLetter(ch))
                || (numbers && Character.isDigit(ch))
                || (!letters && !numbers)) {
                buffer.append(ch);
            } else {
                count++;
            }
        }
        return buffer.toString();
    }
       
        public static String netejaString(String str)
    	{
    		String ret = new String("");

    		ret = killSpaces(str);
    		ret = ret.replaceAll("'",       "");
    		ret = ret.replaceAll("[ñ]",     "n");
    		ret = ret.replaceAll("[Ñ]",     "N");
    		ret = ret.replaceAll("[¥]",     "n");
    		ret = ret.replaceAll("[áàäâã]", "a");
    		ret = ret.replaceAll("[ÁÀÄÂÃ]", "A");
    		ret = ret.replaceAll("[éèëê]",  "e");
    		ret = ret.replaceAll("[ÉÈËÊ]",  "E");
    		ret = ret.replaceAll("[íìïî]",  "i");
    		ret = ret.replaceAll("[ÍÌÏÎ]",  "I");
    		ret = ret.replaceAll("[óòöôõ]", "o");
    		ret = ret.replaceAll("[ÓÒÖÔÕ]", "O");
    		ret = ret.replaceAll("[úùüû]",  "u");
    		ret = ret.replaceAll("[ÚÙÜÛ]",  "U");
    		ret = ret.replaceAll("[ç]",     "c");
    		ret = ret.replaceAll("[Ç]",     "C");
    		ret = ret.replaceAll("[ýÿ]",    "y");
    		ret = ret.replaceAll("[Ý]",     "Y");
    		ret = ret.replaceAll("[ºª]",    ".");
    		ret = ret.replaceAll("·",       "");

    		return ret;
    	}
   
    	private static String killSpaces(String in)
    	{
    		return (in == null)?in:in.trim();
    	} 
}

