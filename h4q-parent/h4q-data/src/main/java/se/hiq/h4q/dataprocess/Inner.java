package se.hiq.h4q.dataprocess;

import java.util.List;

public class Inner {
	private String id;
	private List<InnerInner> members;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<InnerInner> getMembers() {
		return members;
	}
	public void setMembers(List<InnerInner>  members) {
		this.members = members;
	}
}