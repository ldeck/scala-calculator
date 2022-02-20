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
