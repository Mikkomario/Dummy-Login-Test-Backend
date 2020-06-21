package demo.login.servlet

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import utopia.access.http.Status.{BadRequest, Unauthorized}
import utopia.bunnymunch.jawn.JsonBunny
import utopia.flow.generic.DataType
import utopia.flow.parse.JsonParser
import utopia.flow.util.CollectionExtensions._
import utopia.nexus.http.ServerSettings
import utopia.nexus.rest.{BaseContext, Context}
import utopia.nexus.result.Result
import utopia.nexus.servlet.HttpExtensions._

/**
 * A very simple servlet that is used for generating test responses for login front implementations
 * @author Mikko Hilpinen
 * @since 20.6.2020, v1
 */
class DummyLoginServlet() extends HttpServlet
{
	// INITIAL CODE ---------------------------
	
	DataType.setup()
	
	
	// ATTRIBUTES   ---------------------------
	
	private implicit val jsonParser: JsonParser = JsonBunny
	// Hard-coded since server address is not intended to be used in this simple implementation
	private implicit val settings: ServerSettings = ServerSettings("http://localhost:9999")
	
	
	// IMPLEMENTED  ---------------------------
	
	override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = handleRequest(req, resp)
	
	override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = handleRequest(req, resp)
	
	override def doPut(req: HttpServletRequest, resp: HttpServletResponse) = handleRequest(req, resp)
	
	override def doDelete(req: HttpServletRequest, resp: HttpServletResponse) = handleRequest(req, resp)
	
	
	// OTHER    -------------------------------
	
	private def handleRequest(req: HttpServletRequest, resp: HttpServletResponse) =
	{
		req.toRequest match
		{
			case Some(request) =>
				implicit val context: Context = new BaseContext(request)
				// Expects either
				// a) JSON body with email and password
				// b) Basic Auth header
				val result = request.body.findMap { _.bufferedJsonObject.contents.toOption } match
				{
					case Some(body) =>
						body("email").string.flatMap { email =>
							body("password").string.map { password =>
								// Will not perform any real checking, simply returns OK if email and password
								// are equal to each other
								email == password
							}
						} match
						{
							case Some(areEqual) => if (areEqual) Result.Empty else Result.Failure(Unauthorized)
							case None => Result.Failure(BadRequest,
								"Please provide both 'email' and 'password' in the request json object body")
						}
					case None =>
						// Checks for auth-header
						request.headers.basicAuthorization match
						{
							case Some((user, password)) =>
								if (user == password) Result.Empty else Result.Failure(Unauthorized)
							case None => Result.Failure(BadRequest,
								"Please specify either a json object body with 'email' and 'password' or a basic authorization header")
						}
				}
				result.toResponse.update(resp)
			case None => resp.setStatus(BadRequest.code)
		}
	}
}
