package se.hiq.h4q.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.hiq.h4q.dataprocess.DataPoint;


/**
 * Controller for the UI
 *
 * @author Alexander Ludkiewicz, HiQ Stockholm AB
 */
@Controller
public class UiController {

	private static final String ENCODING = "Windows-1252";

	private static Map<String, List<DataPoint>> partiesToDataPoints = new HashMap<>();

	static {
		try {
			populateData();
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}


	@RequestMapping(value = "/ui", method = RequestMethod.GET)
	public String getUI() throws IOException {
		return "ui/ui";
	}

	@RequestMapping(value = "/ui/{param}", method = RequestMethod.GET)
	@ResponseBody
	public Data getDataToUI(@PathVariable String param) throws IOException {
		List<DataPoint> data = partiesToDataPoints.get(param);

		for(String s : partiesToDataPoints.keySet()) {
			System.out.println(s);
		}

		if(data == null) {
			return new Data();
		}
		return dataPointsToCol(data);
	}

	private Data dataPointsToCol(List<DataPoint> dps) {
		Data cols = new Data();
		for(DataPoint dp : dps) {
			cols.x.add(dp.getAvgIncomeDiff());
			cols.y.add(dp.getPercentageVotes());
		}
		return cols;
	}



	private static void populateData() throws IOException {
		File dataFile = new File("C:\\Users\\ealelud\\Desktop\\Data\\scatterPlotData.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), ENCODING));
		String line;
		while((line = br.readLine()) != null) {
			parseLine(line);
		}
	}

	private static void parseLine(String line) {
		String[] split = line.split("\t");
		String region = split[0];
		String party = split[1];
		Double avgIncomeDiff = Double.parseDouble(split[2]);
		Double percentage = Double.parseDouble(split[3]);

		DataPoint dp = new DataPoint();
		dp.setRegion(region);
		dp.setAvgIncomeDiff(avgIncomeDiff);
		dp.setPartyBlock(party);
		dp.setPercentageVotes(percentage);

		List<DataPoint> dataPointsForParty = partiesToDataPoints.get(party);
		if(dataPointsForParty == null) {
			dataPointsForParty = new ArrayList<>();
		}
		dataPointsForParty.add(dp);
		partiesToDataPoints.put(party, dataPointsForParty);
	}
}
class Data {
	public List<Double> x = new ArrayList<>();
	public List<Double> y = new ArrayList<>();
}

