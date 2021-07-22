package ecommander.model.datatypes;

import org.apache.commons.lang3.StringUtils;

public class CharDataType extends IntegerDataType{
	public CharDataType(Type type){
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		if(StringUtils.isBlank(stringValue)){return null;}
		try{
			if(stringValue.length() == 1){
				return (int)stringValue.charAt(0);
			}else if(StringUtils.startsWith(stringValue, "\\")){
				return Integer.parseInt(stringValue.substring(1),16);
			}else if(StringUtils.startsWith(stringValue, "&#")){
				String s = stringValue.substring(2,stringValue.length() - 1);
				return Integer.parseInt(s);
			}
			return null;
		}catch (Exception e){
			return null;
		}
	}
}
