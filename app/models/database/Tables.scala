package DB

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}
import play.api.libs.json._

import org.joda.time.LocalDateTime
import org.joda.time.DateTime

// Patients table
case class Patient(nameFirst: String, nameLast: String, id: Option[Int] = None)
class Patients(tag: Tag) extends Table[Patient](tag, "PATIENTS") {
  //Primary key column
  def id = column[Int]("PATIENT_ID", O.PrimaryKey, O.AutoInc)
  def nameFirst = column[String]("NAME_FIRST")
  def nameLast = column[String]("NAME_LAST")
  
  def * = (nameFirst, nameLast, id.?) <> (Patient.tupled, Patient.unapply)
  //def * : ProvenShape[(Int, String, String)] = 
    //(id, nameFirst, nameLast)
}

case class Order(patientId: Int, description: String, location: String, complete: Boolean, 
                dtDue: String, dtCompleted: String, id: Option[Int] = None)
class Orders(tag: Tag) extends Table[Order](tag, "ORDERS") {
  def id = column[Int]("ORDER_ID", O.PrimaryKey)
  def patientId = column[Int]("PATIENT_ID")
  def description = column[String]("DESCRIPTION")
  def location = column[String]("LOCATION")
  def complete = column[Boolean]("COMPLETE")
  def dtDue = column[String]("DT_DUE")
  def dtCompleted = column[String]("DT_COMPMLETED")

  def * = (patientId, description, location, complete, dtDue, dtCompleted, id.?) <> (Order.tupled, Order.unapply)
  //def * : ProvenShape[(Int, Int, String, String, Boolean, String, String)] =
    //(id, patientID,  description, location, complete, dtDue, dtCompleted)
  
  def patient: ForeignKeyQuery[Patients, Patient] =
    foreignKey("PATIENT_FK", patientId, TableQuery[Patients])(_.id)
}
  
case class Lab(id: Int, description: String)
class Labs(tag: Tag) extends Table[Lab](tag, "LABS"){
  def id = column[Int]("LAB_ID", O.PrimaryKey)
  def description = column[String]("DESCRIPTION")
  def * = (id, description) <> (Lab.tupled, Lab.unapply)
}

case class Medication(id: Int, name: String)
class Medications(tag: Tag) extends Table[Medication](tag, "MEDICATIONS"){
  def id = column[Int]("MEDICATION_ID", O.PrimaryKey)
  def name = column[String]("NAME")
  
  def * = (id, name) <> (Medication.tupled, Medication.unapply)
}
  
case class Note(id: Int, text: String)
class Notes(tag: Tag) extends Table[Note](tag, "NOTES"){
  def id = column[Int]("NOTE_ID", O.PrimaryKey)
  def text = column[String]("TEXT")
  
  def * = (id, text) <> (Note.tupled, Note.unapply)
}

case class Todo(id: Int, text: String)
class Todos(tag: Tag) extends Table[Todo](tag, "TODOS"){
  def id = column[Int]("TODO_ID", O.PrimaryKey)
  def text = column[String]("TEXT")
  
  def * = (id, text) <> (Todo.tupled, Todo.unapply)
}

case class Observation(id: Int, text:String)
class Observations(tag: Tag) extends Table[Observation](tag, "OBSERVATIONS"){
  def id = column[Int]("OBSERVATION_ID", O.PrimaryKey)
  def text = column[String]("TEXT")
  def * = (id, text) <> (Observation.tupled, Observation.unapply)
}
  
case class User(id: Int, username: String)
class Users(tag: Tag) extends Table[User](tag, "USERS"){
  def id = column[Int]("USER_ID", O.PrimaryKey)
  def username = column[String]("USERNAME")
  
  def * = (id, username) <> (User.tupled, User.unapply)
}