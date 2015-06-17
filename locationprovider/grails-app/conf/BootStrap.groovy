import locationprovider.PostCodeDetails
import locationprovider.SecRole
import locationprovider.SecUser
import locationprovider.SecUserSecRole

class BootStrap {

	def grailsApplication
	def PostCodeDetails postCodeDetails


	def init = { servletContext ->

		//initialize test data
		seedTestData()

		//All to do with security
		//1. role creation
		def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
		def adminRole = SecRole.findByAuthority('ROLE_ADMIN') ?: new SecRole(authority: 'ROLE_ADMIN').save(failOnError: true)

		//2. Normal User creation
		def normalUser = SecUser.findByUsername('user') ?: new SecUser(
				username: 'test',
				//no need to encrypt as it is getting encrypted in the service
				password: 'test123',
				enabled: true).save(failOnError: true)

		if (!normalUser.authorities.contains(userRole)) {
			SecUserSecRole.create normalUser, userRole
		}

		//3. Administrator User creation
		def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
				username: 'admin',
				//no need to encrypt as it is getting encrypted in the service
				password: 'admin123',
				enabled: true).save(failOnError: true)

		if (!adminUser.authorities.contains(adminRole)) {
			SecUserSecRole.create adminUser, adminRole

		}


		def destroy = { println "Application shutting down... " }
	}

	/**
	 * The method which is going to load csv data into Domain Objects
	 * In ideal scenario you would like to get the data from DB directly 
	 */
	private void seedTestData(){
		log.debug("PostCodeDetails.count() is zero")
		def filePath = "/resources/ukpostcodesless.csv"
		def myFile = grailsApplication.mainContext.getResource("classpath:$filePath").file
		def i=1
		myFile.splitEachLine(','){fields ->
			postCodeDetails = new PostCodeDetails(
					id       : fields[0],
					postcode : fields[1],
					latitude : fields[2],
					longitude: fields[3]
					)
			postCodeDetails.validate()
			if(postCodeDetails.hasErrors()){
				log.debug("Could not import line ${i} due to ${postCodeDetails.errors}")
			}
			else {
				log.info("Importing line ${i}:${postCodeDetails.toString()}")
				postCodeDetails.save(failOnError:true,flush:true)
			}
			++i
		}
	}
}
