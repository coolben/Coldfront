package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.Future
//import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global
object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
    
  def test(name: String) = Action {
    Ok(Json.toJson("GET SUCCESS, " + name))
  }
  
  def testPost = Action {
    Ok(Json.toJson("POST SUCCESS"))
  }
  
  def listPatients = Action.async {
    val url = "http://fhirtest.uhn.ca/baseDstu1/Patient?_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
    //Ok(Json.toJson("list all patients"))
  }
  
  def getPatient(patientId: Long) = Action {
      Ok(Json.toJson("patient id: " + patientId))
  }
  
  def getConditions(patientId: Long) = Action {
      Ok(Json.toJson("return conditions for patient id: " + patientId))
  }
  
  def getMedications(patientId: Long) = Action {
      Ok(Json.toJson("return medications for patient id: " + patientId))
  }
  
  def getObservations(patientId: Long) = Action {
      Ok(Json.toJson("return observations for patient id: " + patientId))
  }

}