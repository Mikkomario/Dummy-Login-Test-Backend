# Dummy Login Test Backend

## Purpose
The purpose of this project is to offer a very simple test environment for login front ends. This project provides the back end for such tests.

## Deployment (Tomcat)
1) Go to Releases and download the packaged attachment of the latest release.  

2) In your tomcat/webapps folder, create a new directory with the end point name you wish to use (Eg. "dummy")

3) Extract the contents of the attachment to that new directory. The file structure look like this:
- web application directory
	- WEB-INF
		- web.xml
		- classes
			- ...
		- lib
			- ...

4) Start tomcat

5) When making requests to the server, add /login after the end point uri. Eg. "http://localhost:8080/dummy/login"

## Usage

### Request
Send a request with GET, POST, PUT or DELETE method to the end point uri.  
You will need to specify **one** of the following:
- A json object post body with non-empty 'email' and 'password' properties (which are read as strings)
- A basic authorization header

### Response
The server will response with one of the following statuses: 
- **204 No Content** - On successful login (login is considered successful when email address and the password are equal)
- **400 Bad Request** - When the request is missing **both** json object body with email and password properties **and** a basic authentication header.
- **401 Unauthorized** - When credentials were properly parsed from the request but email address wasn't equal to the password (simulates incorrect credentials). 