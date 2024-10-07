/**
 * README
 * This extension is an API transaction from EXT001MI
 * 
 * Name: Delete
 * Description: Delete a record from table MCWCCO
 * Date       Changed By            Description
 * 20240902   Eric Masson           Creation of transaction Delete
 * 20241007   Eric Masson           Post review fixes
 */
 
 public class Delete extends ExtendM3Transaction {
   
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final UtilityAPI utility
  private final LoggerAPI logger
  private final MICallerAPI miCaller
  
  int cono
  private String obv2
  private String obv3 

  public Delete(MIAPI mi, DatabaseAPI database, ProgramAPI program, UtilityAPI utility, LoggerAPI logger, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.program = program
    this.utility = utility
    this.logger = logger
    this.miCaller = miCaller
  }
  
  public void main() {
    cono = (Integer) program.getLDAZD().CONO

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
 
    Closure<Boolean> deleterCallback = { LockedResult lockedResult ->
      lockedResult.delete()
    }
    
    // If there is an existing record of this key, it is deleted
    if (!query.readLock(container, deleterCallback)) {
      // If there is no existing record of this key, an error is thrown
      mi.error("Record not found")
    }
  }
}
