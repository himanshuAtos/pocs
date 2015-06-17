class UrlMappings {

	/**
	 * The URL mapping for Rest requests
	 */
	static mappings = {
		"/$controller/$action?/$id?(.$format)?"{ constraints { // apply constraints here
			}}

		//this is for Authentication module

		"/login/$action?"(controller: "login")
		"/logout/$action?"(controller: "logout")


		//URL mapping getting used for distance calculation
	/*	"/distanceFinder"(controller: "PostCodeDetails"){
			action=[POST: "calculateDistance" ]
		}*/
		
		"/distanceFinder" (controller: "PostCodeDetails", action: "calculateDistance", parseRequest: true)

		//URL mapping for updating the post code details

		"/updatePostCodes"(controller: "PostCodeDetails"){
			action=[POST: "updatePostalCode"]
		}

		//URL mapping for fetching the post code details
		"/postcodeDetails"(controller: "PostCodeDetails"){
			action=[POST: "getDetails"]
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
