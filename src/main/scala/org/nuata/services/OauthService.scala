package org.nuata.services

import org.json4s.Extraction
import org.nuata.authentication.oauth.github.{GithubCode, GithubToken, GithubUserInfo}
import org.nuata.repositories.UserRepository
import org.nuata.shared.{Json4sProtocol, Settings}
import spray.client.pipelining._
import spray.http.HttpHeaders.{Accept, Authorization}
import spray.http._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.routing.{HttpService, MalformedRequestContentRejection}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by nico on 29/12/15.
 */
trait OauthService extends HttpService with Json4sProtocol {
  val pipeline: HttpRequest => Future[HttpResponse] = (
    addHeader(Accept(MediaTypes.`application/json`))
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate))

  val tokenPipeline: HttpRequest => Future[GithubToken] = pipeline ~> unmarshal[GithubToken]

  def userInfoPipeline(accessToken: String): HttpRequest => Future[GithubUserInfo] =
    (addHeader(Authorization(OAuth2BearerToken(accessToken)))
      ~> pipeline
      ~> unmarshal[GithubUserInfo])

  val githubAuthRoutes = (path("auth" / "github") & post & entity(as[GithubCode]) ) { rq =>
    val clientId = Settings.conf.getString("github.clientId")
    val clientSecret = Settings.conf.getString("github.clientSecret")

    val future = tokenPipeline(Get(s"https://github.com/login/oauth/access_token?code=${rq.code}&client_id=${clientId}&client_secret=${clientSecret}"))
      .flatMap( tokenInfo => {
        userInfoPipeline(tokenInfo.access_token)(Get("https://api.github.com/user"))
      }).flatMap { userInfo =>
        println(userInfo)
        UserRepository.emailExists(userInfo.email)
      }

    onSuccess(future) {
      case true => reject(MalformedRequestContentRejection(s"login or mail already registered"))
      case false => complete(Extraction.decompose(Map("token" -> "fsdfdsf")))
    }
  }


//  val githubAuthRoutes = (path("auth" / "github") & post & entity(as[Foo]) ) { rq =>
//    val clientId = Settings.conf.getString("github.clientId")
//    val clientSecret = Settings.conf.getString("github.clientSecret")
//
//      tokenPipeline2(Get(s"https://github.com/login/oauth/access_token?code=${rq.code}&client_id=${clientId}&client_secret=${clientSecret}"))
//        .flatMap( tokenInfo => {
//          pipeline(Get("https://api.github.com/user").withHeaders(Authorization(OAuth2BearerToken(tokenInfo.access_token))))
//        }).map { res =>
//          println(res.entity.asString)
//          parse(res.entity.asString).extract[GithubUserInfo]
////          val tokenInfo = parse(res.entity.asString).extract[GithubToken]
////          println(tokenInfo)
//        }.flatMap { userInfo =>
//          UserRepository.emailExists(userInfo.email)
//        }
//
//    complete {
//      Extraction.decompose(Map(
//        "token" -> "youhou"
//      ))
//    }
//  }


  /*
  val githubAuthRoutes2 = path("auth" / "github") {
    get {
      parameters('code) { (code) =>
        println(code)

        implicit val formatsCur = DefaultFormats
        val clientId = Settings.conf.getString("github.clientId")
        val clientSecret = Settings.conf.getString("github.clientSecret")

        val formData = FormData(Seq(
          "client_id" -> clientId,
          "client_secret" -> clientSecret,
          "code" -> code))

        pipeline(Post("https://github.com/login/oauth/access_token", formData)
          .withHeaders(Accept(MediaTypes.`application/json`)))
          .map { res =>
          parse(res.entity.asString).extract[GithubToken]
        }.flatMap( tokenInfo => {
          pipeline(Get("https://api.github.com/user/emails", formData)
            .withHeaders(Authorization(OAuth2BearerToken(tokenInfo.access_token))))
        }).map { res =>
          println(res.entity.asString)

        }.map { userMails =>
          userMails.find(m => m.primary.getOrElse(false)).getOrElse(userMails.head)
        }.flatMap { userMail =>
          UserRepository.emailExists(userMail.email)
        }

        complete {
          <html>
            <script>
//              window.opener.location.reload();
              window.close();
            </script>
          </html>
        }
      }
    }
  }
  */
}
