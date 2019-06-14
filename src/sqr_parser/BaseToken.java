package sqr_parser;

import java.io.*;

public class BaseToken {
	public TokenType tokenType;
	protected String contents;
	protected boolean EOLReached;
	public int contentsLength = 0;
	
	public BaseToken(TokenType t){
		this.tokenType = t;
	}
	
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException{
		return 0;
	}
	
	public void printToken(){
		if(this.contents != null && this.tokenType != TokenType.WHITE_SPACE)
			System.out.println(this.tokenType + "  \t" + this.contents);
	}
	
	public String returnContents(){
		return this.contents;
	}
	
	public boolean compareContents(BaseToken b){
		return this.contents.equals(b.contents);
	}
	
	public static boolean isInteger(int inputChar){
		if((inputChar >= 48 && inputChar <= 57) || inputChar == 46){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEOL(int inputChar){
		if(inputChar == 10 || inputChar == 13){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isEOF(int inputChar){
		if(inputChar == -1){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isOperator(int inputChar){			
		switch(inputChar)
		{
			case '*':
			case '+':
			case '%':
			case '/':
			case '=':
			case '<':
			case '>':
			case '|':
			case ',':
			case '(':
			case ')':
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isAlpha(int inputChar) {
		if((inputChar >= 65 && inputChar <= 90) || (inputChar >= 97 && inputChar <= 122))
		{
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isAlphaNumeric(int inputChar) {
		if(isAlpha(inputChar) || isInteger(inputChar) || inputChar == '_' || inputChar == '-' || inputChar == ','){
			return true;
		}else{
			return false;
		}
	}
}
