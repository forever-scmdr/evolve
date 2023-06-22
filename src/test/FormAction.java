package test;

import java.util.HashMap;
import java.util.Map;

public class FormAction {

	private String name;
	private String password;
	private HashMap<String, String> value = new HashMap<String, String>();
	
	public String execute() {
//		String common = "";
//		common += name + "  ";
//		common += password;
		//try {
			String str = value.get("$mega@mega");
			System.out.println(str);
			System.out.println(name);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
			//ServletActionContext.getResponse().setCharacterEncoding("UTF-8");
/*		try {
			OutputStream ostream = ServletActionContext.getResponse().getOutputStream();
			String result = "<html><body><h1>MEGA РЕЗАЛТ</h1></body></html>";
			ostream.write(result.getBytes());
			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
*/
		return "test";
	}

	public String getNameEeee() {
		return name;
	}

	public void setNameEeee(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setValue(String s1, String s2) {
		value.put(s1, s2);
	}
	
	public Map<String, String> getValue() {
		return value;
	}
}
