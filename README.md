# scala-calculator #

This is a basic example of a postfix calculator in scala, developed using tdd.

## The aim ##

To develop a calculator using postfix notation, driving the design and features by tests.

In the process of doing so, to experiment with [scala](https://www.scala-lang.org) 3 itself and test the level of IDE assistance available for practical daily use.

## Postfix notation ##

Quote from [wiki.c2.com/?PostfixNotation](https://wiki.c2.com/?PostfixNotation):

> Postfix also known as Reverse Polish Notation (or RPN), is a notational system where the operation/function follows the arguments.

> For example, "1 2 add" would be postfix notation for adding the numbers 1 and 2.

> Most programming languages use either prefix notation ("add(1, 2)" or "(add 1 2)") or infix notation ("1 add 2" or "1 + 2"). Prefix and infix are more familiar to most people, as they are the standard notations used for arithmetic and algebra. Many people wonder why anyone would use this "weird" postfix notation.

> The answer is that it is useful, especially for programming, because it clearly shows the order in which operations are performed, and because it disambiguates operator groupings. For example, look at this postfix expression:

>     1 2 + 3 * 6 + 2 3 + /

## Examples comparing infix with postfix notation ##

Examples include:

| Infix expression  | Postfix expression |
|-------------------|--------------------|
| A + B             | A B +              |
| A + B * C         | A B C * +          |
| A + B * C + D     | A B C * + D +      |
| (A + B) * (C + D) | A B + C D + *      |
| A * B + C * D     | A B * C D * +      |
| A + B + C + D     | A B + C + D +      |

## Details ##

The primary PostfixCalculator class will expose a single public function:

```scala
    def reduce(inputs: List[Int|Char]): List[Int|Char]
```

Why return a list? Consider the following incomplete extressions:

- `A B`
- `A B + C`
- `A B * +`

Are these an error or an opportunity to show an in-progress worksheet from a user? That depends on the application context. i.e., such a calculation reducer can be plugged into concrete implementations to decide.

We'll concentrate here on the core postfix implementation.

## Steps ##

### Step 1: define base test / spec trait ###

Let's define a BaseFlatSpec trait to base our various test/spec files off.

NB: I'm not specifying scalatest's flat spec as a preferred style here; just another one to try out.

```scala
package example

import org.scalacheck.Gen
import org.scalatest.flatspec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

trait BaseFlatSpec extends flatspec.AnyFlatSpec with Matchers with ScalaCheckPropertyChecks
```

### Step 2: Define plus operator behaviour ###

Defining the behaviour of a simple plus operator seems like a good place to start. Notice that a plus '+' operator must takes two operands on its left and right. Thus in postfix notation it will apply to two preceding numbers.

e.g., `A B + == A + B` and `A B C * + == A + B * C`

Our PlusOperatorSpec can begin like so:

```scala
package example

class OperatorSpec extends BaseFlatSpec {

  "a plus operator" should "add two numbers together" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      PlusOperator().compute(a, b) shouldEqual (a + b)
    }
  }
}
```

Pretty simple. The implementation then begins...

```scala
class PlusOperator:
  def compute(a: Int, b: Int): Int = ???
```

We run the test. It naturally compiles but fails being unimplemented. Replacing the '???' with `a + b` and rerunning the test passes.

### Step 3: times operator ###

Let's define one more operator for now to allow multiplying numbers: '*'.

Our test case is not dissimilar to the plus case:

```scala
  "a times operator" should "multiply two numbers together" in {
    forAll ("a", "b") { (a: Int, b: Int) =>
      TimesOperator().compute(a, b) shouldEqual (a * b)
    }
  }
```

And to allow it to compile we add the basic implementation:

```scala
class TimesOperator:
  def compute(a: Int, b: Int): Int = 0
```

We expect this will fail as we're returning zero. Seeing it does fail, we fix this by returning `a * b`.

### Step 4: refactor ###

By this time we can define a common interface for operators in our Operator.scala file:

```scala
package example

trait Operator:
  def compute(a: Int, b: Int): Int

class PlusOperator extends Operator:
  override def compute(a: Int, b: Int): Int = a + b

class TimesOperator extends Operator:
  override def compute(a: Int, b: Int): Int = a * b

```

### Step 5: define operands reducer ###

After some thinking time to reflect on what to do next...

You'll recall that the input expression to our PostfixCalculator is a `List[Int|Char]` (in its simplest form). The 'Char' allows operator chars like '+' and '*' to be expressed. All other items (the operands) are to be operated on.

What occurs to the list when two operands are operated on? The list reduces in size by 1 as two operands are computed into a, possibly intermediate, result.

So what we need is an OperandsListReducer. Let's start with the simplest case, replacing a list of two numbers with their operated result.

Let's assume there was an operator '!' that when applied to two operands returns 100.

```scala
class OperandsListReducerSpec extends BaseFlatSpec:

  "an operands list reducer" should "replace the pair of numbers in the given collection with their calculated value" in {
    val reducer = OperandsListReducer(Map('!' -> ((a: Int, b: Int) => 100)))
    reducer.reduceOne(List(1, 2), '!') shouldEqual List(100)
  }

```

The starting (failed) implementation:

```scala
package example

class OperandsListReducer(val operators: Map[Char, Operator]):

  def reduceOne(numbers: List[Int], op: Char): List[Int] = ???
```

Since our operators require two operands to work on we can use scala's pattern matching to extract them, compute their replacement value and return it as a list to satisy this test case:

```scala
  def reduceOne(operands: List[Int], op: Char): List[Int] =
    List(operands(op).compute(operands.head, operands.last)
```

This is clearly not satisfactory as so far we're discarding possibly extra operands that may be required for subsequent operations.

The question is, do we reduce from the head of the list or the tail? We don't want to think about operator precedence as yet. So let's just reduce from the head of the list, which is the simplest thing we can do.

```scala
  it should "replace the first two items in the list with their calculated value" in {
    val reducer = OperandsListReducer(Map('@' -> ((a: Int, b: Int) => 200)))
    reducer.reduceOne(List(1, 2, 3), '@') shouldEqual List(200, 3)
  }
```

Now we can update the implementation to pattern match on the input like so:

```scala
  def reduceOne(operands: List[Int], op: Char): List[Int] = operands match {
    case a :: b :: tail => operands(op).compute(a, b) :: tail
    case _ => operands  // default case required to satisfy compilation only
```

Are we done? Clearly not. There's 2 possible failed states:
  * What if we don't have at least two operands?
  * What if the operation `op` isn't defined?

```scala
  it should "throw an illegal state exception when less than two operands are supplied" in {
    val reducer = OperandsListReducer(Map('#' -> ((a: Int, b: Int) => 300)))
    forAll { (a: Int) =>
      the[IllegalStateException] thrownBy {
        reducer.reduceOne(List(a), '#')
      } should have message s"A postfix operator cannot be applied to a single value ($a)"
    }
  }
```

The updated default case:

```scala
    case _ =>
      throw IllegalStateException(s"""A postfix operator cannot be applied to a single value (${numbers.mkString("")})""")
```

Next, let's use an illegal argument exception for an unknown operator:

```scala
  it should "return an error when the operator is not supported" in {
    val reducer = OperandsListReducer(operators = Map.empty)
    forAll { (a: Int, b: Int, op: Char) =>
      the[IllegalArgumentException] thrownBy {
        reducer.reduceOne(List(a, b), op)
      } should have message s"The postfix operator '$op' is unsupported!"
    }
  }
```

The updated implementation:

```scala
    case a :: b :: tail =>
      operators.getOrElse(op, throw IllegalArgumentException(s"The postfix operator '$op' is unsupported!"))
      .calculate(a, b) :: tail
```

