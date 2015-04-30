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
import scala.slick.jdbc.meta.MTable

import DB._

object Application extends Controller {
  val baseUrl = "http://fhirtest.uhn.ca/baseDstu2/"

  // Table Queries for H2 database tables
  val users: TableQuery[Users] = TableQuery[Users]
  val patients: TableQuery[Patients] = TableQuery[Patients]
  val todos: TableQuery[Todos] = TableQuery[Todos]

  // Writes to convert our models to json
  implicit val patientWrites = new Writes[Patient]{
    def writes(patient: Patient) = Json.obj(
        "id" -> patient.id,
        "nameFirst" -> patient.nameFirst,
        "nameLast" -> patient.nameLast,
        "dob" -> patient.dob
    )
  }
  
  implicit val patientReads: Reads[(String, String, String, String)] = (
    (__ \ "resource" \ "id").read[String] ~
    (((__ \ "resource" \ "name" )(0)\ "given")(0).read[String] or Reads.pure(""))~
    (((__ \ "resource" \ "name" )(0)\ "family")(0).read[String] or Reads.pure(""))~
    ((__ \ "resource" \ "birthDate").read[String] or Reads.pure(""))
  ).tupled

  implicit val todosWrites = new Writes[Todo]{
    def writes(todo: Todo) = Json.obj(
      "id" -> todo.id,
      "patientId" -> todo.patientId,
      "text" ->  todo.text,
      "state" -> todo.state
    )
  }
  
  implicit val todoReads: Reads[Todo] = (
      (__ \ "id").read[Long] ~
      Reads.pure(1.toLong) ~
      (__ \ "text").read[String] ~
      Reads.pure(0)
  )(Todo.apply _)

  case class DiagnosticOrderItem(text:String, code:String)
  case class DiagnosticOrderEvent(status:String, date:String)
  case class DiagnosticOrder(
      id:String, 
      subject:String, 
      orderer:String, 
      status:Option[String],
      items:Option[List[DiagnosticOrderItem]],
      events:Option[List[DiagnosticOrderEvent]]
  )
  
  implicit val dOrderItemReads: Reads[DiagnosticOrderItem] = (
    (__ \ "code" \ "text").read[String] ~
    (__ \ "code" \ "text").read[String]
  )(DiagnosticOrderItem.apply _)
  
  implicit val dOrderEventReads: Reads[DiagnosticOrderEvent] = (
    (__ \ "status").read[String] ~
    (__ \ "dateTime").read[String]
  )(DiagnosticOrderEvent.apply _)
  
  implicit val dOrderReads: Reads[DiagnosticOrder] = (
    (__ \ "resource" \ "id").read[String] ~
    ((__ \ "resource" \ "subject" \ "reference").read[String]  or Reads.pure("")) ~
    ((__ \ "resource" \ "orderer" \ "reference").read[String] or Reads.pure("")) ~
    (__ \ "resource" \ "status").readNullable[String] ~
    (__ \ "resource" \ "item").readNullable[List[DiagnosticOrderItem]] ~
    (__ \ "resource" \ "event").readNullable[List[DiagnosticOrderEvent]]
  )(DiagnosticOrder.apply _)

  // The database object -- DB will remain open as long as JVM is running
  val db = Database.forURL("jdbc:h2:mem:coldfront;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  db.withSession { implicit session =>

    // Create the schema
    //(users.ddl ++ patients.ddl ++ todos.ddl).create

     if (MTable.getTables.list(session).size < 2) {
        (patients.ddl ++ todos.ddl).create
        // Insert some fake data
        patients += Patient(1, "Doe", "John", "2005-01-01")
        patients += Patient(2, "Doe", "Jane", "2004-07-11")
        // users += User(1, "vu")
        // users += User(2, "john")
        todos += Todo(1, 1, "Replace catheter", 0)
        todos += Todo(2, 1, "Visit Mr. Johnson", 100)
        todos += Todo(3, 1, "See the attending at the end of the day", 100)
        todos += Todo(4, 1, "Turn patient", 100)
        todos += Todo(5, 2, "Retrieve labs", 0)
        todos += Todo(6, 2, "Insert vent", 100)
        todos += Todo(7, 2, "Oral vent care", 0)
    }

    val orderWS = WS.url(baseUrl + "DiagnosticOrder?_format=json").get().map{
      results =>
        Try(
          if (results.status == 200)
            (results.json)
          else
            throw new Exception(s"Error during request. ${results.status} ${results.body}")
        )
    }
    orderWS.map {
      case Success(json) => {
        val entries = (json \ "entry").as[JsArray]
        val orders = (entries).as[Seq[DiagnosticOrder]]
        for (order <- orders) {
          if (order.status.isDefined && order.items.isDefined){
            for (item <- order.items.get){
              val patientId = Try(order.subject.split("/").last.toLong).toOption
              val state = order.status.get match {
                case "requested" => 0
                case "received" => 0
                case "accepted" => 0
                case "in progress" => 1
                case "review" => 1
                case "completed" => 100
                case "suspended" => -1
                case "rejected" => -1
                case "failed" => -1
                case _ => 0
              }
              if (patientId.isDefined){
                db.withSession { implicit session =>
                  todos += Todo(0, patientId.getOrElse(1), item.text, state)
                }
              }
            }   
          }
        }
      }
        case Failure(e) => println(s"error: ${e.getMessage}")
    }
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
        val people = (entries).as[List[(String, String, String, String)]]
        for (person <- people) {
          db.withSession { implicit session =>
            patients += Patient(person._1.toLong, person._2, person._3, person._4)
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

  def filterTodos(patientId:Long) = Action{
    db.withSession{ implicit session =>
              val q = for {t <- todos if t.patientId === patientId} yield t 
      Ok(Json.toJson(q.list))
    }
  }  

  def changeTodoState(todoId: Long, state: Int) = Action{
    try {
      db.withSession { implicit session =>
        val q = for {t <- todos if t.id === todoId} yield t.state
        q.update(state).run
        Ok(s"$todoId state set to $state")
      }
    } catch {
        case _ => BadRequest(s"Error: $todoId state not changed.")
    }
  }

  def updateTodo = Action(BodyParsers.parse.json) { request =>
      val todoResult = request.body.validate[Todo]
      todoResult.fold(
        errors => {
          BadRequest("Failed to update note")
        },
        todo => {
          // Update the todo
          db.withSession { implicit session =>
            val q = for {t <- todos if t.id === todo.id} yield t.text
            q.update(todo.text).run
            Ok("Todo updated")
          }
        }
      )
  }
  
  def addTodo = Action { request =>
      // Add the todo
      val body: Option[String] = request.body.asText
      val note = request.queryString.get("note").flatMap(_.headOption)
      val mrn = request.queryString.get("mrn").flatMap(_.headOption)      
      if (note.isDefined){ 
          db.withSession { implicit session =>
            val q = for {t <- todos if t.patientId === mrn.get.toLong} yield t 
            if (q.list.size==0){
              patients += Patient(mrn.get.toLong, "", "", "2004-07-11")
            }

            val todoId = (todos returning todos.map(_.id)) += Todo(0,mrn.get.toLong,note.get, 0)

            Ok(Json.obj("status" ->"Ok", "id" -> todoId ))  
          }
      } else {
          Ok("SD")
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
    Ok(views.html.patientView(patientId))
  }

}