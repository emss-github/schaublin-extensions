/**
 * README
 * This extension is a utility related to numbers handling
 * 
 * Name: NumberUtil
 * Description: Numbers handling utilities
 * Date       Changed By            Description
 * 20240902   Eric Masson           Creation of utilities
 */

public class NumberUtil extends ExtendM3Utility {

  /**
   *  Transform a string parameter into an integer
   * @return integer
   */
  Integer parseStringToInteger(String parameter) {
    String wParameter = parameter.trim()
    if (wParameter.isInteger()) return Integer.valueOf(wParameter)
    else return 0
  }

  /**
   *  Transform a string parameter into an double
   * @return double
   */
  Double parseStringToDouble(String parameter) {
    String wParameter = parameter.trim()
    if (wParameter.isDouble()) return Double.valueOf(wParameter)
    else return 0d
  }

}
