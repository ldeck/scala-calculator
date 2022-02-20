package example

import org.scalacheck.Gen

class PostfixCalculatorSpec extends BaseFlatSpec {

  "a computer" should "return input digits when no arithmetic operators expressed" in {
    val calculator = PostfixCalculator(operationReducer = OperationReducer(operators = Map()))
    forAll((digits, "a"), (digits, "b"), (digits, "c")) { (a:Int, b:Int, c:Int) =>
      calculator.compute(List(a, b, c)) shouldEqual List(a, b, c)
    }
  }

  it should "be able to add two integers" in {
    val calculator = PostfixCalculator(operationReducer = OperationReducer(operators = Map('+' -> ((a: Int, b: Int) => 100))))
    forAll ((digits, "a"), (digits, "b")) { (a: Int, b: Int) =>
      val tokens = List[Int|Char](a, b, '+')
      calculator.compute(tokens) shouldEqual List(100)
    }
  }

  it should "be capable of more complex additions" in {
    val calculator = PostfixCalculator(operationReducer = OperationReducer(operators = Map('+' -> ((a: Int, b: Int) => a + b))))
    forAll ((digits, "a"), (digits, "b"), (digits, "c")) { (a: Int, b: Int, c: Int) =>
      val tokens = List[Int|Char](a, b, '+', c, '+')
      calculator.compute(tokens) shouldEqual List(a + b + c)
    }
  }

  it should "support operator precedence to multiply last pair first (like A + B * C)" in {
    val calculator = PostfixCalculator(operationReducer = OperationReducer(operators = Map(
      '+' -> ((a: Int, b: Int) => a + b),
      '*' -> ((a: Int, b: Int) => a * b),
    )))
    forAll ((digits, "a"), (digits, "b"), (digits, "c")) { (a: Int, b: Int, c: Int) =>
      val tokens = List[Int|Char](a, b, c, '*', '+')
      calculator.compute(tokens) shouldEqual List(a + b * c)
    }
  }

  it should "support operator precedence A + B * C + D" in {
    val calculator = PostfixCalculator(operationReducer = OperationReducer(operators = Map(
      '+' -> ((a: Int, b: Int) => a + b),
      '*' -> ((a: Int, b: Int) => a * b),
    )))
    forAll ((digits, "a"), (digits, "b"), (digits, "c"), (digits, "d")) { (a: Int, b: Int, c: Int, d: Int) =>
      val tokens = List[Int|Char](a, b, c, '*', '+', d, '+')
      calculator.compute(tokens) shouldEqual List(a + b * c + d)
    }
  }

  it should "support grouped maths like (A + B) * (C + D)" in {
    val calculator = PostfixCalculator(operationReducer = OperationReducer(operators = Map(
      '+' -> ((a: Int, b: Int) => a + b),
      '*' -> ((a: Int, b: Int) => a * b),
    )))
    forAll ((digits, "a"), (digits, "b"), (digits, "c"), (digits, "d")) { (a: Int, b: Int, c: Int, d: Int) =>
      val tokens = List[Int|Char](a, b, '+', c, d, '+', '*')
      calculator.compute(tokens) shouldEqual List((a + b) * (c + d))
    }
  }
}
