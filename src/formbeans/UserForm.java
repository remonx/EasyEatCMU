package formbeans;

import java.util.ArrayList;
import java.util.List;

import org.mybeans.form.FormBean;
/**
 * Course: 15-637
 * Assignment: HW4
 * @author Yin Xu
 * Last modified: 03/01/2013
 */
public class UserForm extends FormBean {
	private String userName = "";
	
	public String getUserName()  { return userName; }
	
	public void setUserName(String s)  { userName = trimAndConvert(s,"<>>\"]"); }

	public List<String> getValidationErrors() {
		List<String> errors = new ArrayList<String>();

		if (userName == null || userName.length() == 0) {
			errors.add("User Name is required");
		}
		
		return errors;
	}
}
