package sqr_parser;

public enum TokenType {
	INVALID, SYMBOL, INTEGER,
	LITERAL, CONST_LITERAL,
	PUNCTUATION, WHITE_SPACE,
	EOL, EOF, COMMENT,
	VARIABLE, OPERATOR
}
