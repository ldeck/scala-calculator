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
    def reduce(inputs: List[Int|Char]): List[Int]
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

### Step 6: circle back to PostfixCalculator ###

We should now be in a position to supply a basic expression `A B +` and obtain a result.

But first, let's just return the input expression if no operands are supplied.

```scala
package Example

class PostfixCalculatorSpec extends BaseFlatSpec {

  "a postfix calculator" should "return input digits when no arithmetic operators expressed" in {
    val calculator = PostfixCalculator(operationReducer = OperandsListReducer(operators = Map()))
    forAll("a", "b", "c") { (a:Int, b:Int, c:Int) =>
      calculator.compute(List(a, b, c)) shouldEqual List(a, b, c)
    }
  }
```

The simplest implementation?

```scala
class PostfixCalculator(val operationReducer: OperandsListReducer):
  def compute(expression: List[Int|Char]): List[Int] = expression.asInstanceOf[List[Int]]
```

It's not typesafe, but we'll be writing more test cases, right? Yes.

Now let's add two numbers:

```scala
  it should "be able to add two integers" in {
    val calculator = PostfixCalculator(operationReducer = OperandsListReducer(operators = Map('+' -> ((a: Int, b: Int) => 100))))
    forAll ("a", "b") { (a: Int, b: Int) =>
      val tokens = List[Int|Char](a, b, '+')
      calculator.compute(tokens) shouldEqual List(100)
    }
  }
```

We're going to be cheeky to force some better tests.

```scala
class PostfixCalculator(val operationReducer: OperandsListReducer):
  def compute(expression: List[Int|Char]): List[Int] = expression.size match {
    case 2 => expression.asInstanceOf[List[Int]]
    case _ => List(100)
  }
```

Okay, this should do it?

```scala
  it should "be capable of more complex additions" in {
    val calculator = PostfixCalculator(operationReducer = OperandsListReducer(operators = Map('+' -> ((a: Int, b: Int) => a + b))))
    forAll ("a", "b", "c") { (a: Int, b: Int, c: Int) =>
      val tokens = List[Int|Char](a, b, '+', c, '+')
      calculator.compute(tokens) shouldEqual List(a + b + c)
    }
  }
```

Don't be so quick!

```scala
class PostfixCalculator(val operationReducer: OperandsListReducer):
  def compute(expression: List[Int|Char]): List[Int] = expression.size match {
    case 2 => expression.asInstanceOf[Int]
    case 3 => List(100)
    case _ => List(expression.filter(_.isInstanceOf[Int]).asInstanceOf[List[Int]].sum)
  }
```

### Step 7: postfix calculation recursion ###

Okay, to do this properly we need to up the game and include multiple operators. e.g., '+' and '*'.

```scala
  it should "support operator precedence to multiply last pair first (like A + B * C)" in {
    val calculator = PostfixCalculator(operationReducer = OperandsListReducer(operators = Map(
      '+' -> ((a: Int, b: Int) => a + b),
      '*' -> ((a: Int, b: Int) => a * b),
    )))
    forAll ((digits, "a"), (digits, "b"), (digits, "c")) { (a: Int, b: Int, c: Int) =>
      val tokens = List[Int|Char](a, b, c, '*', '+')
      calculator.compute(tokens) shouldEqual List(a + b * c)
    }
  }
```

This is trickier now.

- We need to parse a, b and c without yet knowing what to do with them. We covered that above, the result being they are returned uncalculated.
- Then we parse an operator, and we have to decide which operands to operate on since we have three of them. Recall that an operator only operates on two inputs. This is where operator precedence can help us decide.
- The idea is that any operator reduces the 'stack' by taking two numbers and replacing them with a single value. But which two?
- We can observe that it is always reducing from right to left in postfix notation to honour operator precedence. Thus we always pop the last two items and append their result which eventually results in a single value.

For example:
```
1 2 + 3 * 6 + 2 3 + /
```

This is equivalent to:
```
(((1 + 2) * 3) + 6) / (2 + 3)
```

To do this, we'll define another function that takes an empty 'stack' of operands by default. For each element, we'll prepend it to the stack if it's a number and recurse.

```scala
  @tailrec
  private def compute(expression: List[Int|Char], stack: List[Int]): List[Int] = expression match {
    case Nil => stack.reverse
    case head :: tail => head match {
      case number: Int => compute(expression = tail, stack = List(number) ::: stack)
      case op: Char => compute(expression = tail, stack = operationReducer.reduceOne(numbers = stack, op = op))
    }
  }

  def compute(expression: List[Int|Char]): List[Int] = compute(expression, List())
```

NB: operating on the last two items is the same as reversing the order of the list, taking the first two items, calculating their operationp, prepending their result and ultimately returning the resulting stack reversed again. This sounds more complex than it is, but essentially makes pattern matching easier.

Annotating with `scala.annotation.tailrec` verifies that the method will be compiled with tail call optimisation. The compiler will issue an error if the function cannot be optimised into a loop.

## IDE and Build Notes ##

I've used IntelliJ IDEA 2021.3.2 with relevant scala plugins for development. SBT was used for the build. Both, it must be said, were an unfortunate choice. See below for more details.

### IntelliJ ###

#### IntelliJ Version Used ####

```
IntelliJ IDEA 2021.3.2 (Ultimate Edition)
Subscription is active until August 26, 2022.
Runtime version: 11.0.13+7-b1751.25 x86_64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
macOS 12.1
GC: G1 Young Generation, G1 Old Generation
Memory: 2048M
Cores: 16

Registry:
    analyze.exceptions.on.the.fly=true
    js.debugger.webconsole=false
    scala.erase.compiler.process.jdk.once=false

Non-Bundled Plugins:
    systems.fehn.intellijdirenv (0.2.3)
    com.mle.idea.sbtexecutor (1.4.1)
    org.intellij.scala (2021.3.18)
```

#### Pain Points: Scala plugin? ####

I generally found that IntelliJ was unable to provide help for a number of things in Scala that would ordinarily be easy in Java:
- Whilst creating a test for a desired type or function that did not as yet exist, IntelliJ refused to offer any assistance in generating these for Scala.
- If I wanted to debug a test spec, this wasn't always possible. This may be due to the syntactic sugar of certain styles of ScalaTest, particularly for property-based testing.
- Some imports were not suggested, having to be added by hand. Ouch.

That was disappointing, since the Scala language itself is nice.

### SBT ###

Let's just say that the summary of problems I experienced were:

1. SBT takes an age for an sbt project to resolve its dependencies, especially when firstly generating the project. Expect long delays.
2. SBT's initialisation / compilation is slow
3. SBT's syntax is not simple.

In short, SBT stands for "Simple Build Tool", but is not only not simple but sluggish.

A more complete critique is [here](https://www.lihaoyi.com/post/SowhatswrongwithSBT.html).
