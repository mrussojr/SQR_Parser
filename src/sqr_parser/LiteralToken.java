package sqr_parser;

import java.io.*;

public class LiteralToken extends BaseToken {

	public LiteralToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && !BaseToken.isEOL(i) && i != 39) {
			output.append((char) i);			
		}
		
		if(BaseToken.isEOL(i)){
			this.EOLReached = true;
			this.contentsLength = output.toString().length() - 1;
		}else{
			this.contentsLength = output.toString().length();
		}
		
		output.append((char) i);
		this.contents = output.toString();
		return inputChar;
	}
}
