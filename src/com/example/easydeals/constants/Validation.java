package com.example.easydeals.constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


import com.example.easydeals.pojo.User;

public class Validation {
	Validation validateData;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public String validate(User user){
		String flag ="GOOD";
		boolean regExFName = findMatch(user.getfName(), user.getlName());
		boolean emailValidation = validateEmail(user.geteMail());
		if(user.getfName().trim().equals("") || user.getlName().trim().equals("")|| user.geteMail().trim().equals("")
				|| user.getDob().trim().equals("") ||  user.getPwd().trim().equals("")){
			flag = "EMPTY FIELDS";
		} else if (regExFName){
			flag = "INVALID CHARACTER";
		} else if(!emailValidation){
			flag = "INVALID EMAIL";
		} else if(user.getGender() == null){
			flag = "GENDER";
		} 
		return flag;
	}
	
	//To validate the card details
	public String validate(String cardType, String cardNum){
		String flag = "GOOD";
		if(cardType.equalsIgnoreCase("Credit")){
			if(cardNum.trim().equals("")){
				flag = "EMPTY FIELDS";
			} else if (cardNum.length() != 16){
				flag = "INVALID CARD";
			}
		} else if (cardType.equalsIgnoreCase("Costco")){
			if(cardNum.trim().equals("")){
				flag = "EMPTY FIELDS";
			} else if(cardNum.length() != 12){
				flag = "INVALID CARD";
			}
		}
		return flag;
	}
	
	// To check whether the first and last name includes any special characters
		private boolean findMatch(String fName, String lName) {
			boolean match = false;

			Pattern p = Pattern.compile("^[a-zA-Z0-9 ]+$");
			// Find instance of pattern matches
			Matcher t1 = p.matcher(fName);
			Matcher t2 = p.matcher(lName);

			if (t1.find() && t2.find()) {
				match = false;
			} else {
				match = true;
			}
			return match;
		}
		
		//To check whether the email pattern is correct
		private boolean validateEmail(String email){
			System.out.println("The email inside validate email method is ===> " + email);
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			Matcher matcher = pattern.matcher(email);
			boolean ePattern = matcher.matches();
			/*String domain = email.substring(email.indexOf("@"), email.indexOf("."));
			System.out.println("The domain is ===> " + domain);
			String userValue = email.substring(0,email.indexOf("@"));
			System.out.println("The user value is ===> " + userValue);*/
			
			validateData = new Validation();
			if(ePattern){
				InternetAddress internetAddress;
				try {
					internetAddress = new InternetAddress(email);
					internetAddress.validate();
					System.out.println("validating email address ---------------------> ");
					ePattern = true;
				} catch (AddressException e) {
					ePattern = false;
					System.out.println("The email is not valid");
					e.printStackTrace();
				}
				
			}
			
			return ePattern;
			
		}
}
