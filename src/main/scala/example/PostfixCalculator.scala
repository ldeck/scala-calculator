package example

import scala.annotation.tailrec
import scala.collection.mutable

class PostfixCalculator(val operationReducer: OperandsListReducer):

  @tailrec
  private def compute(expression: List[Int|Char], stack: List[Int]): List[Int] = expression match {
    case Nil => stack.reverse
    case head :: tail => head match {
      case number: Int => compute(expression = tail, stack = List(number) ::: stack)
      case op: Char => compute(expression = tail, stack = operationReducer.reduceOne(numbers = stack, op = op))
    }
  }

  def compute(expression: List[Int|Char]): List[Int] = compute(expression, List())


