package cat.udl.asic.datacollector.impl.component;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;


import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.DataSourceValueRow;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataSourceService;
import cat.udl.asic.jobs.SendEmail;

public class SchedulerDataSourceServiceImpl implements DataSourceService {

	private DeveloperHelperService developerHelperService=null;
	private static String DC_GROUP = "dataCollector";
	private static Log log = LogFactory
			.getLog(SchedulerDataSourceServiceImpl.class);

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
		SchedulerManager manager = (SchedulerManager) ComponentManager.get(SchedulerManager.class);
		
			String triggerId = key;
			 
			try{
				return manager.getScheduler().unscheduleJob(new TriggerKey (triggerId,DC_GROUP+":"+ currentSectrion.getId()+":" + dataSource.getId()));
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	}

	
	@Override
	public DataSourceValue getDataSourceValue(SectionInfo currentSection,
			DataSource dataSource) {
		DataSourceValue myScheduled = null;
		try{
		SchedulerManager manager = (SchedulerManager) ComponentManager.get(SchedulerManager.class);
		 Set<TriggerKey> keys = manager.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(DC_GROUP+":"+ currentSection.getId()+":" + dataSource.getId()));
		
		//String [] triggers = manager.getScheduler().getTriggerNames(DC_GROUP+":"+ currentSection.getId()+":" + dataSource.getId());
		
		Map valueRows = new LinkedHashMap(); 
		
		for (TriggerKey triggerKey : keys){
				Trigger t = manager.getScheduler().getTrigger(triggerKey);
				DataSourceValueRow dsvr = new DataSourceValueRow(triggerKey.getName());
				
				if (t!= null){
					long milistoExecute = t.getStartTime().getTime() - (new Date()).getTime();
					Map params = t.getJobDataMap();
					dsvr.addColumnValue("triggerId",triggerKey.getName());
					dsvr.addColumnValue("timeToExecute",milistoExecute);
					dsvr.addColumnValue("params",params);
					valueRows.put(triggerKey.getName(), dsvr);
				}
		}
			
			myScheduled=  new DataSourceValue(dataSource.getId(), currentSection.getId(), valueRows , currentSection.isUpdatable()
					&& dataSource.isUpdatable());
		}catch (Exception e) {
			e.printStackTrace();
		}
			
		return myScheduled;
	}
	
	private static Date schedule(SchedulerManager manager,String triggerId,String groupId, long millis, Map dataMap,String className) throws SchedulerException {
	     try { 
	    	 JobDetail detail = new JobDetailImpl("job-for:" + triggerId,  groupId, (Class <org.quartz.Job>)Class.forName(className));
	      
	      if (dataMap != null){
	    	  detail.getJobDataMap().putAll(dataMap);
	      }
	      
	      return manager.getScheduler().scheduleJob(detail, new SimpleTriggerImpl(
	         triggerId,groupId, new Date(System.currentTimeMillis() + millis)));
	     }catch (ClassNotFoundException cnfe){
	    	 throw new SchedulerException(className + " is an invalid Job class Name" );
	     }
	   }
	

	@Override
	public boolean saveDataSourceValue(SectionInfo currentSectrion,
			DataSource dataSource, DataSourceValue dataSourceValue){
		
	
		SchedulerManager manager = (SchedulerManager) ComponentManager.get(SchedulerManager.class);

		if (dataSourceValue.getId()==null){
			dataSourceValue.setId("");
	    }
		
		if ("".equals(dataSourceValue.getId()) || dataSource.getId().equals(dataSourceValue.getId())){
				String idCreated =IdManager.createUuid();
				dataSourceValue.setId(idCreated);
		}
		
		
		Map value = (Map) dataSourceValue.getValue();
		
				
		String triggerId = dataSourceValue.getId();
		 
		long millis =  new Long ("" +value.get("timeToExecute")).longValue();  
		Map params = (Map) value.get("params");
		
		try{
			Date mydate = schedule (manager,triggerId, DC_GROUP+":"+ currentSectrion.getId()+":" + dataSource.getId(),millis,params,dataSource.getTableSrc());
			
		}catch (SchedulerException e) {
				e.printStackTrace();
				return false;
		}
		return true;
	}
   
}
