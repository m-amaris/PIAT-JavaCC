package piat.opendatasearch;

/**
 * Represents a resource entity with various properties, typically associated
 * with open data information.
 * 
 * @author Miguel Amarís
 */
public class Resource {
	private String resourceId;// @id
	private String conceptId;// @type
	private String title;
	private String link; // relation
	private String area;
	private String eventLocation; // street-address
	private String startDate;
	private String endDate;
	private double latitude;
	private double longitude;
	private String accessibility;
	private String description;// services
	private String organizationName;

	/**
	 * Get the resource ID.
	 *
	 * @return The resource ID.
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * Set the resource ID.
	 *
	 * @param resourceId The resource ID to set.
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * Get the concept ID.
	 *
	 * @return The concept ID.
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * Set the concept ID.
	 *
	 * @param conceptId The concept ID to set.
	 */
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * Get the title of the resource.
	 *
	 * @return The title of the resource.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the resource.
	 *
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the link associated with the resource.
	 *
	 * @return The link of the resource.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Set the link associated with the resource.
	 *
	 * @param link The link to set.
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Get the area information.
	 *
	 * @return The area information.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * Set the area information.
	 *
	 * @param area The area information to set.
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * Get the event location, typically a street address.
	 *
	 * @return The event location.
	 */
	public String getEventLocation() {
		return eventLocation;
	}

	/**
	 * Set the event location, typically a street address.
	 *
	 * @param eventLocation The event location to set.
	 */
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	/**
	 * Get the start date of the resource.
	 *
	 * @return The start date.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date of the resource.
	 *
	 * @param startDate The start date to set.
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date of the resource.
	 *
	 * @return The end date.
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Set the end date of the resource.
	 *
	 * @param endDate The end date to set.
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the latitude coordinate.
	 *
	 * @return The latitude coordinate.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Set the latitude coordinate.
	 *
	 * @param latitude The latitude coordinate to set.
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get the longitude coordinate.
	 *
	 * @return The longitude coordinate.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Set the longitude coordinate.
	 *
	 * @param longitude The longitude coordinate to set.
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get the accessibility information.
	 *
	 * @return The accessibility information.
	 */
	public String getAccesibility() {
		return accessibility;
	}

	/**
	 * Set the accessibility information.
	 *
	 * @param accessibility The accessibility information to set.
	 */
	public void setAccesibility(String accessibility) {
		this.accessibility = accessibility;
	}

	/**
	 * Get the description or services provided by the resource.
	 *
	 * @return The description or services information.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description or services provided by the resource.
	 *
	 * @param description The description or services information to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the name of the organization associated with the resource.
	 *
	 * @return The organization name.
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * Set the name of the organization associated with the resource.
	 *
	 * @param organizationName The organization name to set.
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * Returns a string representation of the Resource object, including its
	 * attributes.
	 *
	 * @return A string representation of the Resource object.
	 */
	@Override
	public String toString() {
		return "Resource [resourceId=" + resourceId + ", conceptId=" + conceptId + ", title=" + title + ", link=" + link
				+ ", area=" + area + ", eventLocation=" + eventLocation + ", startDate=" + startDate + ", endDate="
				+ endDate + ", latitude=" + latitude + ", longitude=" + longitude + ", accesibility=" + accessibility
				+ ", description=" + description + ", organizationName=" + organizationName + "]\n";
	}
}
