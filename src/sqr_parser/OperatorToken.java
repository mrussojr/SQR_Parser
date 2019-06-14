package sqr_parser;

import java.io.*;

public class OperatorToken extends BaseToken {
	private String translation;
	
	public OperatorToken(TokenType t) {
		super(t);
	}
	
	public OperatorToken(TokenType t, String translated) {
		super(t);
		this.translation = translated;
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && i != 32 && !BaseToken.isEOL(i) && BaseToken.isOperator(i)) {
			output.append((char) i);			
		}
		
		if(BaseToken.isEOL(i))
			this.EOLReached = true;
		
		this.contentsLength = output.toString().length() - 1;

		this.contents = output.toString();
		return inputChar;
	}
	
	public String translationIfExists() {
		if(translation == null)
			return this.contents;
		else
			return this.translation;
	}
}
