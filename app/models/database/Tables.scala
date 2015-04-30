package DB

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}
import play.api.libs.json._

import org.joda.time.LocalDateTime
import org.joda.time.DateTime

// Patients table
case class Patient(id: Long, nameFirst: String, nameLast: String, dob: String)
class Patients(tag: Tag) extends Table[Patient](tag, "PATIENTS") {
  //Primary key column
  def id = column[Long]("PATIENT_ID", O.PrimaryKey)
  def nameFirst = column[String]("NAME_FIRST")
  def nameLast = column[String]("NAME_LAST")
  def dob = column[String]("DOB")
  
  def * = (id, nameFirst, nameLast, dob) <> (Patient.tupled, Patient.unapply)
  //def * : ProvenShape[(Int, String, String)] = 
    //(id, nameFirst, nameLast)
}

case class Order(patientId: Long, description: String, location: String, complete: Boolean, 
                dtDue: String, dtCompleted: String, id: Option[Long] = None)
class Orders(tag: Tag) extends Table[Order](tag, "ORDERS") {
  def id = column[Long]("ORDER_ID", O.PrimaryKey)
  def patientId = column[Long]("PATIENT_ID")
  def description = column[String]("DESCRIPTION")
  def location = column[String]("LOCATION")
  def complete = column[Boolean]("COMPLETE")
  def dtDue = column[String]("DT_DUE")
  def dtCompleted = column[String]("DT_COMPMLETED")

  def * = (patientId, description, location, complete, dtDue, dtCompleted, id.?) <> (Order.tupled, Order.unapply)
  //def * : ProvenShape[(Int, Int, String, String, Boolean, String, String)] =
    //(id, patientID,  description, location, complete, dtDue, dtCompleted)
  
  def patient: ForeignKeyQuery[Patients, Patient] =
    foreignKey("PATIENT_ORDERS_FK", patientId, TableQuery[Patients])(_.id)
}
  
case class Lab(id: Long, description: String)
class Labs(tag: Tag) extends Table[Lab](tag, "LABS"){
  def id = column[Long]("LAB_ID", O.PrimaryKey)
  def description = column[String]("DESCRIPTION")
  def * = (id, description) <> (Lab.tupled, Lab.unapply)
}

case class Medication(id: Long, name: String)
class Medications(tag: Tag) extends Table[Medication](tag, "MEDICATIONS"){
  def id = column[Long]("MEDICATION_ID", O.PrimaryKey)
  def name = column[String]("NAME")
  
  def * = (id, name) <> (Medication.tupled, Medication.unapply)
}
  
case class Note(id: Long, text: String)
class Notes(tag: Tag) extends Table[Note](tag, "NOTES"){
  def id = column[Long]("NOTE_ID", O.PrimaryKey)
  def text = column[String]("TEXT")
  
  def * = (id, text) <> (Note.tupled, Note.unapply)
}

case class Todo(id: Long, patientId:String, text: String, state: Int)
class Todos(tag: Tag) extends Table[Todo](tag, "TODOS"){
  def id = column[Long]("TODO_ID", O.PrimaryKey, O.AutoInc)
  def patientId = column[String]("PATIENT_ID")
  def text = column[String]("TEXT")

  /*********
    * State Column will hold the current state of the todo
    * Leaving a gap between In Progress and Done in case we
    * need more states
    *  0:   New
    *  1:   In Progress
    *  100: Done
    * -1:   Removed from list
    */
  def state = column[Int]("STATE")

  def * = (id, patientId, text, state) <> (Todo.tupled, Todo.unapply)

}

case class Observation(id: Long, text:String)
class Observations(tag: Tag) extends Table[Observation](tag, "OBSERVATIONS"){
  def id = column[Long]("OBSERVATION_ID", O.PrimaryKey)
  def text = column[String]("TEXT")

  def * = (id, text) <> (Observation.tupled, Observation.unapply)
}
  
case class User(id: Long, username: String)
class Users(tag: Tag) extends Table[User](tag, "USERS"){
  def id = column[Long]("USER_ID", O.PrimaryKey)
  def username = column[String]("USERNAME")
  
  def * = (id, username) <> (User.tupled, User.unapply)
}