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
import scala.slick.driver.H2Driver.simple._

import DB._

object Application extends Controller {
  val baseUrl = "http://fhirtest.uhn.ca/baseDstu1/"
  val users: TableQuery[Users] = TableQuery[Users]
  val patients: TableQuery[Patients] = TableQuery[Patients]
  val todos: TableQuery[Todos] = TableQuery[Todos]
  
  implicit val patientWrites = new Writes[Patient]{
    def writes(patient: Patient) = Json.obj(
        "id" -> patient.id,
        "nameFirst" -> patient.nameFirst,
        "nameLast" -> patient.nameLast
    )
}
  
  val db = Database.forURL("jdbc:h2:mem:coldfront;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  db.withSession { implicit session =>

    // Create the schema
    (users.ddl ++ patients.ddl ++ todos.ddl).create

    // Insert some fake data
    users += (1, "vu")
    users += (2, "john")
    patients += Patient("Vu", "Nguyen")
    patients += Patient("John", "Doe")
  
  }
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
    
  def test(name: String) = Action {
    Ok(Json.toJson("GET SUCCESS, " + name))
  }
  
  def testPost = Action {
    Ok(Json.toJson("POST SUCCESS"))
  }
  
  def listPatients = Action {
      //val patientWrites = Json.writes[Patients]
    db.withSession{ implicit session =>
      val results = for {p <- patients} yield (p.id.toString + p.nameLast + ", " + p.nameFirst)
      Ok(Json.toJson(patients.list))
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

  def getPatientViews(patientId: Long) = Action {
    Ok(views.html.patientView("hello"))
  }

}