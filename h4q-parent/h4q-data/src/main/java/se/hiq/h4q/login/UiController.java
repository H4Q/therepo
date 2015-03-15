package se.hiq.h4q.login;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.hiq.h4q.dataprocess.DataPoint;
import se.hiq.h4q.dataprocess.UiDataProcessor;


/**
 * Controller for the UI
 *
 * @author Alexander Ludkiewicz, HiQ Stockholm AB
 */
@Controller
public class UiController {

	private static final String ENCODING = "Windows-1252";

	public static void main(String[] args) throws IOException {
		UiDataProcessor.populateData();
		UiDataProcessor.orderRegionsByDeviation();
	}

	@RequestMapping(value = "/ui", method = RequestMethod.GET)
	public String getUI() throws IOException {
		return "ui/ui";
	}

	@RequestMapping(value = "/ui/parties/{param}", method = RequestMethod.GET)
	@ResponseBody
	public Data getDataToUI(@PathVariable String param) throws IOException {
		List<DataPoint> data = UiDataProcessor.partiesToDataPoints.get(param);

		if(data == null) {
			return new Data();
		}
		return UiDataProcessor.dataPointsToCol(data);
	}

	@RequestMapping(value = "/ui/regions/deviation", method = RequestMethod.GET)
	@ResponseBody
	public List<DistData> getRegionDeviation() throws IOException {
		List<DistData> regionsByDeviation = UiDataProcessor.orderRegionsByDeviation();

		return regionsByDeviation;
	}
}