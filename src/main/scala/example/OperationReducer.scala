package example

class OperationReducer(val operators: Map[Char, Operator]):

  def reduceOne(numbers: List[Int], op: Char): List[Int] = numbers match {
    case a :: b :: tail => operators(op).calculate(a, b) :: tail
    case _ => throw IllegalStateException(s"""A postfix operator cannot be applied to a single value (${numbers.mkString("")})""")
  }

