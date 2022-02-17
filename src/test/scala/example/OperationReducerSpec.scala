package example

import org.scalacheck.Gen

class OperationReducerSpec extends BaseFlatSpec:

  "an operation reducer" should "replace the pair of numbers in the given collection with their calculated value" in {
    val reducer = OperationReducer(Map('!' -> ((a: Int, b: Int) => 100)))
    reducer.reduceOne(List(1, 2), '!') shouldEqual List(100)
  }

  it should "replace the first two items in the list with their calculated value" in {
    val reducer = OperationReducer(Map('@' -> ((a: Int, b: Int) => 200)))
    reducer.reduceOne(List(1, 2, 3), '@') shouldEqual List(200, 3)
  }

  it should "return an error when less than two numbers are supplied" in {
    val reducer = OperationReducer(Map('#' -> ((a: Int, b: Int) => 300)))
    forAll(digits) { (a: Int) =>
      the[IllegalStateException] thrownBy {
        reducer.reduceOne(List(a), '#')
      } should have message s"A postfix operator cannot be applied to a single value ($a)"
    }
  }

  it should "return an error when the operator is not supported" in {
    val reducer = OperationReducer(Map('$' -> ((a: Int, b: Int) => 300)))
    val invalidOperators: Gen[Char] = for (n <- Gen.asciiChar.filter(_ != '$')) yield n
    forAll(digits, digits, invalidOperators) { (a: Int, b: Int, op: Char) =>
      the[IllegalArgumentException] thrownBy {
        reducer.reduceOne(List(a, b), op)
      } should have message s"The postfix operator '$op' is unsupported!"
    }
  }
