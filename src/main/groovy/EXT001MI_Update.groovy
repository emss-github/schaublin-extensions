/**
 * README
 * This extension is an API transaction from EXT001MI
 * 
 * Name: Delete
 * Description: Update a record from table MCWCCO
 * Date       Changed By            Description
 * 20240902   Eric Masson           Creation of transaction Update
 * 20241007   Eric Masson           Post review fixes
 */
 
 public class Update extends ExtendM3Transaction {

  private final MIAPI mi;
  private final DatabaseAPI database
  private final ProgramAPI program
  private final UtilityAPI utility
  private final LoggerAPI logger
  private final MICallerAPI miCaller
  
  int cono
  String chid
  int chno
  private String obv2
  private String obv3
  
  public Update(MIAPI mi, DatabaseAPI database, ProgramAPI program, UtilityAPI utility, LoggerAPI logger, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.program = program
    this.utility = utility
    this.logger = logger
    this.miCaller = miCaller
  }
  

  public void main() {
    cono = (Integer) program.getLDAZD().CONO
    chid = program.getUser()
  
    // Check FRDT
    if (!utility.call("DateUtil","isDateValid", mi.inData.get("FRDT", "yyyyMMdd"))) {
      mi.error("From date " + mi.inData.get("FRDT").trim() + " is invalid")
      return
    }
    
    // Select fields to handle from table MCWCCO
    DBAction query = database.table("MCWCCO")
    .index("00")
    .build()
    DBContainer container = query.getContainer()
    // Set the key fields of the record to delete
    obv2 = mi.inData.get("OBV2") == null ? "" : mi.inData.get("OBV2").trim()
    obv3 = mi.inData.get("OBV3") == null ? "" : mi.inData.get("OBV3").trim()
    container.set("KECONO", cono)
    container.set("KEFACI", mi.inData.get("FACI").trim())
    container.set("KECCOM", mi.inData.get("CCOM").trim())
    container.set("KEPCTP", mi.inData.get("PCTP").trim())
    container.set("KEOBV1", mi.inData.get("OBV1").trim())
    container.set("KEOBV2", obv2)
    container.set("KEOBV3", obv3)
    container.set("KEFRDT", utility.call("DateUtil","dateY8AsInt", mi.inData.get("FRDT")))
     
    Closure<Boolean> updateCallBack = { LockedResult lockedResult ->
      checkDoubleEmptyField("KECDPR", mi.inData.get("CDPR"), lockedResult)

      // The change number is retrieved from the existing record and is being added 1
      chno = lockedResult.get("KECHNO")
      chno++;
      lockedResult.set("KELMDT", utility.call("DateUtil","currentDateY8AsInt"))
      lockedResult.set("KECHNO", chno)
      lockedResult.set("KECHID", chid)
      lockedResult.update()
    }
    
    // If there is an existing record of this key, the fields are set and the record is updated in the table
    if (!query.readLock(container, updateCallBack)) {
      // If the record doesn't already exist in the table, an error is thrown
      mi.error("Record not found")
    }
  }
  
  /**
   * Check if the input field is empty. If it is not empty, the value is set to the record
   * @param fieldToSet: the name of the field to set in the table 
   * @param fieldToCheck: the input field of the API to control
   * @param recordToModify: the record that will be changed in the table
   */
  String checkEmptyField(String fieldToSet, String fieldToCheck, LockedResult recordToModify) {
    String field = fieldToCheck.trim()
    if (!field.isEmpty()) {
      recordToModify.set(fieldToSet, field)
    }
  }

  /**
   * Check if the input field is empty. If it is not empty, the value is set to the record
   * @param fieldToSet: the name of the field to set in the table 
   * @param fieldToCheck: the input field of the API to control
   * @param recordToModify: the record that will be changed in the table
   */
  String checkDoubleEmptyField(String fieldToSet, String fieldToCheck, LockedResult recordToModify) {
    String field = fieldToCheck.trim()
    if (!field.isEmpty()) {
      recordToModify.set(fieldToSet, utility.call("NumberUtil","parseStringToDouble", field))
    }
  }
   
 }
