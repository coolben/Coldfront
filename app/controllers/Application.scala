package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def test = Action {
      Ok(Json.toJson("GET SUCCESS"))
  }
  
  def testPost = Action {
      Ok(Json.toJson("POST SUCCESS"))
  }

}