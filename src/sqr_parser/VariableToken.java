package sqr_parser;

import java.io.*;

public class VariableToken extends BaseToken {
	public String variableType;

	public VariableToken(TokenType t, String typeOfVariable) {
		super(t);
		this.variableType = typeOfVariable;
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && i != 32 && !BaseToken.isEOL(i) && BaseToken.isAlphaNumeric(i)) {
			output.append((char) i);			
		}
		
		if(BaseToken.isEOL(i))
			this.EOLReached = true;
			
		this.contentsLength = output.toString().length() - 1;
		
		this.contents = output.toString();
		return inputChar;
	}
}
