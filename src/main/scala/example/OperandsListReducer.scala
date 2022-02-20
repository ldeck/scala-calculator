package example

class OperandsListReducer(val operators: Map[Char, Operator]):

  def reduceOne(numbers: List[Int], op: Char): List[Int] = numbers match {
    case a :: b :: tail => 
      operators.getOrElse(op, throw IllegalArgumentException(s"The postfix operator '$op' is unsupported!"))
      .calculate(a, b) :: tail

    case _ =>
      throw IllegalStateException(s"""A postfix operator cannot be applied to a single value (${numbers.mkString("")})""")
  }

