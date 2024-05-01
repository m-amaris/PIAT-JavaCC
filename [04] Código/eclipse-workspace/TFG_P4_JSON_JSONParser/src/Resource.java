
public class Resource {
	private String resourceId;//@id
	private String conceptId;//@type
	private String title;
	private String link; //relation
	private String area;
	private String eventLocation; //street-address
	private String startDate;
	private String endDate;
	private double latitude;
	private double longitude;
	private String accesibility;
	private String description;//services
	private String organizationName;
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getConceptId() {
		return conceptId;
	}
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getEventLocation() {
		return eventLocation;
	}
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getAccesibility() {
		return accesibility;
	}
	public void setAccesibility(String accesibility) {
		this.accesibility = accesibility;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	@Override
	public String toString() {
		return "Resource [resourceId=" + resourceId + ", conceptId=" + conceptId + ", title=" + title + ", link=" + link
				+ ", area=" + area + ", eventLocation=" + eventLocation + ", startDate=" + startDate + ", endDate="
				+ endDate + ", latitude=" + latitude + ", longitude=" + longitude + ", accesibility=" + accesibility
				+ ", description=" + description + ", organizationName=" + organizationName + "]\n";
	}
}
