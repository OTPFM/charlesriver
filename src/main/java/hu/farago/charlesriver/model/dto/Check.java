package hu.farago.charlesriver.model.dto;

public class Check {

	private String extId;
	private String complianceType;
	private String batchType;
	private String id;
	private boolean details;
	
	public Check() {}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getComplianceType() {
		return complianceType;
	}

	public void setComplianceType(String complianceType) {
		this.complianceType = complianceType;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isDetails() {
		return details;
	}

	public void setDetails(boolean details) {
		this.details = details;
	}

}
