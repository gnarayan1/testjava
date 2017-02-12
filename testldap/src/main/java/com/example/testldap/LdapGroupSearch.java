package com.example.testldap;

import java.text.MessageFormat;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapGroupSearch {

	public static final String CN = "cn";
	public static final String SEARCH_BY_MEMBER = "(member=uid={0})";
	static Logger logger = LoggerFactory.getLogger(LdapGroupSearch.class);
	

	private static NamingEnumeration executeSearch(DirContext ctx, int searchScope, String searchBase,
			String searchFilter, String[] attributes) throws NamingException {
		SearchControls searchCtls = new SearchControls();
		if (attributes != null) {
			searchCtls.setReturningAttributes(attributes);
		}

		searchCtls.setSearchScope(searchScope);
		NamingEnumeration result = ctx.search(searchBase, searchFilter, searchCtls);
		return result;
	}



	public static void main(String[] args) {
		
		//String username = "jdoe";
		String username = "admin";
		
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "password");
		DirContext ctx = null;
		String defaultSearchBase = "ou=cognizant,ou=system";
		String groupDistinguishedName = "ou=Group,ou=cognizant,ou=system";

		try {
			ctx = new InitialDirContext(env);

			NamingEnumeration ne = executeSearch(ctx, SearchControls.SUBTREE_SCOPE, groupDistinguishedName,
					MessageFormat.format(SEARCH_BY_MEMBER, new Object[] { username}),
					new String[] {  CN });
			
			while (ne.hasMoreElements()) {
				SearchResult sr = (SearchResult) ne.next();
				logger.info(sr.getAttributes().get(CN).toString());
			}

		} catch (AuthenticationException e) {
			logger.info(username + " is NOT authenticated");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

}