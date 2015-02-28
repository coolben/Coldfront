package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

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
  
  def listPatients = Action{
    Ok(Json.toJson("list all patients"))
  }
  
  def getPatient(patientId: Long){
      Ok(Json.toJson("patient id: " + patientId))
  }
  
  def getConditions(patientId: Long){
      Ok(Json.toJson("return conditions for patient id: " + patientId))
  }
  
  def getMedications(patientId: Long){
      Ok(Json.toJson("return medications for patient id: " + patientId))
  }
  
  def getObservations(patientId: Long){
      Ok(Json.toJson("return observations for patient id: " + patientId))
  }

}