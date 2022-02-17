package example

class OperatorSpec extends BaseFlatSpec {

  "plus operator" should "add two numbers numbers together" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      PlusOperator().calculate(a, b) shouldEqual (a + b)
    }
  }

  "minus operator" should "subtract a number from another" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      MinusOperator().calculate(a, b) shouldEqual (a - b)
    }
  }

  "times operator" should "multiply two numbers together" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      TimesOperator().calculate(a, b) shouldEqual (a * b)
    }
  }

  "divide operator" should "divide one number into another" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      whenever(b != 0) {
        DivideOperator().calculate(a, b) shouldEqual (a / b)
      }
    }
  }

  it should "throw an illegal argument exception when dividing by zero" in {
    forAll ("a") { (a: Int) =>
      an [IllegalArgumentException] should be thrownBy {
        DivideOperator().calculate(a, 0)
      }
    }
  }
}
