package cat.udl.asic.jobs;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.component.cover.ServerConfigurationService;
import java.util.List;
import java.util.ArrayList ;
import java.util.Collection;
import java.util.Map;




public class SendEmail implements Job {

	private static final Log log = LogFactory.getLog(SendEmail.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
	    UserDirectoryService uds = (UserDirectoryService) ComponentManager.get(UserDirectoryService.class);
	    EmailService es = (EmailService) ComponentManager.get(EmailService.class);
	    log.debug("INICI EXECUCIO JOB abans del parametres");
	    Map params = jobExecutionContext.getJobDetail().getJobDataMap();
	    Collection <String> usersEids = new ArrayList <String> ();
	    Collection <String> addresses = new ArrayList <String> (); 
	    log.debug("INICI EXECUCIO JOB");
	    if (params != null){
	    	
	    	for (Entry <String,String>  currentEntry: (Set <Entry<String,String>>) params.entrySet()){
	    			String key = currentEntry.getKey();
	    			if (key.startsWith("toaddr.")){
	    				addresses.add (currentEntry.getValue());
	    			}else{
	    				if (key.startsWith("to.")){
	    					usersEids.add(currentEntry.getValue());
	    				}
	    			}
	    	}
	    	
	    	String subject = (String) params.get("subject");
	    	String message = (String) params.get("message");

System.out.println("Subject: " + subject);
System.out.println("Message: " + message);

	    	log.debug("NOU EMAIL");
	    	log.debug("Subject: " + subject);
	    	log.debug("Message: " + message);
	    	
	    	List additionalHeaders = new ArrayList();
    		additionalHeaders.add("Content-Type: text/html");
    		additionalHeaders.add("From: "+ServerConfigurationService.getString("ui.service")  +" <no-reply@"+ ServerConfigurationService.getServerName() + ">");
    		additionalHeaders.add("Subject: " + subject);
    		
   			for (String address: addresses){
   					log.debug ("Destinatari directe:" + address);
   					System.out.println("Destinatari directe:" + address);
   					es.send(ServerConfigurationService.getString("ui.service")  +" <no-reply@"+ ServerConfigurationService.getServerName() + ">", address, subject, message,null, null,
   						additionalHeaders);
   			}
   			
   			if (!usersEids.isEmpty()){
   		    	for (String userEid : usersEids) {
   		    	   log.debug("Destinatari des de user: " + userEid);
   		    	   System.out.println("Destinatari des de user: " + userEid);
   		    	}

   				Collection <User> users= uds.getUsersByEids(usersEids);
   				es.sendToUsers(users, additionalHeaders, message);
   			}

	    }

	}
}
