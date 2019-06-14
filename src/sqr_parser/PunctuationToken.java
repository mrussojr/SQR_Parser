package sqr_parser;

import java.io.*;

public class PunctuationToken extends BaseToken {

	public PunctuationToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) {
		return 4;
	}
}
