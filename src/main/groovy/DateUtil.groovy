/**
 * README
 * This extension is a utility related to date handling
 * 
 * Name: DateUtil
 * Description: Date handling utilities
 * Date       Changed By            Description
 * 20240902   Eric Masson           Creation of utilities
 */

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

public class DateUtil extends ExtendM3Utility {

  /**
   * Get date in yyyyMMdd format
   * @return date
   */
  String currentDateY8AsString() {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
  }

  /**
   * Get date in yyyyMMdd format
   * @return date
   */
  public int currentDateY8AsInt() {
    return Integer.valueOf(currentDateY8AsString());
  }


  /**
   * Get date in yyMMdd format
   * @return date
   */
  public String currentDateY6AsString() {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
  }

  /**
   * Get date in yyMMdd format
   * @return date
   */
  public int currentDateY6AsInt() {
    return Integer.valueOf(currentDateY6AsString());
  }

  /**
   * Get time in HHmmss format
   * @return time
   */
  public String currentTimeAsString() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
  }

  /**
   * Get time in HHmmss format
   * @return time
   */
  public int currentTimeAsInt() {
    return Integer.valueOf(currentTimeAsString());
  }

}
