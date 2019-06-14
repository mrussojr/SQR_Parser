package sqr_parser;

import java.io.*;

public class SymbolToken extends BaseToken {

	public SymbolToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && i != 32 && !BaseToken.isEOL(i) && !BaseToken.isOperator(i)) {
			output.append((char) i);			
		}
		
		if(BaseToken.isEOL(i))
			this.EOLReached = true;
			
		this.contentsLength = output.toString().length() - 1;
		
		this.contents = output.toString();
		return inputChar;
	}
}
