package sqr_parser;

import java.io.*;

public class ConstLiteralToken extends BaseToken {

	public ConstLiteralToken(TokenType t) {
		super(t);
	}
	
	@Override
	public int parseToken(BufferedInputStream b, int inputChar) {
		return 7;
	}
}
