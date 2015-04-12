package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try, Success}
import scala.slick.driver.H2Driver.simple._

import DB._

object Application extends Controller {
  val baseUrl = "http://fhirtest.uhn.ca/baseDstu1/"

  // Table Queries for H2 database tables
  val users: TableQuery[Users] = TableQuery[Users]
  val patients: TableQuery[Patients] = TableQuery[Patients]
  val todos: TableQuery[Todos] = TableQuery[Todos]

  // Writes to convert our models to json
  implicit val patientWrites = new Writes[Patient]{
    def writes(patient: Patient) = Json.obj(
        "id" -> patient.id,
        "nameFirst" -> patient.nameFirst,
        "nameLast" -> patient.nameLast
    )
  }
  implicit val patientReads: Reads[(String, String, String)] = (
    (__ \ "id").read[String] ~
    ((__ \ "content" \ "name" )(0)\ "given")(0).read[String] ~
    ((__ \ "content" \ "name" )(0)\ "family")(0).read[String]
  ).tupled

  implicit val todosWrites = new Writes[Todo]{
    def writes(todo: Todo) = Json.obj(
      "id" -> todo.id,
      "patientId" -> todo.patientId,
      "text" ->  todo.text,
      "state" -> todo.state
    )
  }

  // The database object -- DB will remain open as long as JVM is running
  val db = Database.forURL("jdbc:h2:mem:coldfront;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  db.withSession { implicit session =>

    // Create the schema
    //(users.ddl ++ patients.ddl ++ todos.ddl).create

    (patients.ddl ++ todos.ddl).create
    // Insert some fake data
    patients += Patient(1, "Doe", "John")
    patients += Patient(2, "Doe", "Jane")
    // users += User(1, "vu")
    // users += User(2, "john")
    todos += Todo(1, 1, "Replace catheter", 0)
    todos += Todo(2, 1, "Check crocin order", 1)
    todos += Todo(3, 1, "Turn patient", 100)
    todos += Todo(4, 2, "Oral vent care", 0)
    todos += Todo(5, 2, "Insert vent", 100)


    val patientWS = WS.url(baseUrl + "Patient?_format=json").get().map {
      results =>
        Try(
          if (results.status == 200)
            (results.json)
          else
            throw new Exception(s"Error during request. ${results.status} ${results.body}")
        )
    }
    patientWS.map {
      case Success(json) => {
        val entries = (json \ "entry").as[JsArray]
        val people = (entries).as[List[(String, String, String)]]
        for (person <- people) {
          db.withSession { implicit session =>
            patients += Patient(person._1.split("/").last.toLong, person._2, person._3)
          }
        }
      }
      case Failure(e) => println(s"error: ${e.getMessage}")
    }
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
    db.withSession{ implicit session =>
      Ok(Json.toJson(patients.list))
    }
  }
  
  def getPatient(patientId: Long) = Action.async {
    val url = baseUrl + s"Patient/$patientId?_format=json"
    WS.url(url).get().map { response =>
        Ok(response.json)
    }
  }

  def listTodos = Action{
    db.withSession{ implicit session =>
      Ok(Json.toJson(todos.list))
    }
  }

  def changeTodoState(todoId: Long, state: Int) = Action{
    db.withSession { implicit session =>
      val q = for { t <- todos if t.id === todoId} yield t.state
      q.update(state).run
      Ok(s"$todoId state set to $state") 
    }
  }

  def getConditions(patientId: Long) = Action.async {
    val url = baseUrl + s"Condition?subject._id=$patientId&_format=json"
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