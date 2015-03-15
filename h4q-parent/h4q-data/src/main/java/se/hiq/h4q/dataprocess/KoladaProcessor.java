package se.hiq.h4q.dataprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class KoladaProcessor {

	public static Map<String, String> idToNameOfKpi = new HashMap<>();
	public static Map<String, String> regionNameToCode = new HashMap<>();

	private static final String urlFormat = "http://api.kolada.se/v2/data/kpi/%s/municipality/%s/year/%s";
	//http://api.kolada.se/v2/data/kpi/U33400,U28404/municipality/1283/year/2013

	private static final String ENCODING = "Windows-1252";

	static {
		populateKpiMap();
		try {
			populateRegionCodeMap();
		} catch (IOException e) {
			// TODO Auto-generated catch-block
			throw new RuntimeException("", e);
		}
	}


	public static void main(String[] args) throws IOException {
		populateKpiMap();
		populateRegionCodeMap();
		getDataFromKolada("Sundbyberg", "2013");
	}

	public static KoladaFront getDataFromKolada(String region, String year) {

		year = year == null ? "2013" : year;
		String regionCode = regionNameToCode.get(region);
		StringBuilder allKpis = new StringBuilder();
		for(String id : idToNameOfKpi.keySet()) {
			allKpis.append(id);
			allKpis.append(",");
		}
		allKpis.setLength(allKpis.length() - 1); // Remove last comma

		Gson gson = new Gson();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(String.format(urlFormat, allKpis.toString(), regionCode, year), HttpMethod.GET, null, String.class, "");
		KoladaDtoFinal koladaDtoFinal = gson.fromJson(response.getBody(), new TypeToken<KoladaDtoFinal>(){}.getType());

		List<KoladaFront> toFront = new ArrayList<>();
		List<Inner2> inner = koladaDtoFinal.getValues();
		KoladaFront kf = new KoladaFront();
		kf.setRegion(region);
		for(Inner2 i : inner) {
			String name = idToNameOfKpi.get(i.getKpi());
			Double kpiVal = 0.0;
			if(name == null) {
				continue;
			}
			for(InnerInner2 ii : i.getValues()) {
				kpiVal = ii.getValue();
			}
			Map<String, Double> kpiNameToKpiVal = new HashMap<>();
			kpiNameToKpiVal.put(name, kpiVal);
			kf.getKpiToKpiValue().add(kpiNameToKpiVal);

		}

		return kf;
	}


	private static void populateRegionCodeMap() throws IOException {
		File electionResultFile = new File("C:\\Users\\ealelud\\Desktop\\Data\\Valresultat_2014_no_header.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(electionResultFile), ENCODING));

		String line;
		while((line = br.readLine()) != null){
			String regionCodeAndName = line.split(",")[0];
			String[] split = regionCodeAndName.split(" ");
			String code = split[0];
			code = code.replaceAll("\"", "");

			StringBuilder region = new StringBuilder();
			for(int i = 1; i < split.length; i++) {
				region.append(split[i] + " ");
			}
			region.setLength(region.length() - 1); // Remove last whitespace
			regionNameToCode.put(region.toString().replaceAll("\"", ""), code);
		}
		br.close();

	}



	public static void populateKpiMap() {
		Gson gson = new Gson();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange("http://api.kolada.se/v2/kpi_groups?title=kkik", HttpMethod.GET, null, String.class, "");
		KoladaDto koladaDto = gson.fromJson(response.getBody(), new TypeToken<KoladaDto>(){}.getType());

		List<Inner> values = koladaDto.getValues();
		for(Inner i : values) {
			List<InnerInner> members = i.getMembers();
			for(InnerInner ii : members) {
				idToNameOfKpi.put(ii.getMember_id(), ii.getMember_title());
			}
		}
	}
}
