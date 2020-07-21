package cat.udl.asic.cm.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;



public class UdlCourseManagementHibernateImpl extends HibernateDaoSupport implements CourseManagementService {
	private static final Log log = LogFactory.getLog(UdlCourseManagementHibernateImpl.class);
	
	public void init() {
		log.info("Initializing " + getClass().getName());	
	}

	public void destroy() {
		log.info("Destroying " + getClass().getName());
	}
	
	/**
	 * A generic approach to finding objects by their eid.  This is "coding by convention",
	 * since it expects the parameterized query to use "eid" as the single named parameter.
	 * 
	 * @param eid The eid of the object we're trying to load
	 * @param className The name of the class / interface we're looking for
	 * @return The object, if found
	 * @throws IdNotFoundException
	 */
	private Object getObjectByEid(final String eid, final String className) throws IdNotFoundException {
		
			HibernateCallback hc = session -> {
				StringBuilder hql = new StringBuilder();
				hql.append("from ").append(className).append(" as obj where obj.eid=:eid");
				Query q = session.createQuery(hql.toString());
				q.setParameter("eid", eid);
				Object result = q.uniqueResult();
				if(result == null) {
					throw new IdNotFoundException(eid, className);
				}
				return result;
		};
		return getHibernateTemplate().execute(hc);
	}	
	

	/* ACADEMIC SESSION */
	public boolean isAcademicSessionDefined(String eid) {
		return ((Number)getHibernateTemplate().findByNamedQueryAndNamedParam("isUdlAcademicSessionDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public List<AcademicSession> getAcademicSessions() {
		log.debug("getUdlAcademicSessions:");
		return (List<AcademicSession>) getHibernateTemplate().findByNamedQuery("findUdlAcademicSessions");
	}

	public List<AcademicSession> getCurrentAcademicSessions() {
		log.debug("getUdlCurrentAcademicSessions:");
		List las = (List<AcademicSession> ) getHibernateTemplate().findByNamedQuery("findUdlCurrentAcademicSessions");
		
		for(Iterator iter = las.iterator(); iter.hasNext();){
			AcademicSessionUdlCmImpl as = (AcademicSessionUdlCmImpl)iter.next();
			log.debug("ACADEMIC SESSION = " + as.getEid());			
		}
			
		return las;
	}
	
	public AcademicSession getAcademicSession(final String eid) throws IdNotFoundException {
		log.debug("getUdlAcademicSession: eid :" + eid);
		return (AcademicSession)getObjectByEid(eid, AcademicSessionUdlCmImpl.class.getName());
	}

	/* SECTION */
	public Section getSection(String eid) throws IdNotFoundException {
		log.debug("getSection: eid :" + eid);
		return (Section)getObjectByEid(eid, SectionUdlCmImpl.class.getName());
	}

	public boolean isSectionDefined(String eid) {
		log.debug("isSectionDefined: eid :" + eid);
		return ((Number)getHibernateTemplate().findByNamedQueryAndNamedParam("isUdlSectionDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public Set<Section> getSections(String courseOfferingEid) throws IdNotFoundException {
		CourseOffering courseOffering = getCourseOffering(courseOfferingEid);

		return new HashSet<Section>((List<Section>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlTopLevelSectionsInCourseOffering", "courseOffering", courseOffering));
	}
	
	public Set<Section> findInstructingSections(final String userId) {
		log.debug("findUdlInstructingSections: userId :" + userId);			
		
		Set results = new HashSet<Section>((List<Section>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlInstructingSections", "userId", userId));
		
		for(Iterator iter = results.iterator(); iter.hasNext();){
			SectionUdlCmImpl oa = (SectionUdlCmImpl)iter.next();
			log.debug("INSTRUCTING SECTIONS: " + oa.getEid());			
		}		
		return results;
	}
	
	public Set<Section> findInstructingSections(final String userId, final String academicSessionEid) {
		log.debug("findUdlInstructingSections: userId :" + userId + " academicSessionId" +academicSessionEid );
						HibernateCallback hc = session -> {
				Query q = session.getNamedQuery("findUdlInstructingSectionsByAcademicSession");
				q.setParameter("userId", userId);
				q.setParameter("academicSessionEid", academicSessionEid);
				return q.list();
		};
		return new HashSet<Section>((List<Section>) getHibernateTemplate().execute(hc));
	}
	
	public Map<String, String> findSectionRoles(final String userEid) {
		log.debug("findUdlSectionRoles: userEId :" + userEid);
		List results = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlSectionRoles", "userEid", userEid);
		Map<String, String> sectionRoleMap = new HashMap<String, String>();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			log.debug("SECTION:"+(String)oa[0]+"  ROL:"+(String)oa[1]);
			sectionRoleMap.put((String)oa[0], (String)oa[1]);
		}
		return sectionRoleMap;
	}

	public Map<String, String> findSectionRoles(final String userEid, final String academicSessionEid) {
		HibernateCallback hc = session -> {
			Query q = session.getNamedQuery("findUdlSectionRolesByAcademicSession");
			q.setParameter("userEid", userEid);
			q.setParameter("academicSessionEid", academicSessionEid);
			return q.list();
		};

		List<Object[]> results = new ArrayList<>((List<Object[]>) getHibernateTemplate().execute(hc));
		Map<String, String> sectionRoleMap = new HashMap<>();
		for(Object[] oa : results) {
			sectionRoleMap.put((String) oa[0], (String) oa[1]);
		}

		return sectionRoleMap;
	}
	
	public Set<Section> getChildSections(final String parentSectionEid) throws IdNotFoundException {
		if( !isSectionDefined(parentSectionEid)) {
			log.debug("Section "+parentSectionEid+" no definida");
			throw new IdNotFoundException(parentSectionEid, SectionUdlCmImpl.class.getName());		
		}
		return null;
		/*
		log.debug("Buscant filles de "+parentSectionEid);
		return new HashSet<Section>(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlChildSections", "parentEid", parentSectionEid));*/
	}


	/* SECTION CATEGORIES */
	public List<String> getSectionCategories() {
		log.debug("getSectionCategories()");
		
		return (List<String>) getHibernateTemplate().findByNamedQuery("findUdlSectionCategories");
		
	}
	
	public String getSectionCategoryDescription(String categoryCode) {
		log.debug("getSectionCategoryDescription(): "+categoryCode);
		if(categoryCode == null) {
			log.debug("*** CategoryDescription(): null");
			return null;
		}
		SectionCategory cat = (SectionCategory)getHibernateTemplate().get(SectionCategoryUdlCmImpl.class, categoryCode);
		if(cat == null) {
			log.debug("*** CategoryDescription(): null");
			return null;
		} else {
			log.debug("*** CategoryDescription(): "+cat.getCategoryDescription());
			return cat.getCategoryDescription();		
		}
	}


	/* COURSE OFFERING */	
	public boolean isCourseOfferingDefined(String eid) {
		return ((Number)getHibernateTemplate().findByNamedQueryAndNamedParam("isUdlCourseOfferingDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public CourseOffering getCourseOffering(String eid) throws IdNotFoundException {
		
		return (CourseOffering)getObjectByEid(eid, CourseOfferingUdlCmImpl.class.getName());
	}
	
	public Set<CourseOffering> findCourseOfferings(final String courseSetEid, final String academicSessionEid) throws IdNotFoundException {
			HibernateCallback hc = session -> {
				Query q = session.getNamedQuery("findUdlCourseOfferingsByCourseSetAndAcademicSession");
				q.setParameter("courseSetEid", courseSetEid);
				q.setParameter("academicSessionEid", academicSessionEid);
				return q.list();
		};
		return new HashSet<CourseOffering>((List<CourseOffering>)getHibernateTemplate().execute(hc));
	}

	public Map<String, String> findCourseOfferingRoles(final String userEid) {
		// Keep track of CourseOfferings that we've already queried
		Set<String> queriedCourseOfferingEids = new HashSet<String>();
		List results = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlCourseOfferingRoles", "userEid", userEid);
		Map<String, String> courseOfferingRoleMap = new HashMap<String, String>();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			courseOfferingRoleMap.put((String)oa[0], (String)oa[1]);
			queriedCourseOfferingEids.add((String)oa[0]);
		}
		return courseOfferingRoleMap;
	}

	public Set<Membership> getCourseOfferingMemberships(String courseOfferingEid) throws IdNotFoundException {
		return getMemberships((AbstractMembershipContainerUdlCmImpl)getCourseOffering(courseOfferingEid));
	}
	
	/* COURSE SET */
	public boolean isCourseSetDefined(String eid) {
		return ((Number)getHibernateTemplate().findByNamedQueryAndNamedParam("isUdlCourseSetDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public CourseSet getCourseSet(String eid) throws IdNotFoundException {
		return (CourseSet)getObjectByEid(eid, CourseSetUdlCmImpl.class.getName());
	}
	
	public Set<CourseSet> getCourseSets() {
		return new HashSet<CourseSet>((List<CourseSet>) getHibernateTemplate().findByNamedQuery("findUdlTopLevelCourseSets"));
	}
	
	public Set<CourseOffering> getCourseOfferingsInCourseSet(final String courseSetEid) throws IdNotFoundException {
		if( ! isCourseSetDefined(courseSetEid)) {
			throw new IdNotFoundException(courseSetEid, CourseOfferingUdlCmImpl.class.getName());
		}
		return ((CourseSetUdlCmImpl)getCourseSet(courseSetEid)).getCourseOfferings();
	}
	
	public List<CourseSet> findCourseSets(final String category) {
		return (List<CourseSet>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlCourseSetByCategory", "category", category);
	}

	public boolean isEmpty(final String courseSetEid) {
			HibernateCallback hc = session -> {
				Query q = session.getNamedQuery("findUdlNonEmptyCourseSet");
				q.setParameter("eid", courseSetEid);
				return Boolean.valueOf( ! q.iterate().hasNext());
		};
		return ((Boolean)getHibernateTemplate().execute(hc)).booleanValue();
	}

	public Map<String, String> findCourseSetRoles(final String userEid) {
		List results = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlCourseSetRoles", "userEid", userEid);
		Map<String, String> courseSetRoleMap = new HashMap<String, String>();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			courseSetRoleMap.put((String)oa[0], (String)oa[1]);
		}
		return courseSetRoleMap;
	}
	
	
	/* MEMBERSHIPS */
	public Set<Membership> getCourseSetMemberships(String courseSetEid) throws IdNotFoundException {
		return getMemberships((AbstractMembershipContainerUdlCmImpl)getCourseSet(courseSetEid));
	}
	
	public Set<Membership> getSectionMemberships(String sectionEid) throws IdNotFoundException {
		return getMemberships((AbstractMembershipContainerUdlCmImpl)getSection(sectionEid));
	}
	
	/**
	 * Gets the memberships for a membership container.  This query can not be
	 * performed using just the container's eid, since it may conflict with other kinds
	 * of objects with the same eid.
	 * 
	 * @param container
	 * @return
	 */
	private Set<Membership> getMemberships(final AbstractMembershipContainerUdlCmImpl container) {
		
		// This may be a dynamic proxy.  In that case, make sure we're using the class
		// that hibernate understands.
		final String className = Hibernate.getClass(container).getName();
		log.debug("getMemberships for : " +className+ " eid: " +container.getEid());
		
			HibernateCallback hc = session -> {
				StringBuilder sb = new StringBuilder("select mbr from MembershipUdlCmImpl as mbr, ");
					sb.append(className);
					sb.append(" as container where mbr.memberContainer=container ");
					sb.append("and container.eid=:eid");
				Query q = session.createQuery(sb.toString());
				q.setParameter("eid", container.getEid());
				log.debug("QUERY  getMemberships: " +q.list().toString());
				return q.list();
		};
			
		HashSet<Membership> membres = new HashSet<Membership>((List <Membership>)getHibernateTemplate().execute(hc));
				
		for(Iterator iter = membres.iterator(); iter.hasNext();) {	
			MembershipUdlCmImpl m = new MembershipUdlCmImpl();
			m = (MembershipUdlCmImpl)iter.next();
			log.debug("RESULT  getMemberships: " + m.getUserId() +" "+m.getRole()+" "+m.getMemberContainer());
		}
		return membres;
			
		//return new HashSet<Membership>(getHibernateTemplate().executeFind(hc));
	}

	/* ENROLLMENTS I ENROLLMENTSETS */
	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException {
		return (EnrollmentSet)getObjectByEid(eid, EnrollmentSetUdlCmImpl.class.getName());
	}

	public Set<EnrollmentSet> getEnrollmentSets(final String courseOfferingEid) throws IdNotFoundException {
		if(!isCourseOfferingDefined(courseOfferingEid)) {
			throw new IdNotFoundException(courseOfferingEid, CourseOfferingUdlCmImpl.class.getName());
		}
		return new HashSet<EnrollmentSet>((List <EnrollmentSet>)getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlEnrollmentSetsByCourseOffering", "courseOfferingEid", courseOfferingEid));
	}

	public Set<Enrollment> getEnrollments(final String enrollmentSetEid) throws IdNotFoundException {
		if( ! isEnrollmentSetDefined(enrollmentSetEid)) {
			throw new IdNotFoundException(enrollmentSetEid, EnrollmentSetUdlCmImpl.class.getName());
		}
		return new HashSet<Enrollment>((List <Enrollment>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlEnrollments", "enrollmentSetEid", enrollmentSetEid));
	}

	public boolean isEnrolled(final String userId, final Set<String> enrollmentSetEids) {

			HibernateCallback hc = session -> {
				Query q = session.getNamedQuery("countEnrollments");
				q.setParameter("userId", userId);
				q.setParameterList("enrollmentSetEids", enrollmentSetEids);
				return q.iterate().next();
		};
		int i = ((Number)getHibernateTemplate().execute(hc)).intValue();
		if(log.isDebugEnabled()) log.debug(userId + " is enrolled in " + i + " of these " + enrollmentSetEids.size() + " EnrollmentSets" );
		return i > 0;
	}

	public boolean isEnrolled(String userId, String enrollmentSetEid) {
		HashSet<String> enrollmentSetEids = new HashSet<String>();
		enrollmentSetEids.add(enrollmentSetEid);
		return isEnrolled(userId, enrollmentSetEids);
	}
	
	public Enrollment findEnrollment(final String userId, final String enrollmentSetEid) {
		if( ! isEnrollmentSetDefined(enrollmentSetEid)) {
			log.warn("Could not find an enrollment set with eid=" + enrollmentSetEid);
			return null;
		}
		HibernateCallback hc = session -> {
				Query q = session.getNamedQuery("findUdlEnrollment");
				q.setParameter("userId", userId);
				q.setParameter("enrollmentSetEid", enrollmentSetEid);
				return q.uniqueResult();
		};
		return (Enrollment)getHibernateTemplate().execute(hc);
	}
	
	public Set<String> getInstructorsOfRecordIds(String enrollmentSetEid) throws IdNotFoundException {
		EnrollmentSet es = getEnrollmentSet(enrollmentSetEid);
		return es.getOfficialInstructors();
	}


	public Set<EnrollmentSet> findCurrentlyEnrolledEnrollmentSets(final String userId) {
		return new HashSet<EnrollmentSet>((List <EnrollmentSet>) getHibernateTemplate().findByNamedQueryAndNamedParam("findUdlCurrentlyEnrolledEnrollmentSets", "userId", userId));
	}


	public Set<EnrollmentSet> findCurrentlyInstructingEnrollmentSets(final String userId) {
		return new HashSet<EnrollmentSet>((List <EnrollmentSet>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlCurrentlyInstructingEnrollmentSets", "userId", userId));
	}

	public Set<Section> findEnrolledSections(final String userId) {
		return new HashSet<Section>((List <Section>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findUdlEnrolledSections", "userId", userId));
	}
	
	public boolean isEnrollmentSetDefined(String eid) {
		return ((Number)getHibernateTemplate().findByNamedQueryAndNamedParam("isUdlEnrollmentSetDefined", "eid", eid).get(0)).intValue() == 1;
	}
	
	public Map<String, String> getEnrollmentStatusDescriptions(Locale locale) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("enrolled", "Enrolled");
		map.put("wait", "Waitlisted");
		return map;
	}

	public Map<String, String> getGradingSchemeDescriptions(Locale locale) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("standard", "Letter Grades");
		map.put("pnp", "Pass / Not Pass");
		return map;
	}

	public Map<String, String> getMembershipStatusDescriptions(Locale locale) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("member", "Member");
		map.put("guest", "Guest");
		return map;
	}
	
/*	public Set<CourseSet> getChildCourseSets(final String parentCourseSetEid) throws IdNotFoundException {
		// Ensure that the parent exists
		if(!isCourseSetDefined(parentCourseSetEid)) {
			throw new IdNotFoundException(parentCourseSetEid, CourseSetCmImpl.class.getName());
		}
		return new HashSet<CourseSet>(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findChildCourseSets", "parentEid", parentCourseSetEid));
	}

	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException {
		return (CanonicalCourse)getObjectByEid(eid, CanonicalCourseCmImpl.class.getName());
	}

	public Set<CanonicalCourse> getEquivalentCanonicalCourses(String canonicalCourseEid) {
		final CanonicalCourseCmImpl canonicalCourse = (CanonicalCourseCmImpl)getCanonicalCourse(canonicalCourseEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEquivalentCanonicalCourses");
				q.setParameter("crossListing", canonicalCourse.getCrossListing());
				q.setParameter("canonicalCourse", canonicalCourse);
				return q.list();
			}
		};
		return new HashSet<CanonicalCourse>(getHibernateTemplate().executeFind(hc));
	}

	public Set<CanonicalCourse> getCanonicalCourses(final String courseSetEid) throws IdNotFoundException {
		return ((CourseSetCmImpl)getCourseSet(courseSetEid)).getCanonicalCourses();
	}

	public Set<CourseOffering> getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		final CourseOfferingCmImpl courseOffering = (CourseOfferingCmImpl)getCourseOffering(courseOfferingEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEquivalentCourseOfferings");
				q.setParameter("crossListing", courseOffering.getCrossListing());
				q.setParameter("courseOffering", courseOffering);
				return q.list();
			}
		};
		return new HashSet<CourseOffering>(getHibernateTemplate().executeFind(hc));
	}

	
	public List<CourseOffering> findActiveCourseOfferingsInCanonicalCourse(
			String eid) {
		log.debug("findActiveCourseOfferingsInCanonicalCourse(eid");
		CanonicalCourse canonicalCourse = null;
		try {
			canonicalCourse = this.getCanonicalCourse(eid);
		}
		catch (IdNotFoundException e) {
			//its quite possible someone ask for a course that doesn't exits
			return new ArrayList<CourseOffering>();
		}
		
		List<CourseOffering> ret = new ArrayList<CourseOffering>(getHibernateTemplate().findByNamedQueryAndNamedParam("findActiveCourseOfferingsInCanonicalCourse", 
				"canonicalCourse", canonicalCourse));
		
		return ret;
	}

	public Set<CourseOffering> getCourseOfferingsInCanonicalCourse(final String canonicalCourseEid) throws IdNotFoundException {
		if(!isCanonicalCourseDefined(canonicalCourseEid)) {
			throw new IdNotFoundException(canonicalCourseEid, CanonicalCourseCmImpl.class.getName());
		}
		return new HashSet<CourseOffering>(getHibernateTemplate().findByNamedQueryAndNamedParam("findCourseOfferingsByCanonicalCourse", "canonicalCourseEid", canonicalCourseEid));
	}

	public boolean isCanonicalCourseDefined(String eid) {
		return ((Number)getHibernateTemplate().findByNamedQueryAndNamedParam("isCanonicalCourseDefined", "eid", eid).get(0)).intValue() == 1;
	}


	private static final int MAX_COURSE_OFFERINGS = 8;
	private static final int MAX_SECTIONS = 70;
*/

	@Override
	public List<CourseOffering> findActiveCourseOfferingsInCanonicalCourse(
			String eid) {
		log.debug("findActiveCourseOfferingsInCanonicalCourse: eid :" + eid);
		return null;
	}
/*
	@Override
	public Map<String, String> findCourseOfferingRoles(String userEid) {
		log.debug("findCourseOfferingRoles: userEid: " + userEid);
		Map<String, String> coRoles = new HashMap<String, String>();

		if ("guzman".equals(userEid)) {
			coRoles.put("CO-1", "CourseAdmin");
			coRoles.put("CO-2", "I");
			return coRoles;
		} else if ("harvey".equals("userEid")) {
			for (int i = 0; i < MAX_COURSE_OFFERINGS; i++) {
				coRoles.put("CO-" + i, "I");
			}
			return coRoles;
		}

		return null;
	} */


	@Override
	public CanonicalCourse getCanonicalCourse(String canonicalCourseEid)
			throws IdNotFoundException {
		log.debug("getCanonicalCourse: canonicalCourseEid: "
				+ canonicalCourseEid);
		throw new IdNotFoundException(canonicalCourseEid,
				CanonicalCourse.class.getName());
	}

	@Override
	public Set<CanonicalCourse> getCanonicalCourses(String courseSetEid)
			throws IdNotFoundException {
		log.debug("getCanonicalCourses: courseSetEid: " + courseSetEid);
		return null;
	}

	@Override
	public Set<CourseSet> getChildCourseSets(String parentCourseSetEid)
			throws IdNotFoundException {
		log.debug("getChildCourseSets: parentCourseSetEid: "
				+ parentCourseSetEid);
		throw new IdNotFoundException(parentCourseSetEid,
				CourseSet.class.getName());
	}


	@Override
	public Set<CourseOffering> getCourseOfferingsInCanonicalCourse(
			String canonicalCourseEid) throws IdNotFoundException {
		log.debug("getCourseOfferingsInCanonicalCourse: canonicalCourseEid: "
				+ canonicalCourseEid);
		throw new IdNotFoundException(canonicalCourseEid,
				CanonicalCourse.class.getName());
	}


	@Override
	public Set<CourseOffering> getEquivalentCourseOfferings(
			String courseOfferingEid) throws IdNotFoundException {
		log.debug("getEquivalentCourseOfferings: courseOfferingEid: "
				+ courseOfferingEid);
		return new HashSet<CourseOffering>();

		 //throw new IdNotFoundException(courseOfferingEid,
		 //CourseOffering.class.getName());
	}

	/*
	private Set<CourseSet> getCoursetsFromCourseOffering(String courseOffering) {

		Set<CourseSet> cslist = new HashSet<CourseSet>();

		if (courseOffering.equals("CO-0") || courseOffering.equals("CO-1")
				|| courseOffering.equals("CO-2")) {
			cslist.add(getCourseSet("CS-M202"));
		} else if (courseOffering.equals("CO-3")
				|| courseOffering.equals("CO-4")) {
			cslist.add(getCourseSet("CS-M202"));
			cslist.add(getCourseSet("CS-M303"));
		} else if (courseOffering.equals("CO-5")
				|| courseOffering.equals("CO-6")) {
			cslist.add(getCourseSet("CS-M303"));
		}
		return cslist;
	}
*/
	@Override
	public boolean isCanonicalCourseDefined(String eid) {
		log.debug("isCanonicalCourseDefined: eid: " + eid);
		return false;
	}

	@Override
	public Set<CanonicalCourse> getEquivalentCanonicalCourses(
			String canonicalCourseEid) throws IdNotFoundException {
		log.debug("getEquivalentCanonicalCourses: canonicalCourseEid: "
				+ canonicalCourseEid);
		return null;
	}
	

}
