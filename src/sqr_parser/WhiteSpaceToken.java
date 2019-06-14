package sqr_parser;

import java.io.*;

public class WhiteSpaceToken extends BaseToken {

	public WhiteSpaceToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && !BaseToken.isEOL(i) && (i == 32 || i == 9)) {
			output.append((char) i);			
		}
		
		if(BaseToken.isEOL(i))
			this.EOLReached = true;
		
		this.contentsLength = output.toString().length()-1;
		
		this.contents = output.toString();
		return inputChar;
	}
}
