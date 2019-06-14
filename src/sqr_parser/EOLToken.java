package sqr_parser;

import java.io.*;

public class EOLToken extends BaseToken {

	public EOLToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) {
		return inputChar;
	}
}
