package sqr_parser;

import java.io.*;
import java.util.regex.*;

public class TokenParser {
	private TokenList tokenList = new TokenList();
	private BufferedInputStream bStream;
	
	public TokenParser(BufferedInputStream b){
		this.bStream = b;
	}
	
	public boolean parse_tokens() throws IOException{
		BaseToken token = null;
		StringBuilder sb = null;
		Pattern p = null;
		Matcher m = null;
		int i;
		int peekChar;
		
		while ((i=bStream.read()) != -1){
		    //space
		    if(i == 32 || i == 9){
		    	token = new WhiteSpaceToken(TokenType.WHITE_SPACE);
		    }
		    
			if(i == '!'){
				bStream.mark(1);
				peekChar = bStream.read();
				bStream.reset();
					
				if(peekChar != '='){
					token = new CommentToken(TokenType.COMMENT);
				}else{
					token = new OperatorToken(TokenType.OPERATOR);
				}
			}
					
		    if(i == '#'){
				bStream.mark(3);
				sb = new StringBuilder();
				sb.append((char) bStream.read());
				sb.append((char) bStream.read());
				sb.append((char) bStream.read());
				bStream.reset();
				
				p = Pattern.compile("(if |def|end|els|inc)", Pattern.CASE_INSENSITIVE);
				m = p.matcher(sb.toString());
				
				if(m.matches()){
					token = new InvalidToken(TokenType.INVALID);
				}else{
					token = new VariableToken(TokenType.VARIABLE, "int");
				}
			}
		    
		    if(i == '-'){
		    	bStream.mark(1);
		    	peekChar = bStream.read();
		    	bStream.reset();
		    	
		    	if(BaseToken.isInteger(peekChar)){
		    		token = new IntegerToken(TokenType.INTEGER);
		    	}else{
		    		token = new OperatorToken(TokenType.OPERATOR);
		    	}		    	
		    }
		    
		    if(BaseToken.isOperator(i)){
		    	token = new OperatorToken(TokenType.OPERATOR);
		    }
		    
		    if(i == '$'){
		    	token = new VariableToken(TokenType.VARIABLE, "String");
		    }
		    
		    if(i == '&'){
		    	token = new VariableToken(TokenType.VARIABLE, "Column");
		    }
		    
		    //A-Z or a-z
		    if(BaseToken.isAlpha(i)){
		    	bStream.mark(4);
		    	sb = new StringBuilder();
		    	sb.append(bStream.read());
		    	sb.append(bStream.read());
		    	sb.append(bStream.read());
		    	sb.append(bStream.read());
		    	bStream.reset();
		    	
				p = Pattern.compile("^(and |or .)", Pattern.CASE_INSENSITIVE);
				m = p.matcher(sb);
		    	
				if(m.matches()){
					String translation = "";
							
					if(m.group().equals("and ")){
						translation = "&&";
					}else{
						translation = "||";
					}
					
					token = new OperatorToken(TokenType.OPERATOR, translation);
				}else{
					token = new SymbolToken(TokenType.SYMBOL);
				}
		    }
		    
		    //0-9 or .
		    if(BaseToken.isInteger(i)){
		    	token = new IntegerToken(TokenType.INTEGER);
		    }
		    
		    //single quote
		    if(i == 39){
		    	token = new LiteralToken(TokenType.LITERAL);
		    }
		    
		    //line feed or carriage return
		    if(BaseToken.isEOL(i)){
		    	token = new EOLToken(TokenType.EOL);
		    }
		    
		    if(token == null){
		    	return false;
		    }
			
		    bStream.mark(1000);
		    token.parseToken(bStream, i);
			tokenList.addLast(token);
			//token.PrintToken();
			bStream.reset();
			
			for(int x = 0; x < token.contentsLength; x++){
				bStream.read();
			}
		}
		
		return true;
	}
	
	public TokenList getTokenList(){
		return this.tokenList;
	}
}
