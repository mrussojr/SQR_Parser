package sqr_parser;

import java.io.*;

public class CommentToken extends BaseToken {

	public CommentToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) throws IOException {
		int i;
		StringBuilder output = new StringBuilder();
		
		output.append((char) inputChar);
		
		while((i = b.read()) != -1 && !BaseToken.isEOL(i)) {
			output.append((char) i);			
		}
		
		this.EOLReached = true;
		this.contentsLength = output.toString().length() - 1;
		
		this.contents = output.toString();
		return inputChar;
	}
}
