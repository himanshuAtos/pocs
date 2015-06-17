package locationprovider

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.rest.RestfulController

import javax.transaction.Transactional

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class PostCodeDetailsController extends RestfulController {

	//static allowedMethods = [save: "POST",calculateDistance:"POST"]
	static responseFormats =['json']


	def index = {
	}
	def showDistance ={ render (view : "calculateDistance") }
	def showPost ={ render (view : "showPost") }
	def showPostEdit ={ render (view : "updatePostcodes") }

	/**
	 * The method intend to update the post code data in a persistent manner
	 * Hence,it first updates the post code details in csv file ( could be DB) and then into domain class which is cached
	 * In case of failure in the file update, it will no longer update domain class
	 * On successful update, the data should be available when server is started next time
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
	@Transactional
	def updatePostalCode() {

		def jsonObj = request.JSON
		def postcode
		def longitude
		def latitude

		if(params.containsKey("postalCode")){
			postcode = params.get("postalCode")
			longitude = params.get("latitude")
			atitude=params.get("longitude")
		}
		else if(jsonObj){
			postcode = jsonObj.postalCode
			latitude = jsonObj.latitude
			longitude=jsonObj.longitude
		}


		if(postcode && latitude && longitude){

			//1. write to the file and then when successful, write the content to the domain object
			def filePath = "/resources/ukpostcodesless.csv"
			def myFile = this.grailsApplication.mainContext.getResource("classpath:$filePath").file
			try{
				myFile.append((PostCodeDetails.getCount().intValue() + 1) + "," + postcode + "," + latitude + "," +longitude + "\r\n" )
			}
			catch (Exception ex){
				log.info("unable to write to the csv file, & exception is " + ex)
				def returnMap = [:]
				returnMap["error"]="Unable to update Postal Codes, please try later"
				log.info("Response :" + (returnMap as JSON))
				render returnMap as JSON
				return
			}
			// when successful writing, update domain object
			def PostCodeDetails postcodedetails =  new PostCodeDetails(
					postcode : postcode,
					latitude : latitude,
					longitude: longitude
					)
			postcodedetails.save(failOnError:true,flush:true)
			def returnMap = [:]
			returnMap["success"]="PostCode Details updated"
			render returnMap as JSON
			return
		}
		else{
			log.info("Invalid/Missing parameters found in request")
			def returnMap = [:]
			returnMap["error"]="Please provide the correct input"
			render returnMap as JSON
			return
		}

		//
	}

	/**
	 * The method returns the post code details for a given post code
	 * @return JSON Object with post code, latitude, longitude
	 */
	@Secured(['ROLE_USER'])
	def getDetails(){
		def jsonObj = request.JSON
		def postalCode
		if(params.containsKey("postalCode")){
			postalCode = params.get("postalCode")
		}
		else if(jsonObj ){
			postalCode = jsonObj.postalCode
		}
		if(postalCode){
			def location
			try{
				location=PostCodeDetails.findByPostcode(postalCode)
				if(!location)
				{
					log.info("Post Code not found")
					def returnMap = [:]
					returnMap["error"]="Post Code not found"
					render returnMap as JSON
					return


				}
			}
			catch(Exception ex)
			{
				log.info("Invalid/Missing parameters found in request")
				def returnMap = [:]
				returnMap["error"]="Invalid/Missing parameters in request"
				log.info("Response :" + (returnMap as JSON))
				render returnMap as JSON
				return
			}
			def returnMap= [:]
			returnMap['postcode'] = postalCode
			returnMap['latitude'] = location.latitude
			returnMap['longitude'] = location.longitude
			render returnMap as JSON
			return
		}

		else{
			log.info("Invalid/Missing parameters found in request")
			def returnMap = [:]
			returnMap["error"]="Please provide the correct input"
			render returnMap as JSON
			return
		}


	}



	/**
	 * The code method which will calculate distance between two UK post codes
	 * 1. The method first validates the post codes with the existing data available
	 * 2. In case of valid values of the post codes, it goes to the persistent repository ( in this case csv)
	 * for respective latitudes and longitudes
	 * 3. Further the method calculates the distance using the utility method from generic Util
	 * 4. Then method structures the JSON response and send back
	 */

	//calculates the distance between two post codes
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	@Transactional
	def calculateDistance(){

		//1. validate whether valid postal codes
		log.info("inside calculateDistance Method")

		//retrieve loaded data of post codes for comparison and availability
		// added this patch because not able to implement http request from gsp
		def jsonObj = request.JSON
		def postcodeloc1
		def postcodeloc2

		//TODO here just checking params has relevant values or not
		if(params.containsKey("postalCodeLoc1")){
			postcodeloc1 = params.get("postalCodeLoc1")
			postcodeloc2 = params.get("postalCodeLoc2")
		}
		else if (jsonObj){
			postcodeloc1 = jsonObj.postalCodeLoc1
			postcodeloc2 = jsonObj.postalCodeLoc2
		}


		if(postcodeloc1 && postcodeloc2){

			// find whether the post codes are present in the list of known post codes
			def isLoc1Present
			def isLoc2Present
			try{

				isLoc1Present = PostCodeDetails.findByPostcode(postcodeloc1)
				isLoc2Present = PostCodeDetails.findByPostcode(postcodeloc2)

			}
			//not in relevance here but could be relevant when DB is connected
			//advised to capture specific DB exception, rather than capturing generic exception which is not good practice
			catch(Exception ex)
			{

				log.info("Problems retrieving the Post Code location " + ex)
				def returnMap = [:]
				returnMap["error"]="Problems retrieving the Post Code location"

				render returnMap as JSON
				return
			}

			if(isLoc1Present && isLoc2Present){
				//call Generic Util method and find the distance
				double  distance = GenericUtil.calculateDistance(isLoc1Present.latitude,isLoc1Present.longitude,
						isLoc2Present.latitude,isLoc2Present.longitude)
				log.info("Distance is :" + distance)

				//create the return map
				def returnMap= [:]
				returnMap['postcode1'] = postcodeloc1
				returnMap['latitude1'] = isLoc1Present.latitude
				returnMap['longitude1'] = isLoc1Present.longitude
				returnMap['postcode2'] = postcodeloc2
				returnMap['latitude2'] = isLoc2Present.latitude
				returnMap['longitude2'] = isLoc2Present.longitude
				returnMap['distance'] = distance
				returnMap['unit'] = "km"


				render returnMap as JSON
				return returnMap

			}

			else
			{
				log.info("Either or both of the Post Code Locations are incorrect or missing")
				def returnMap = [:]
				returnMap["error"]="Either or both of the Post Code Locations are incorrect or missing"

				render returnMap as JSON
				return

			}
		}
		else
		{
			log.info("Post Code locations/location is missing")
			def returnMap = [:]
			returnMap["error"]="Please correct the input"

			render returnMap as JSON
			return


		}
	}

}