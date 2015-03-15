package se.hiq.h4q.dataprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import se.hiq.h4q.login.Data;
import se.hiq.h4q.login.DistData;

/**
 * Class that handles dataprocessing that will eventually be presented by the UI
 *
 * @author Alexander Ludkiewicz, HiQ Stockholm AB
 */
public class UiDataProcessor {

	public static Map<String, List<DataPoint>> partiesToDataPoints = new HashMap<>();

	static {
		try {
			populateData();
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}


	public static Data dataPointsToCol(List<DataPoint> dps) {
		Data cols = new Data();
		for(DataPoint dp : dps) {
			cols.x.add(dp.getAvgIncomeDiff());
			cols.y.add(dp.getPercentageVotes());
		}
		return cols;
	}


	public static List<DistData> orderRegionsByDeviation() {
		Collection<List<DataPoint>> dataPoints = partiesToDataPoints.values();

		/**
		 * This will keep track of the running total of the distance from each
		 * graph for each region. In the end we will calculate the mean square
		 * root of all lists. Hopefully cool stuff will emerge
		 */
		Map<String, List<Double>> regionsToDistancesFromGraphs = new HashMap<>();


		/**
		 * Each list of data points corresponds to a graph - Find the best
		 * linear fit to it, calculate the distance from the line for each
		 * region and find the average distance for each region.
		 */
		for(List<DataPoint> dps : dataPoints) {
			/*
			 * Each list corresponds to the graph for a party.
			 * Create a linear fit for each party
			 */
			SimpleRegression sr = new SimpleRegression();
			for(DataPoint dp : dps) {
				sr.addData(dp.getAvgIncomeDiff(), dp.getPercentageVotes());
			}
			// All data added

			// Equation of a line is y = mx + k
			double m = sr.getSlope();
			double k = sr.getIntercept();

			//Distance from a point (x0,y0) is http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_an_equation
			for(DataPoint dataPoint : dps) {
				getDistance(regionsToDistancesFromGraphs, m, k, dataPoint);
			}
		}
		Set<String> regions = regionsToDistancesFromGraphs.keySet();
		List<DistData> dds = new ArrayList<>();
		for(String region : regions) {
			getRmsDistances(regionsToDistancesFromGraphs, region, dds);
		}
		sort(dds);
		for(DistData dd : dds) {
			System.out.println(dd.getRegion() + dd.getRmsDist());
		}
		return dds;

	}

	private static void getRmsDistances(Map<String, List<Double>> regionsToDistancesFromGraphs, String region, List<DistData> distDatas) {
		List<Double> distances = regionsToDistancesFromGraphs.get(region);
		Double rms = 0.0;
		for(Double d : distances) {
			rms = rms + d*d;
		}
		rms = rms / distances.size();
		rms = Math.sqrt(rms);
		DistData dd = new DistData();
		dd.setRegion(region);
		dd.setRmsDist(round(rms));
		distDatas.add(dd);
	}

	private static void sort(List<DistData> dds) {
		Collections.sort(dds, new Comparator<DistData>() {
			@Override
			public int compare(DistData o1, DistData o2) {
				return o1.getRmsDist().compareTo(o2.getRmsDist());
			}
		});
	}

	private static void getDistance(Map<String, List<Double>> regionsToDistancesFromGraphs, double m, double k, DataPoint dataPoint) {
		Double x0 = dataPoint.getAvgIncomeDiff();
		Double y0 = dataPoint.getPercentageVotes();

		Double numerator = x0 + m*y0 - m*k;
		Double denominator = m*m + 1;

		Double numDivDen = numerator / denominator;

		Double firstTerm = numDivDen - x0;
		Double secondTerm = m*numDivDen + k - y0;

		Double d = Math.sqrt(firstTerm*firstTerm + secondTerm*secondTerm);

		String region = dataPoint.getRegion();
		List<Double> distances = regionsToDistancesFromGraphs.get(region);
		if(distances == null) {
			distances = new ArrayList<>();
		}
		distances.add(d);
		regionsToDistancesFromGraphs.put(region, distances);
	}

	public static void populateData() throws IOException {
		File dataFile = new File("C:\\Users\\ealelud\\Desktop\\Data\\scatterPlotData.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "UTF-8"));
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
		dp.setAvgIncomeDiff(round(avgIncomeDiff));
		dp.setPartyBlock(party);
		dp.setPercentageVotes(percentage);

		List<DataPoint> dataPointsForParty = partiesToDataPoints.get(party);
		if(dataPointsForParty == null) {
			dataPointsForParty = new ArrayList<>();
		}
		dataPointsForParty.add(dp);
		partiesToDataPoints.put(party, dataPointsForParty);
	}

	private static Double round(Double d, int precision) {
		return (double)Math.round(d * precision) / precision;
	}

	private static Double round(Double d) {
		return round(d, 100);
	}

	public static void main(String[] args) {
		System.out.println(round(-45.1293809));
	}
}
