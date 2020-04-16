package services

object MAPEService {
  def getPercentageError(prediction: Double, actual: Double): Double = {
    math.abs(actual-prediction) / actual
  }

  def calculateMAPE(predictions: Seq[Double], actuals: Seq[Double]): Double = {
    var errors :Double = 0
    predictions.zipWithIndex foreach{case(prediction,i) =>
      errors += getPercentageError(prediction, actuals(i))
    }

    errors / predictions.length
  }
}
