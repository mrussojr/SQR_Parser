package sqr_parser;

import java.io.*;

public class IntegerToken extends BaseToken {

	public IntegerToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && i != 32 && !BaseToken.isEOL(i) && BaseToken.isInteger(i)) {
			output.append((char) i);			
		}
		
		if(BaseToken.isEOL(i)){
			this.EOLReached = true;
			this.contentsLength = output.toString().length() - 1;
		}else{
			this.contentsLength = output.toString().length();
		}
		
		this.contents = output.toString();
		return inputChar;
	}
}
