package pt.unl.fct.di.apdc.individualproject.util;




public class RoleJson {



  public String valueType;
  public boolean excludeFromIndexes;
  public int meaning;
  public String value;



  public RoleJson(){

  }

  public RoleJson(String valueType, boolean excludeFromIndexes, int meaning,
                  String value){
   this.valueType = valueType;
   this.excludeFromIndexes  = excludeFromIndexes;
   this.meaning = meaning;
   this.value = value;


  }
 }


