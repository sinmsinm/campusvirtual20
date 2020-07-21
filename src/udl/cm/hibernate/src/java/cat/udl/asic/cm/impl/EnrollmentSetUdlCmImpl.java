/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.2.x/cm-service/cm-impl/hibernate-impl/hibernate/src/java/org/sakaiproject/coursemanagement/impl/EnrollmentSetCmImpl.java $
 * $Id: EnrollmentSetCmImpl.java 59674 2009-04-03 23:05:58Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package cat.udl.asic.cm.impl;

import java.io.Serializable;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;

public class EnrollmentSetUdlCmImpl 
	implements EnrollmentSet, Serializable {

	private static final long serialVersionUID = 1L;

	private String eid;
	private String title;
	private String description;
	private String category;
	private String defaultEnrollmentCredits;
	private CourseOffering courseOffering;
	private Set officialInstructors;

	private Long key;

	public static final String AUTHORY = "MockUdl";
	
	public EnrollmentSetUdlCmImpl () {}
	
	public EnrollmentSetUdlCmImpl(String eid, String title, String description, String category,
			String defaultEnrollmentCredits, CourseOffering courseOffering, Set officialInstructors) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.category = category;
		this.defaultEnrollmentCredits = defaultEnrollmentCredits;
		this.courseOffering = courseOffering;
		this.officialInstructors = officialInstructors;
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
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public CourseOffering getCourseOffering() {
		return courseOffering;
	}

	public void setCourseOffering(CourseOffering courseOffering) {
		this.courseOffering = courseOffering;
	}

	public String getDefaultEnrollmentCredits() {
		return defaultEnrollmentCredits;
	}

	public void setDefaultEnrollmentCredits(String defaultEnrollmentCredits) {
		this.defaultEnrollmentCredits = defaultEnrollmentCredits;
	}

	public Set getOfficialInstructors() {
		return officialInstructors;
	}
	public void setOfficialInstructors(Set officialInstructors) {
		this.officialInstructors = officialInstructors;
	}
	public String getAuthority() {
		return AUTHORY;
	}

	public void setAuthority(String arg0) {
		throw new RuntimeException("You can not change the authority of this CM object.  Authority = " + AUTHORY);
	}

}
