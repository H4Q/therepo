package se.hiq.h4q.dataprocess;

public class DataPoint {

	private String region;
	private String partyBlock;
	private Double percentageVotes;
	private Double avgIncomeDiff;



	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getPartyBlock() {
		return partyBlock;
	}
	public void setPartyBlock(String partyBlock) {
		this.partyBlock = partyBlock;
	}
	public Double getPercentageVotes() {
		return percentageVotes;
	}
	public void setPercentageVotes(Double percentageVotes) {
		this.percentageVotes = percentageVotes;
	}
	public Double getAvgIncomeDiff() {
		return avgIncomeDiff;
	}
	public void setAvgIncomeDiff(Double avgIncomeDiff) {
		this.avgIncomeDiff = avgIncomeDiff;
	}
}
