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
  val baseUrl = "http://fhirtest.uhn.ca/baseDstu1/"
    
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
    val url = baseUrl + "Patient?_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
  }
  
  def getPatient(patientId: Long) = Action.async {
    val url = baseUrl + s"Patient/$patientId?_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
  }
  
  def getConditions(patientId: Long) = Action.async {
    val url = baseUrl + s"Condition?subject._id=13123&_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
  }
  
  def getMedications(patientId: Long) = Action.async {
    val url = baseUrl + s"MedicationStatement?patient._id=$patientId&_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
  }
  
  def getObservations(patientId: Long) = Action.async {
    val url = baseUrl + s"Observation?subject._id=$patientId&_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
  }

  def getPatientView = Action {
    Ok(views.html.patientView())
  }

}