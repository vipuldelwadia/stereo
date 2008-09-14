package util.queryparser;

import java.util.Scanner;

import daap.DAAPConstants;

import interfaces.Track;

public class Token implements Filter {
	
	public final boolean falseify;
	public final String property;
	public final String value;
	
	public Token(String t) {
	
		Scanner s = new Scanner(t);
		s.useDelimiter("[:]");
		
		String p = s.next();
		if (s.hasNext()) {
			value = s.next();
		}
		else {
			value = "";
		}
		
		if (p.charAt(p.length()-1) == '!') {
			falseify = true;
			property = p.substring(0, p.length()-1);
		}
		else {
			falseify = false;
			property = p;
		}
	}
	
	public boolean check(Track t) {
		
		Integer code = DAAPConstants.shortCodes.get(property);
		
		if (code != null) {
			
			Object tval = null;
			Object pval = t.getTag(code);
			
			int ae00 = 0x61650000;
			if ((0xFFFF0000 & code) == ae00) {
				//TODO we ignore apple codes because mt-daapd doesn't implement them
				//this should change?
				return true;
			}
			switch (DAAPConstants.types.get(code)) {
			case 1: tval = Byte.parseByte(value); break;
			case 5: tval = Integer.parseInt(value); break;
			case 9: tval = value; break; //string
			default:
				throw new IllegalArgumentException("unknown or unimplemented type: "
						+ DAAPConstants.types.get(code)	+ " for " + property);
			}
			
			return (pval != null && tval.equals(pval)) ^ falseify;
		}
		else {
			throw new IllegalArgumentException("unknown property: " + property);
		}
	}
	
	public String toString() {
		return "'"+property+(falseify?"!":"")+":"+value+"'";
	}
}