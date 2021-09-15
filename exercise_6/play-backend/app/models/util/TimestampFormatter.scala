package models.util

import play.api.libs.json.{Reads, Writes}

import java.sql.Timestamp

class TimestampFormatter {
  implicit val timestampReads: Reads[Timestamp] = {
    implicitly[Reads[Long]].map(new Timestamp(_))
  }

  implicit val timestampWrites: Writes[Timestamp] = {
    implicitly[Writes[Long]].contramap(_.getTime)
  }
}
