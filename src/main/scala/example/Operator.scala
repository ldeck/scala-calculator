package example

trait Operator:
  def calculate(a: Int, b: Int): Int

class PlusOperator extends Operator {
  override def calculate(a: Int, b: Int): Int = a + b
}

class MinusOperator extends Operator {
  override def calculate(a: Int, b: Int): Int = a - b
}

class TimesOperator extends Operator {
  override def calculate(a: Int, b: Int): Int = a * b
}

class DivideOperator extends Operator {
  override def calculate(a: Int, b: Int): Int = {
    b match {
      case 0 => throw IllegalArgumentException("Cannot divide by zero!")
      case _ => a / b
    }
  }
}

