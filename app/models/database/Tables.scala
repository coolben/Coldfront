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

class Orders(tag: Tag) extends Table[(Int, Int, String, String, Boolean, String, String)](tag, "ORDERS") {
  def id: Column[Int] = column[Int]("ORDER_ID", O.PrimaryKey)
  def patientID: Column[Int] = column[Int]("PATIENT_ID")
  def description: Column[String] = column[String]("DESCRIPTION")
  def location: Column[String] = column[String]("LOCATION")
  def complete: Column[Boolean] = column[Boolean]("COMPLETE")
  def dtDue: Column[String] = column[String]("DT_DUE")
  def dtCompleted: Column[String] = column[String]("DT_COMPMLETED")

  def * : ProvenShape[(Int, Int, String, String, Boolean, String, String)] =
    (id, patientID,  description, location, complete, dtDue, dtCompleted)
  
  def patient: ForeignKeyQuery[Patients, Patient] =
    foreignKey("PATIENT_FK", patientID, TableQuery[Patients])(_.id)
}
  
class Labs(tag: Tag)
  extends Table[(Int)](tag, "LABS"){
      def id: Column[Int] = column[Int]("LAB_ID", O.PrimaryKey)
      
      def * : ProvenShape[(Int)] = 
        (id)
}
  
class Medications(tag: Tag)
  extends Table[(Int)](tag, "MEDICATIONS"){
      def id: Column[Int] = column[Int]("MEDICATION_ID", O.PrimaryKey)
      
      def * : ProvenShape[(Int)] = 
        (id)
}
  
class Notes(tag: Tag)
  extends Table[(Int)](tag, "NOTES"){
      def id: Column[Int] = column[Int]("NOTE_ID", O.PrimaryKey)
      
      def * : ProvenShape[(Int)] = 
        (id)
}
  
class Todos(tag: Tag)
  extends Table[(Int)](tag, "TODOS"){
      def id: Column[Int] = column[Int]("TODO_ID", O.PrimaryKey)
      
      def * : ProvenShape[(Int)] = 
        (id)
}

class Observations(tag: Tag)
  extends Table[(Int)](tag, "OBSERVATIONS"){
      def id: Column[Int] = column[Int]("OBSERVATION_ID", O.PrimaryKey)
      
      def * : ProvenShape[(Int)] = 
        (id)
}
  
class Users(tag: Tag)
  extends Table[(Int, String)](tag, "USERS"){
      def id: Column[Int] = column[Int]("USER_ID", O.PrimaryKey)
      def username: Column[String] = column[String]("USERNAME")
      
      def * : ProvenShape[(Int, String)] = 
        (id, username)
}