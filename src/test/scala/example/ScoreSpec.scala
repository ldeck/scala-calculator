package example

import org.scalatest.*
import org.scalatest.prop.TableDrivenPropertyChecks
import Score.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._

class ScoreSpec extends flatspec.AnyFlatSpec
  with Matchers
  with TableDrivenPropertyChecks
{

  behavior of "game scores"

  private val scores = Table(
    ("name", "value", "next"),
    ("Zero", "0", "Fifteen"),
    ("Fifteen", "15", "Thirty"),
    ("Thirty", "30", "Forty"),
    ("Forty", "40", "Advantage"),
    ("Advantage", "Adv", null)
  )

  forAll(scores) { (name: String, value: String, _) =>
    it should s"include ${name}" in {
      Score.valueOf(name).printVal shouldEqual value
    }
  }

  behavior of "score.next"

  forAll(scores) { (name: String, _, next: String) =>
    if (next == null)
      it should s"not be possible for ${name}" in {
        Score.valueOf(name).next shouldBe empty
      }
    else
      it should s"be ${name} -> ${next}" in {
        Score.valueOf(name).next should contain(Score.valueOf(next))
      }
  }
}
