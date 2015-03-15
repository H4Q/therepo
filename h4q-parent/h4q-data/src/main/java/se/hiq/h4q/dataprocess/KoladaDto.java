package se.hiq.h4q.dataprocess;

import java.util.List;

public class KoladaDto {

	private int count;
	private List<Inner> values;

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<Inner> getValues() {
		return values;
	}
	public void setValues(List<Inner> values) {
		this.values = values;
	}
}
