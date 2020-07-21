package cat.udl.asic.cm.impl;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.coursemanagement.api.AcademicSession;

public class AcademicSessionUdlCmImpl implements AcademicSession, Serializable {
	
	private static final long serialVersionUID = 1L;

	private String eid;
	private String title;
	private String description;
	private Date startDate;
	private Date endDate;
	private boolean current;
	private Long key;
 
	public static final String AUTHORY = "MockUdl";
	
	public AcademicSessionUdlCmImpl() {}

	public AcademicSessionUdlCmImpl(String eid, String title, String description, Date startDate, Date endDate) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Long getKey() {
		return key;
	}
	public void setKey(Long key) {
		this.key = key;
	}
	
	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}

	public String getAuthority() {
		return AUTHORY;
	}

	public void setAuthority(String arg0) {
		throw new RuntimeException("You can not change the authority of this CM object.  Authority = " + AUTHORY);
	}


}
