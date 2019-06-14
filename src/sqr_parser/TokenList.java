package sqr_parser;

import java.util.Iterator;
import java.util.LinkedList;

public class TokenList extends LinkedList<BaseToken>{
	private static final long serialVersionUID = -3759862685880498096L;
	
	public TokenList(){
		
	}
	
	public void printTokens(){
		for(BaseToken b : this){
			b.printToken();
		}
	}
	
	public int tokenTypeCount(TokenType t){
		int count = 0;
		
		for(BaseToken b : this){
			if(b.tokenType == t)
				count++;
		}
		
		return count;
	}
	
	public void removeNullTokens(){
		for(BaseToken b : this){
			if(b == null)
				this.remove(b);
		}
	}
	
	public void removeTokensOfType(TokenType t){
		BaseToken b = null;
		
		for(Iterator<BaseToken> it = this.iterator(); it.hasNext();){
			b = it.next();
			
			if(b.tokenType == t)
				it.remove();
		}
	}
	
	public TokenList getFilteredTokens(TokenType t){
		TokenList list = new TokenList();
		
		for(BaseToken b : this){
			if(b.tokenType == t)
				list.add(b);
		}
		
		return list;
	}
	
	public TokenList getDistinctFilteredTokens(TokenType t){
		TokenList list = new TokenList();
		boolean add;
		
		for(BaseToken b : this){
			if(b.tokenType == t){
				if(list.isEmpty())
					list.add(b);
				else{
					add = false;
					
					for(BaseToken bb : list)
					{
						add = bb.compareContents(b);
						if(add)
							break;
					}
					
					if(!add)
						list.add(b);
				}
			}	
		}
		
		return list;
	}
	
	public void printListBreakdown(){
		for(TokenType tk : TokenType.values())
		{
			System.out.println("Number of " + tk + ": " + this.tokenTypeCount(tk));
		}
	}
	
	public BaseToken prevToken(BaseToken b){
		int tokenIndex = this.indexOf(b);
		int prevTokenIndex = 0;
		
		if(tokenIndex > 0){
			prevTokenIndex = tokenIndex - 1;
			return this.get(prevTokenIndex);
		}else{
			return b;
		}
	}
	
	public BaseToken nextToken(BaseToken b){
		int tokenIndex = this.indexOf(b);
		int nextTokenIndex = 0;
		
		if(tokenIndex < this.size() - 2){
			nextTokenIndex = tokenIndex + 1;
			return this.get(nextTokenIndex);
		}else{
			return b;
		}
	}
	
	public void allToLowercase(){
		for(BaseToken b : this){
			if(b.contents != null && b.tokenType != TokenType.LITERAL)
				b.contents = b.contents.toLowerCase();
		}
	}
}
