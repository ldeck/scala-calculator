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
    ("Love", "0", "Fifteen"),
    ("Fifteen", "15", "Thirty"),
    ("Thirty", "30", "Forty"),
    ("Forty", "40", "Advantage"),
    ("Advantage", "Adv", "Love")
  )

  forAll(scores) { (name: String, value: String, _) =>
    it should s"include ${name}" in {
      Score.valueOf(name).printVal shouldEqual value
    }
  }

  behavior of "score.next (when winning)"

  it should "be Love -> 15" in {
    Love.next(true, Love) shouldEqual Fifteen
  }

  it should "be 15 -> 30" in {
    Fifteen.next(true, Love) shouldEqual Thirty
  }

  it should "be 30 -> 40" in {
    Thirty.next(true, Love) shouldEqual Forty
  }

  it should "be 40 -> Advantage if the opponent score is 40" in {
    Forty.next(true, Forty) shouldEqual Advantage
  }

  it should "be 40 -> Love if the opponent score is not 40" in {
    Forty.next(true, Thirty) shouldEqual Love
  }

  it should "be Advantage -> Love" in {
    Advantage.next(true, Forty) shouldEqual Love
  }


  behavior of "score.next (when losing)"

  for {
    score <- List(Love, Fifteen, Thirty, Forty)
    oppositionScore <- List(Love, Fifteen, Thirty)
  } {
    it should s"not advance for ${score} against opposition score ${oppositionScore}" in {
      score.next(false, oppositionScore) shouldEqual score
    }
  }

  it should "remain 40 when opposition score is 40" in {
    Forty.next(false, Forty) shouldEqual Forty
  }

  it should "be Love when opposition score was Advantage" in {
    Forty.next(false, Advantage) shouldEqual Love
  }

  for (score <- List(Love, Fifteen, Thirty)) {
    it should s"reset from ${score} to Love when opposition score was 40" in {
      score.next(false, Forty) shouldEqual Love
    }
  }

  it should "reset to 40 from Advantage" in {
    Advantage.next(false, Forty) shouldEqual Forty
  }
}
