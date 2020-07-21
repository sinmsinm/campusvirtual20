package cat.udl.asic.cm.impl;

import java.util.Set;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;

public class SectionUdlCmImpl extends AbstractMembershipContainerUdlCmImpl
	implements Section {

	private static final long serialVersionUID = 1L;

	private String category;
	private Set meetings;
	private CourseOffering courseOffering;
	private String courseOfferingEid; // We keep this here to avoid lazy loading of the courseOffering
	private Section parent;
	private EnrollmentSet enrollmentSet;
    private Integer maxSize;
         
    
	public SectionUdlCmImpl() {}
	
    public SectionUdlCmImpl(String eid, String title, String description, String category, Section parent, CourseOffering courseOffering, EnrollmentSet enrollmentSet, Integer maxSize) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.category = category;
		this.parent = parent;
		this.courseOffering = courseOffering;
		if(courseOffering != null) {
			this.courseOfferingEid = courseOffering.getEid();
		}
		this.enrollmentSet = enrollmentSet;
                this.maxSize = maxSize;
	}

	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public CourseOffering getCourseOffering() {
		return courseOffering;
	}
	public String getCourseOfferingEid() {
		return courseOfferingEid;
	}
	public void setCourseOffering(CourseOffering courseOffering) {
		this.courseOffering = courseOffering;
		if(courseOffering == null) {
			this.courseOfferingEid = null;
		} else {
			this.courseOfferingEid = courseOffering.getEid(); // Make sure we update the cached eid
		}
	}
	public Section getParent() {
		return parent;
	}
	public void setParent(Section parent) {
		this.parent = parent;
	}
	public EnrollmentSet getEnrollmentSet() {
		return enrollmentSet;
	}
	public void setEnrollmentSet(EnrollmentSet enrollmentSet) {
		this.enrollmentSet = enrollmentSet;
	}
	public Set getMeetings() {
		return meetings;
	}
	public void setMeetings(Set meetings) {
		this.meetings = meetings;
	}
	public Integer getMaxSize() {
		return maxSize;
	}
        public void setMaxSize(Integer maxSize) {
	    this.maxSize = maxSize;
	}
}
