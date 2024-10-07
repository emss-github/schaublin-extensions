/**
 * README
 * This extension is an API transaction from EXT001MI
 * 
 * Name: Add
 * Description: Add a record in table MCWCCO
 * Date       Changed By            Description
 * 20240902   Eric Masson           Creation of transaction Add
 * 20241007   Eric Masson           Post review fixes
 */
 
public class Add extends ExtendM3Transaction {

  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final UtilityAPI utility
  private final LoggerAPI logger
  private final MICallerAPI miCaller
  
  private int cono
  private String chid
  private String sCono
  private String obv2
  private String obv3 

  public Add(MIAPI mi, DatabaseAPI database, ProgramAPI program, UtilityAPI utility, LoggerAPI logger, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.program = program
    this.utility = utility
    this.logger = logger
    this.miCaller = miCaller
  }
  
  public void main() {
    cono = (Integer) program.getLDAZD().CONO
    sCono = program.getLDAZD().CONO
    chid = program.getUser()
    
    // Check FACI
    if (!checkFACIExists(mi.inData.get("FACI").trim())) {
      mi.error("Facility " + mi.inData.get("FACI").trim() + " does not exist")
      return
    }
    
    // Check CCOM MCCOMP
    if (!checkCCOMExists(mi.inData.get("CCOM").trim())) {
      mi.error("Costing component " + mi.inData.get("CCOM").trim() + " does not exist")
      return
    }
    
    // Check PCTP PCS005MI/Get 
    if (!checkPCTPExists(mi.inData.get("PCTP").trim())) {
      mi.error("Costing type " + mi.inData.get("PCTP").trim() + " does not exist")
      return
    }
    
    // Check OBV1 PDS010MI/Get 
    if (!checkPLGRExists(mi.inData.get("FACI").trim(), mi.inData.get("OBV1").trim())) {
      mi.error("Work center " + mi.inData.get("OBV1").trim() + " does not exist")
      return
    }
    
    // Check FRDT
    if (!utility.call("DateUtil","isDateValid", mi.inData.get("FRDT", "yyyyMMdd"))) {
      mi.error("From date " + mi.inData.get("FRDT").trim() + " is invalid")
      return
    }
    
    // Select fields to handle from table MCWCCO
    DBAction query = database.table("MCWCCO")
    .index("00")
      .selection("KECONO", "KEFACI", "KECCOM", "KEPCTP", "KEOBV1", "KEOBV2", "KEOBV3", "KEFRDT",
                  "KECDPR", "KERGDT", "KERGTM", "KELMDT", "KECHNO", "KECHID")
    .build()

    DBContainer container = query.getContainer()
    // Set the key fields
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
    
    // If there is no existing record of this key, the other fields are set and the record is inserted in the table
    if (!query.read(container)) {

      container.set("KECDPR", utility.call("NumberUtil","parseStringToDouble", mi.inData.get("CDPR")))
      container.set("KERGDT", utility.call("DateUtil","currentDateY8AsInt"))
      container.set("KERGTM", utility.call("DateUtil","currentTimeAsInt"))
      container.set("KELMDT", utility.call("DateUtil","currentDateY8AsInt"))
      container.set("KECHNO", 1)
      container.set("KECHID", chid)
    
      query.insert(container)
    } else {
      // If the record already exists in the table, an error is thrown
      mi.error("Record already exists")
    }
  }
  
  /**
   * checkFACIExists - Database access to CFACIL as CRS008MI/Get fails
   */
  private boolean checkFACIExists(String FACI) {
    DBAction CFACIL = database.table("CFACIL").index("00").selection("CFCONO", "CFFACI", "CFFACN", "CFDIVI", "CFWHLO").build()
    DBContainer container = CFACIL.getContainer()
    container.set("CFCONO", cono)
    container.set("CFFACI", FACI)
    return CFACIL.read(container)
  }

  /**
   * checkCCOMExists - Database access to MCCOMP as PDS010MI/Get fails
   */
  private boolean checkCCOMExists(String CCOM) {
    DBAction MCCOMP = database.table("MCCOMP").index("00").selection("KCCONO", "KCCCOM", "KCTX40").build()
    DBContainer container = MCCOMP.getContainer()
    container.set("KCCONO", cono)
    container.set("KCCCOM", CCOM)
    return MCCOMP.read(container)
  }

  /**
   * checkPCTPExists - Database access to MCCOTY as PCS005MI/Get fails
   */
  private boolean checkPCTPExists(String PCTP) {
    DBAction MCCOTY = database.table("MCCOTY").index("00").selection("KBCONO", "KBPCTP", "KBTX40").build()
    DBContainer container = MCCOTY.getContainer()
    container.set("KBCONO", cono)
    container.set("KBPCTP", PCTP)
    return MCCOTY.read(container)
  }

  /**
   * checkPLGRExists - Calls PDS010MI/Get to check whether work center exists
   */
  private boolean checkPLGRExists(String FACI, String PLGR) {
    DBAction MPDWCT = database.table("MPDWCT").index("00").selection("PPCONO", "PPFACI", "PPPLGR", "PPPLNM").build()
    DBContainer container = MPDWCT.getContainer()
    container.set("PPCONO", cono)
    container.set("PPFACI", FACI)
    container.set("PPPLGR", PLGR)
    return MPDWCT.read(container)
  }
}
