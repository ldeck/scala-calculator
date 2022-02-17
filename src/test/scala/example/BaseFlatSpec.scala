package example

import org.scalacheck.Gen
import org.scalatest.flatspec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

trait BaseFlatSpec extends flatspec.AnyFlatSpec with Matchers with ScalaCheckPropertyChecks {

}
