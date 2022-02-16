package example

import org.scalatest.*
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class OperatorSpec extends flatspec.AnyFlatSpec with Matchers with ScalaCheckPropertyChecks {

  private val plusOperator = PlusOperator()
  private val minusOperator = MinusOperator()
  private val timesOperator = TimesOperator()
  private val divideOperator = DivideOperator()

  "plus operator" should "add two numbers numbers together" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      plusOperator.calculate(a, b) shouldEqual (a + b)
    }
  }

  "minus operator" should "subtract a number from another" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      minusOperator.calculate(a, b) shouldEqual (a - b)
    }
  }

  "times operator" should "multiply two numbers together" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      timesOperator.calculate(a, b) shouldEqual (a * b)
    }
  }

  "divide operator" should "divide one number into another" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      whenever(b != 0) {
        divideOperator.calculate(a, b) shouldEqual (a / b)
      }
    }
  }

  it should "throw an illegal argument exception when dividing by zero" in {
    forAll ("a") { (a: Int) =>
      an [IllegalArgumentException] should be thrownBy {
        divideOperator.calculate(a, 0)
      }
    }
  }
}
