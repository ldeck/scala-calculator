package example

import example.Score.{Advantage, Forty, Love}

enum Score(val printVal: String):
  def next(didWin: Boolean, opponentScore: Score): Score =
    if (didWin) this match {
      case Forty => if (opponentScore == Forty) Advantage else Love
      case _     => Score.values.drop(ordinal + 1).headOption.getOrElse(Love)
    }
    else opponentScore match {
      case Advantage => Love
      case Forty => this match {
        case Advantage => Forty
        case Forty => this
        case _ => Love
      }
      case _ => this
    }

  case Love extends Score("0")
  case Fifteen extends Score("15")
  case Thirty extends Score("30")
  case Forty extends Score("40")
  case Advantage extends Score("Adv")
end Score
