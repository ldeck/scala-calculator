package example

enum Score(val printVal: String):
  def next: Option[Score] = Score.values.drop(ordinal + 1).headOption

  case Love extends Score("0")
  case Fifteen extends Score("15")
  case Thirty extends Score("30")
  case Forty extends Score("40")
  case Advantage extends Score("Adv")
end Score
