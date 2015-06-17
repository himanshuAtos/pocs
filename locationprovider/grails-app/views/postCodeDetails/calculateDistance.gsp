<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
<style type="text/css" media="screen">
#status {
	background-color: #eee;
	border: .2em solid #fff;
	margin: 2em 2em 1em;
	padding: 1em;
	width: 12em;
	float: left;
	-moz-box-shadow: 0px 0px 1.25em #ccc;
	-webkit-box-shadow: 0px 0px 1.25em #ccc;
	box-shadow: 0px 0px 1.25em #ccc;
	-moz-border-radius: 0.6em;
	-webkit-border-radius: 0.6em;
	border-radius: 0.6em;
}

.ie6 #status {
	display: inline;
	/* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
}

#status ul {
	font-size: 0.9em;
	list-style-type: none;
	margin-bottom: 0.6em;
	padding: 0;
}

#status li {
	line-height: 1.3;
}

#status h1 {
	text-transform: uppercase;
	font-size: 1.1em;
	margin: 0 0 0.3em;
}

#page-body {
	margin: 2em 1em 1.25em 18em;
}

h2 {
	margin-top: 1em;
	margin-bottom: 0.3em;
	font-size: 1em;
}

p {
	line-height: 1.5;
	margin: 0.25em 0;
}

#controller-list ul {
	list-style-position: inside;
}

#controller-list li {
	line-height: 1.3;
	list-style-position: inside;
	margin: 0.25em 0;
}

@media screen and (max-width: 480px) {
	#status {
		display: none;
	}
	#page-body {
		margin: 0 1em 1em;
	}
	#page-body h1 {
		margin-top: 0;
	}
}
</style>
</head>
<body>
	<a href="#page-body" class="skip"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div id="page-body" role="main">
		<h1>Welcome to Distance Calculator</h1>

		<sec:ifNotLoggedIn>
			<g:link controller="login" action="auth">LOGIN</g:link>
		</sec:ifNotLoggedIn>
		<sec:ifAllGranted roles="ROLE_USER">
			<g:form controller="postCodeDetails" method="POST" Content-Type ="application/json">
				<table>
					<tbody>
						<tr>
							<td><label>Post Code Location 1 : </label> <g:textField
									id='postalCodeLoc1' name="postalCodeLoc1" /></td>
						<tr>
							<td><label>Post Code Location 2: </label> <g:textField
									id='postalCodeLoc2' name="postalCodeLoc2" /></td>
						</tr>
					</tbody>
				</table>
				<g:actionSubmit onSuccess="clear(e)" onLoading="showSpinner(true)"
					onComplete="showSpinner(false)"
					value="Find Distance between Post Codes" action="calculateDistance" />
			</g:form>

			<p>
				${returnMap}
			</p>
			<g:javascript>
            function clear(e) {
                $('postalCodeLoc1').value=''
                $('postalCodeLoc2').value=''
                
            }
            function calculateDistance{
            
            eval($('.data').attr('data-comments'));
            }
            function showSpinner(visible) {
                $('spinner').style.display = visible ? "
					inline" : "none"

                if (visible==
					false) {
                    // Move the child element to
					after 'firstPost'.
                    var first=$(
					'firstPost')
                    var
					child=first.firstDescendant().remove()first.insert({after:child})}}
			
		
					</g:javascript>




		</sec:ifAllGranted>
</body>
</html>