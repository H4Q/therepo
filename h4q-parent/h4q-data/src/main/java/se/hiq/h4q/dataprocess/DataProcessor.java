package se.hiq.h4q.dataprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataProcessor {


	/**
	 * Common index for regions in both files
	 */
	private static final int REGION_IDX = 0;

	/**
	 * Election result indices
	 */
	private static final int PARTY_IDX = 1;
	private static final int RESULT_IN_PERCENT_IDX = 2;

	/**
	 * Income file indices
	 */
	private static final int GENDER_IDX = 1;
	private static final int AGE_GRP_IDX = 2;
	private static final int AVG_INCOME_IDX = 3;
	private static final int MEDIAN_INCOME_IDX = 4;

	private static final Double AVG_INCOME_SE = 258.11;

	private static final String ENCODING = "Windows-1252";
	private static final String TAB = "\t";

	public static void main(String[] args) throws IOException {
		generateData();
	}

	private static void generateData() throws FileNotFoundException, IOException {
		File avgIncomeFile = new File("C:\\Users\\ealelud\\Desktop\\Data\\Medel-och medianinkomst_2013_no_header.csv");
		File electionResultFile = new File("C:\\Users\\ealelud\\Desktop\\Data\\Valresultat_2014_no_header.csv");
		Map<String, Double> regionToAvgIncome = new HashMap<>();
		Map<String, Map<String, Double>> regionToElectionRes = new HashMap<>();
		List<DataPoint> data = new ArrayList<>();

		parseIncomeFile(avgIncomeFile, regionToAvgIncome);
		parseElectionResultFile(electionResultFile, regionToElectionRes);

		Collection<Double> allAvgIncomes = regionToAvgIncome.values();
		double runningSum = 0;
		for(Double d : allAvgIncomes) {
			runningSum = runningSum + d;
		}
		double averageIncomeAll = runningSum/allAvgIncomes.size();

		System.out.println("Average income Sweden: " + averageIncomeAll);
		populateData(regionToAvgIncome, regionToElectionRes, data);
		writeDataToFile(data);
	}

	private static void writeDataToFile(List<DataPoint> data) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("C:\\Users\\ealelud\\Desktop\\Data\\scatterPlotData.txt", "UTF-8");
		sort(data);
		for(DataPoint dp : data) {
			writer.println(dp.getRegion() + TAB + dp.getPartyBlock() + TAB + dp.getAvgIncomeDiff() + TAB + dp.getPercentageVotes());
		}
		writer.close();
	}

	private static void sort(List<DataPoint> data) {
		Collections.sort(data, new  Comparator<DataPoint>() {

			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				if(o1.getPartyBlock().equals(o2.getPartyBlock())) {
					return o1.getRegion().compareTo(o2.getRegion());
				}
				return o1.getPartyBlock().compareTo(o2.getPartyBlock());
			}
		});
	}

	private static void populateData(Map<String, Double> regionToAvgIncome, Map<String, Map<String, Double>> regionToElectionRes, List<DataPoint> data) {
		Set<String> regions = regionToAvgIncome.keySet();
		for(String region : regions) {
			Map<String, Double> regionResults = regionToElectionRes.get(region);
			Set<String> parties = regionResults.keySet();
			for(String party : parties) {
				addDataPoint(regionToAvgIncome, data, region, regionResults, party);
			}
		}
	}

	private static void addDataPoint(Map<String, Double> regionToAvgIncome, List<DataPoint> data, String region, Map<String, Double> regionResults, String party) {
		Double income = regionToAvgIncome.get(region);
		Double partyResults = regionResults.get(party);
		DataPoint dp = new DataPoint();
		dp.setRegion(region);
		dp.setPartyBlock(party);
		dp.setAvgIncomeDiff(income - AVG_INCOME_SE);
		dp.setPercentageVotes(partyResults);
		data.add(dp);
	}

	private static void parseElectionResultFile(File electionResultFile, Map<String, Map<String, Double>> regionToElectionRes) throws NumberFormatException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(electionResultFile), ENCODING));
		String line;
		while((line = br.readLine()) != null){
			if(line.contains("..")) {
				continue;
			}
			parseElectionLine(line, regionToElectionRes);
		}
		br.close();
	}

	private static void parseIncomeFile(File avgIncomeFile, Map<String, Double> regionToIncome) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(avgIncomeFile), ENCODING));

		String line;
		while((line = br.readLine()) != null){
			if(!line.contains(",\"totalt 20+ år\"")) {
				continue;
			}
			parseIncomeLine(line, regionToIncome);
		}
		br.close();
	}

	private static void parseIncomeLine(String line, Map<String, Double> regionToIncome) {
		String[] split = line.split(",");
		String region = split[REGION_IDX];
		region = region.substring(6, region.length() - 1);
		Double avgIncome = Double.parseDouble(split[AVG_INCOME_IDX]);
		regionToIncome.put(region, avgIncome);
	}

	private static void parseElectionLine(String line, Map<String, Map<String, Double>> regionToElectionRes) {
		String[] split = line.split(",");
		String region = split[REGION_IDX];
		region = region.substring(6, region.length() - 1);

		String party = split[PARTY_IDX].replaceAll("\"", "");

		String resultString = split[RESULT_IN_PERCENT_IDX];

		Double result = Double.parseDouble(split[RESULT_IN_PERCENT_IDX]);

		Map<String, Double> electionResult = regionToElectionRes.get(region);

		if(electionResult == null) {
			electionResult = new HashMap<>();
		}
		electionResult.put(party, result);
		regionToElectionRes.put(region, electionResult);
	}
}