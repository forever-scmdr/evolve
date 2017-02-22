package ecommander.model;

import org.apache.commons.lang3.StringUtils;

public enum LOGICAL_SIGN {
	AND(" AND "), OR(" OR ");
	
	private final String text;
	
	private LOGICAL_SIGN(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
	
	public static LOGICAL_SIGN getSign(String signStr) {
		if (!StringUtils.isBlank(signStr) && signStr.trim().equalsIgnoreCase("OR"))
			return OR;
		return AND;
	}
}
