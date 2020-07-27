package cat.udl.asic.datacollector.impl.component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;


import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.DataSourceValueRow;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataSourceService;

public class AuthzDataSourceServiceImpl implements DataSourceService {

	private DeveloperHelperService developerHelperService=null;
	
	private static Log log = LogFactory
			.getLog(AuthzDataSourceServiceImpl.class);

	public DeveloperHelperService getDeveloperHelperService() {
		return developerHelperService;
	}

	public void setDeveloperHelperService(
		DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}
	
	@Override
	public boolean deleteDataSourceValue(SectionInfo currentSectrion,
			DataSource dataSource, String key) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public DataSourceValue getDataSourceValue(SectionInfo currentSection,
			DataSource dataSource) {
		
		DataSourceValue retDataSource = null;
		final String sectionId = currentSection.getId();

	String realmpath = dataSource.getTableSrc();
		boolean updatable = dataSource.isUpdatable();

		// It can be filtered by role or eid 
		String role = "";
		String eid = "";
		
		for (Parameter param : dataSource.getParameterList()) {
			if ("role".equals(param.getId())) {
				Parameter paramSec = currentSection.getParameter(param.getId());
				

				if (paramSec != null) {
					role = (String) paramSec.getValue();
				} else {// maybe has defaultValue
					role = (String) param.getValue();
				}

			} else if ("eid".equals(param.getId())) {
				Parameter paramSec = currentSection.getParameter(param.getId());

				if (paramSec != null) {
					eid = (String) paramSec.getValue();
				} else {// maybe has defaultValue
					eid = (String) param.getValue();
				}
			}
		}

		// On that point we got the realm path and filtered params if exists
		// Lets start with query

		AuthzGroupService azGroupService = (AuthzGroupService) ComponentManager
				.get("org.sakaiproject.authz.api.AuthzGroupService");
		Map realmPath = new LinkedHashMap();

		if (azGroupService != null) {

			List<AuthzGroup> matchAuthzGroups = azGroupService.getAuthzGroups(
					realmpath, null);
			for (AuthzGroup agroup : matchAuthzGroups) {
				Map valuesMap = new LinkedHashMap();
				String parsedid = agroup.getId().substring(1).replaceAll("/",":");
				DataSourceValueRow dsvr = new DataSourceValueRow(parsedid);
				
				for (Member member : agroup.getMembers()) {
					if ("".equals(role)) {
						if ("".equals(eid) || eid.equals(member.getUserEid())) {
							valuesMap.put(member.getUserEid(), member.getRole()
									.getId());
						}
					} else if (role.equals(member.getRole().getId())) {
						if ("".equals(eid) || eid.equals(member.getUserEid())) {
							valuesMap.put(member.getUserEid(), member.getRole()
									.getId());
						}
					}
				}
				if (valuesMap.size() > 0) {
					dsvr.setValue(valuesMap);
					realmPath.put(parsedid, dsvr);
				}
			}

		} else {
			Map valuesMap = new LinkedHashMap(); // Its linked for the order
			realmPath.put("simple.value", "value not found");
		}
		

		 retDataSource = new DataSourceValue(dataSource.getId(), sectionId,
				 realmPath, currentSection.isUpdatable()
						&& dataSource.isUpdatable());

		return retDataSource;
	}

	@Override
	public boolean saveDataSourceValue(SectionInfo currentSection,
			DataSource dataSource, DataSourceValue dataSourceValue){

		// TODO Auto-generated method stub
		String searchpath = dataSource.getTableSrc();
		
		AuthzGroupService azGroupService = (AuthzGroupService) ComponentManager.get("org.sakaiproject.authz.api.AuthzGroupService");
			// For each user 
			
			String realmPath = dataSourceValue.getId();
		
			realmPath = realmPath.replaceAll(":", "/");
			if (realmPath.startsWith(searchpath)) {
			
				AuthzGroup ag = null;
				Map users = (Map) dataSourceValue.getValue();
				
				try {
					ag = azGroupService.getAuthzGroup(realmPath);
				} catch (Exception e) {
					log.error("that realm no longer exists" + realmPath);
					return false;
				}
			
				boolean change = false;
				UserDirectoryService uds = (UserDirectoryService) ComponentManager.get("org.sakaiproject.user.api.UserDirectoryService");
				//Modify the groups
				for (Entry currentRow : (Set<Entry>) users.entrySet()) {
						String action = (String) currentRow.getValue();
						String userId;

						if (action.startsWith("grant:") || action.equals("revoke")){
							
							try {
								userId = uds.getUserId((String) currentRow.getKey());
							} catch (UserNotDefinedException e) {
								log.error("User with eid" + (String) currentRow.getKey() + " not found");
								continue;
							} 
						
						if (action.startsWith("grant:")){
							String[] actionrole = action.split(":");
							if (actionrole.length != 2){
								log.error("Error processig action role"
										+ action + " for realm" + realmPath);
								return false;
							}else{
								String role = actionrole[1];
								if(hasChanged(ag, userId, role, "grant") ){
									ag.addMember(userId, role,true, false);
									change = true;
								}

							}
						} else if (action.equals("revoke")){
							if(hasChanged(ag, userId, "", "revoke") ){
								ag.removeMember(userId);
								change = true;
							}
						}
					}
				}
				boolean restoreCurrentUser = false;
				try {
					if(change){
						if(!azGroupService.allowUpdate(realmPath)){
							developerHelperService.setCurrentUser("/user/admin");
							restoreCurrentUser = true;
						}
						azGroupService.save(ag);
					}
						
				} catch (GroupNotDefinedException e) {
						log.error("Group not defined" + realmPath);
						return false;
				} catch (AuthzPermissionException e) {
					log.error("Not permissions to update" + realmPath);
					return false;
				}finally{
					if(restoreCurrentUser)
						developerHelperService.restoreCurrentUser();
				}
			}
		return true;
	}

	private boolean hasChanged(AuthzGroup ag, String userId, String role, String action){

		if(action.equals("grant") && ag.hasRole(userId,role))
			return false;

		//si l'usuari existeix amb un rol diferent, si es grant cal fer update, si es revoke cal eliminar, hi ha canvi sempre...
		if(ag.getMember(userId) != null)
			return true;

		//user doesn't exists, we add new member
		if(action.equals("grant"))
			return true;

		//revoke of unexistent user
		return false;
	}
   
}
