package extensions

import reactivemongo.bson.BSONObjectID

/**
 * Application route binder for [[String]] to [[reactivemongo.bson.BSONObjectID]] converting.
 */
object Binders {

  /**
   * For the path binding.
   */
  implicit object pathBindableBSONObjectID
    extends play.api.mvc.PathBindable.Parsing[BSONObjectID](
      BSONObjectID(_),
      _.stringify,
      (key: String, e: Exception) =>
        "Cannot parse parameter %s as BSONObjectID: %s".format(key, e.getMessage)
    )

  /**
   * For the query string binding.
   */
  implicit object queryStringBindableBSONObjectID
    extends play.api.mvc.QueryStringBindable.Parsing[BSONObjectID](
      BSONObjectID(_),
      _.stringify,
      (key: String, e: Exception) =>
        "Cannot parse parameter %s as BSONObjectID: %s".format(key, e.getMessage)
    )
}