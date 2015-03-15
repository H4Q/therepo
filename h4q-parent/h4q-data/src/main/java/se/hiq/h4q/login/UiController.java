package se.hiq.h4q.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.hiq.h4q.dataprocess.UiDataProcessor;


/**
 * Controller for the UI
 *
 * @author Alexander Ludkiewicz, HiQ Stockholm AB
 */
@Controller
public class UiController {

	private static final String ENCODING = "Windows-1252";

	//	public static void main(String[] args) throws IOException {
	//		UiDataProcessor.populateData();
	//		UiDataProcessor.orderRegionsByDeviation();
	//	}

	@RequestMapping(value = "/ui", method = RequestMethod.GET)
	public String getUI() throws IOException {
		return "ui/ui";
	}

	@RequestMapping(value = "/ui/incomeparty", method = RequestMethod.GET)
	public String getUIIncomeParties() throws IOException {
		return "ui/ui_incomeparty";
	}


	@RequestMapping(value = "/ui/regiondeviation", method = RequestMethod.GET)
	public String getUIRegionDeviation() throws IOException {
		return "ui/ui_regiondeviation";
	}

	@RequestMapping(value = "/ui/parties/{param}", method = RequestMethod.GET)
	@ResponseBody
	public ColumnData getDataToUI(@PathVariable String param) throws IOException {
		List<DataPoint> data = UiDataProcessor.partiesToDataPoints.get(param);

		if(data == null) {
			return new ColumnData();
		}
		return UiDataProcessor.dataPointsToCol(data);
	}

	@RequestMapping(value = "/ui/regions/deviation", method = RequestMethod.GET)
	@ResponseBody
	public ColumnDataRegionDeviation getRegionDeviation() throws IOException {
		List<DistData> regionsByDeviation = UiDataProcessor.orderRegionsByDeviation();

		return distDataToColData(regionsByDeviation);
	}

	@RequestMapping(value = "/ui/regions/deviation/map", method = RequestMethod.GET)
	@ResponseBody
	public Map<Integer, String> getRegionDeviationMap() throws IOException {

		List<DistData> dds = UiDataProcessor.orderRegionsByDeviation();
		Map<Integer, String> orderToRegion = new HashMap<>();

		int i = 1;
		for(DistData dd : dds) {
			orderToRegion.put(i++, dd.getRegion());
		}

		return orderToRegion;
	}

	private ColumnDataRegionDeviation distDataToColData(List<DistData> dds) {
		ColumnDataRegionDeviation cd = new ColumnDataRegionDeviation();
		int i = 1;
		for(DistData dd : dds) {
			cd.x.add(i++);
			cd.y.add(dd.getRmsDist());
		}
		return cd;
	}
}