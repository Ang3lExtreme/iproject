package pt.unl.fct.di.apdc.individualproject.util;




public class UsersJson {

 public String user_name;
 public String user_password;
 public String user_email;
 public String user_phone;
 public String user_role;
 public String user_state;
 public String user_profile;
 public String time_stamp;


 public UsersJson(){

 }

 public UsersJson(String user_name,String user_password, String user_email,
                  String user_phone, String user_role, String user_state, String user_profile,
                  String time_stamp){
  this.user_name = user_name;
  this.user_password  = user_password;
  this.user_email = user_email;
  this.user_phone = user_phone;
  this.user_role = user_role;
  this.user_state = user_state;
  this.user_profile = user_profile;
  this.time_stamp = time_stamp;

 }
}
