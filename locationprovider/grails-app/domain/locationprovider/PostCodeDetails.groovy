package locationprovider

import grails.rest.Resource

@Resource(uri="/PostCodeDetails")
class PostCodeDetails {

	/* Default (injected) attributes of GORM */
	Long    id
	Long    version

	/* Automatic time stamping of GORM */
	Date    dateCreated
	Date    lastUpdated

	// field variables from the postcodeuk.csv to be used for domain class mapping

	String postcode
	double latitude
	double longitude

	PostCodeDetails(String postalCode, double latitude, double longitude){
		this.postcode =postalCode
		this.latitude =latitude
		this.longitude=longitude
	}
	//update advance validation constrains later for validation if the time permits
	static constraints = {
		postcode blank : false,nullable : false
		latitude blank : false,nullable : false
		longitude blank : false,nullable : false
	}
}
