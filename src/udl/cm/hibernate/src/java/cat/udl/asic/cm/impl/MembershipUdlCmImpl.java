package cat.udl.asic.cm.impl;


import java.io.Serializable;

import org.sakaiproject.coursemanagement.api.Membership;

public class MembershipUdlCmImpl implements Membership, Serializable {

	private static final long serialVersionUID = 1L;	

	private String userId;
	private String role;
	private AbstractMembershipContainerUdlCmImpl memberContainer;
	private String status;

	private String eid;
	private int version;
	
	
	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	private Long key;
	
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public MembershipUdlCmImpl() {}
	
    public MembershipUdlCmImpl(String userId, String role, AbstractMembershipContainerUdlCmImpl memberContainer,
                            String status) {
		this.userId = userId;
		this.role = role;
		this.memberContainer = memberContainer;
                this.status = status;
	}
    

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public AbstractMembershipContainerUdlCmImpl getMemberContainer() {
		return memberContainer;
	}

	public void setMemberContainer(AbstractMembershipContainerUdlCmImpl memberContainer) {
		this.memberContainer = memberContainer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return "MockUdl";
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
}