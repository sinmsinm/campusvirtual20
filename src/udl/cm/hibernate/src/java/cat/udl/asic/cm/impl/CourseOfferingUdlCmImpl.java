package cat.udl.asic.cm.impl;



import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseOffering;

public class CourseOfferingUdlCmImpl extends AbstractMembershipContainerUdlCmImpl
	implements CourseOffering{
	
	private static final long serialVersionUID = 1L;

	private String status;
	private CanonicalCourse canonicalCourse;
	private String canonicalCourseEid;
	private AcademicSession academicSession;
	//private CrossListingCmImpl crossListingCmImpl;
	private Set courseSets;
	private Date startDate;
	private Date endDate;

	/** A cache of courseSetEids */
	private Set courseSetEids;

	
	
	public CourseOfferingUdlCmImpl() {}
	
	public CourseOfferingUdlCmImpl(String eid, String title, String description,String status, AcademicSession academicSession, CanonicalCourse canonicalCourse, Date startDate, Date endDate) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.status = status;
		this.academicSession = academicSession;
		this.canonicalCourse = canonicalCourse;
		if(canonicalCourse == null) {
			this.canonicalCourseEid = null;
		} else {
			this.canonicalCourseEid = canonicalCourse.getEid();
		}
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Set getCourseSets() {
		return courseSets;
	}
	public void setCourseSets(Set courseSets) {
		this.courseSets = courseSets;

		// Update our cache of courseSetEids
		if(courseSets == null) {
			courseSetEids = new HashSet();
		} else {
			courseSetEids = new HashSet(courseSets.size());
			for(Iterator iter = courseSets.iterator(); iter.hasNext();) {
				CourseSetUdlCmImpl courseSet = (CourseSetUdlCmImpl)iter.next();
				courseSetEids.add(courseSet.getEid());
			}
		}
	}

	/*public CrossListingCmImpl getCrossListing() {
		return crossListingCmImpl;
	}
	public void setCrossListing(CrossListingCmImpl crossListingCmImpl) {
		this.crossListingCmImpl = crossListingCmImpl;
	}*/

	public CanonicalCourse getCanonicalCourse() {
		return canonicalCourse;
	}
	public void setCanonicalCourse(CanonicalCourse canonicalCourse) {
		this.canonicalCourse = canonicalCourse;
		if(canonicalCourse == null) {
			this.canonicalCourseEid = null;
		} else {
			this.canonicalCourseEid = canonicalCourse.getEid();
		}
	}
	
	public AcademicSession getAcademicSession() {
		return academicSession;
	}
	public void setAcademicSession(AcademicSession academicSession) {
		this.academicSession = academicSession;
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

	public String getCanonicalCourseEid() {
		return canonicalCourseEid;
	}

	public Set getCourseSetEids() {
		return courseSetEids;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
