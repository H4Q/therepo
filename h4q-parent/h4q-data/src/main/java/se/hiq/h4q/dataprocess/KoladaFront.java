package se.hiq.h4q.dataprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KoladaFront {
	private String region;
	private String id;

	private List<Map<String, Double>> kpiToKpiValue = new ArrayList<>();

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Map<String, Double>> getKpiToKpiValue() {
		return kpiToKpiValue;
	}

	public void setKpiToKpiValue(List<Map<String, Double>> kpiToKpiValue) {
		this.kpiToKpiValue = kpiToKpiValue;
	}



}
