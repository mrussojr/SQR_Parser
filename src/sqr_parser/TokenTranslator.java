package sqr_parser;

import java.io.*;
import java.util.*;

public class TokenTranslator {
	private final static String PROG_NAME_SEARCH_STRING = "<<change_name>>";
	
	//token lists to hold different groups of statements
	private TokenList parsedTokens = null;
	private TokenList mainFunction = new TokenList();
	private TokenList currentFunction = null;
	private TokenList openProcess = new TokenList();
	private TokenList methodCall = new TokenList();
	private TokenList sql = new TokenList();
	
	private LinkedList<TokenList> functions = new LinkedList<TokenList>();
	private LinkedList<String> inConditional = new LinkedList<String>();
	private LinkedList<String> finalOutput = new LinkedList<String>();
	
	//booleans to control program flow based on different situations
	private boolean inSql = false;
	private boolean pauseSql = false;
	private boolean inFunction = false;
	private boolean inMain = false;
	private boolean inIfPart = false;
	private boolean callingFunction = false;
	private boolean colonPresent = true;
	private boolean catchOpen = false;
	private boolean catchWrite = false;
	private boolean inTry = false;
	private boolean lookForNextSymbolToken = false;
	
	private BufferedWriter bw = null;
	private int tabStart = 1;
	private int curTab;
	private int varLineNo;
	private int markLocation = 0;
	private String programName;
	private String pauseReason;
	
	public TokenTranslator(TokenList tokenList, BufferedWriter bwrt, String progName){
		this.parsedTokens = tokenList;
		this.parsedTokens.allToLowercase();
		this.bw = bwrt;
		this.programName = progName;
	}
	
	public void startTranslation(){
		//printFileStart();
		printVariables();
		
		processFunctions();
		
		for(BaseToken b : parsedTokens){
			processTokens(b);
		}
		
		processMain();
		translateFunction();
		
		//bw.write("}");
		//bw.flush();
	}
	
	public void printFinalOuput() throws IOException{
		printFileStart();
		
		for(String s : finalOutput){
			bw.write(s);
		}
		
		bw.write("}");
		bw.flush();
	}
	
	private void processFunctions(){
		BaseToken b;
		TokenList tl;
		
		for(Iterator<BaseToken> it = parsedTokens.iterator(); it.hasNext(); ){
			b = it.next();
			
			if(b.tokenType == TokenType.SYMBOL && b.contents.equals("begin-procedure")){
				tl = new TokenList();
				boolean capturing = true;
				
				do {
					if(b.tokenType == TokenType.SYMBOL && b.contents.equals("end-procedure"))
						capturing = false;
					
					tl.add(b);
					
					if(it.hasNext()){
						it.remove();
						b = it.next();
					}
				} while(capturing && it.hasNext());

				functions.add(tl);
			}
			
			if(b.tokenType == TokenType.SYMBOL && b.contents.equals("begin-report")){
				boolean capturing = true;
				
				do {
					if(b.tokenType == TokenType.SYMBOL && b.contents.equals("end-report"))
						capturing = false;
					
					mainFunction.add(b);

					if(it.hasNext()){
						it.remove();
						b = it.next();
					}
				} while(capturing && it.hasNext());
			}
		}
	}
	
	private void processMain(){
		inMain = true;
		curTab++;
		
		for(BaseToken b : mainFunction){
			processTokens(b);
		}

		inMain = false;
		curTab--;
		
		if(inTry){
			inTry = false;
			curTab--;
			finalOutput.add("catch(Exception e){\nSystem.out.println(e);\n}\n");
			printTabChar(curTab);
			finalOutput.add("}\n");
		}
	}
	
	private void translateFunction(){
		inFunction = true;
		while(!functions.isEmpty()){
			currentFunction = functions.removeLast();
			
			for(BaseToken b : currentFunction){
				processTokens(b);
			}
		}
		inFunction = false;
	}
	
	private void processCommentToken(CommentToken cT){
		String javaCommentChar = "//";
		String javaEndCommentChar = "";
		String javaStartCommentChar = "";
		
		BaseToken nextToken = parsedTokens.nextToken(parsedTokens.nextToken(cT));
		BaseToken prevToken = parsedTokens.prevToken(parsedTokens.prevToken(cT));
		
		if(nextToken.tokenType == TokenType.COMMENT && (prevToken.tokenType != TokenType.COMMENT || prevToken == cT)){
			javaStartCommentChar = "/*";
			javaCommentChar = "";
		}
		
		if(prevToken.tokenType == TokenType.COMMENT && (nextToken.tokenType != TokenType.COMMENT || nextToken == cT)){
			javaEndCommentChar = "*/";
			javaCommentChar = "";
		}
		
		printTabChar(tabStart);
		finalOutput.add(javaStartCommentChar + javaCommentChar + cT.contents.substring(1) + javaEndCommentChar);
		
		return;
	}
	
	private void processEOLToken(EOLToken eT){
		TokenList toSearch = currentList();
		
		if(toSearch.prevToken(eT).tokenType == TokenType.EOL)
			colonPresent = false;
		
		if(inIfPart){
			finalOutput.add("){");
			inIfPart = false;
		}
		
		if(colonPresent)
			finalOutput.add(";");
		
		if(inSql && pauseSql && pauseReason == "do")
		{
			pauseReason = "";
			pauseSql = false;
		}
		
		if(inSql && pauseSql == false){
			sql.add(eT);
			return;
		}
		
		finalOutput.add("\n");
		colonPresent = true;
	}
	
	private TokenList currentList(){
		TokenList currentList = null;
		
		if(inMain)
			currentList = mainFunction;
		else if(inFunction)
			currentList = currentFunction;
		else
			currentList = parsedTokens;
		
		return currentList;
	}
	
	private void processIntegerToken(IntegerToken intT){		
		printTabChar(curTab + 1);
		finalOutput.add(intT.contents);
	}
	
	private void processInvalidToken(InvalidToken invT){
		
	}
	
	private void processLiteralToken(LiteralToken lT){
		finalOutput.add(lT.contents.replace('\'', '"'));
	}
	
	private void processOperatorToken(OperatorToken oT){
		if(!inIfPart)
			printTabChar(curTab+1);
		
		switch(oT.contents){
			case "||":				
				finalOutput.add(" + ");
				break;
			case ")||":
				finalOutput.add(") + ");
				break;
			default:
				finalOutput.add(oT.translationIfExists());
				break;
		}
	}
	
	private void processSymbolToken(SymbolToken sT){
		printTabChar(curTab);
		switch(sT.contents){
			case "if":
				if(inSql)
				{
					pauseSql = true;
					pauseReason = "if";
					
					if(markLocation == 0)
						markLocation = finalOutput.size();
				}
				
				colonPresent = false;
				inIfPart = true;
				inConditional.add("1");
				curTab = inConditional.size() + tabStart;
				finalOutput.add(sT.contents + "(");
				break;
			case "end-if":
				if(inSql){
					pauseSql = false;
					pauseReason = "";
				}
				
				colonPresent = false;
				finalOutput.add("}");
				inConditional.removeLast();
				curTab = inConditional.size() + tabStart;
				break;
			case "else":
				colonPresent = false;
				finalOutput.add("}" + sT.contents + "{");
			case "do":
				callingFunction = true;
				
				if(inSql){
					pauseSql = true;
					pauseReason = "do";
					
					if(markLocation == 0)
						markLocation = finalOutput.size();
				}
					
				break;
			case "getenv":
				finalOutput.add("System." + sT.contents);
				break;
			case "begin-select":
			case "begin-sql":
				inSql = true;
				sql.add(sT);
				break;
			case "end-select":
			case "end-sql":
				inSql = false;
				pauseSql = false;
				SQL_Parser s = new SQL_Parser(sql);
				finalOutput.add(markLocation, s.toString());
				printTabChar(curTab);
				finalOutput.add("\t}\n}catch(Exception e){\nSystem.out.println(e);}\n");
				markLocation = 0;
				break;
			case "begin-report":
				finalOutput.add("public static void main(String[] args){");
				break;
			case "begin-procedure":
				finalOutput.add("public static void ");
				lookForNextSymbolToken = true;
				break;
			case "open":
				catchOpen = true;
				processOpen(sT);
				break;
			case "write":
				catchWrite = true;
				processWrite(sT);
				break;
			case "end-report":
			case "end-procedure":
				finalOutput.add("}\n\n");
				inSql = false;
				pauseSql = false;
				break;
			//handle all key words that don't need to be printed here
			case "let":
			case "begin-heading":
			case "begin-setup":
			case "end-heading":
			case "end-setup":
				break;
			default:				
				if(callingFunction){
					finalOutput.add(sT.contents + "();");
					callingFunction = false;
				}else if(lookForNextSymbolToken){
					finalOutput.add(sT.contents + "(){\n");
					lookForNextSymbolToken = false;
				}else if(inSql && pauseSql == false){
					sql.add(sT);
				}else{
					finalOutput.add(sT.contents);
				}
				
				break;
		}
	}
	
	private void processVariableToken(VariableToken vT){		
		printTabChar(curTab + 1);
		finalOutput.add(vT.contents.substring(1));
	}
	
	private void processWhiteSpaceToken(WhiteSpaceToken wT){
		finalOutput.add(wT.contents);
	}
	
	private void printTabChar(int x){
		for(int y = 0; y < x; y++)
			finalOutput.add("\t"); //bw.write("\t");
	}
	
	private void printFileStart() throws IOException{
		BufferedInputStream bs = new BufferedInputStream(new FileInputStream("U:\\SQR_Working\\begin.txt"));
		int i;
		StringBuilder s = new StringBuilder();
		String output;
		
		while((i = bs.read()) != -1){
			//bw.write((char) i);
			s.append((char) i);
		}
		
		bs.close();
		
		output = s.toString();
		
		output = output.replaceFirst(PROG_NAME_SEARCH_STRING, programName);
		
		bw.write(output);
		
		bw.newLine();
	}
	
	private void printVariables(){
		VariableToken vt = null;
		TokenList tl = this.parsedTokens.getDistinctFilteredTokens(TokenType.VARIABLE);
		
		printTabChar(tabStart);
		finalOutput.add("//declaring variables\n"); //bw.write("//declaring variables");
		//bw.newLine();
		
		for(BaseToken b : tl){
			vt = (VariableToken) b;
			if(vt.variableType.equals("String") || vt.variableType.equals("int")){
				printTabChar(tabStart);
				finalOutput.add("private " + vt.variableType + " " + vt.contents.substring(1) + ";\n");
			}
		}
		
		varLineNo = finalOutput.size(); 
		
		finalOutput.add("\n"); //bw.newLine();
	}
	
	private void processTokens(BaseToken b){
		if(catchOpen){
			processOpen(b);
			return;
		}else if(catchWrite){
			processWrite(b);
			return;
		}else if(inSql && pauseSql == false){			
			if(b.tokenType == TokenType.SYMBOL)
				processSymbolToken((SymbolToken) b);
			else if(b.tokenType == TokenType.EOL)
				processEOLToken((EOLToken) b);
			else
				sql.add(b);
			
			return;
		}
		
		switch(b.tokenType){
			case COMMENT:
				processCommentToken((CommentToken) b);
				break;
			case CONST_LITERAL:
				break;
			case EOF:
				break;
			case EOL:
				processEOLToken((EOLToken) b);
				break;
			case INTEGER:
				processIntegerToken((IntegerToken) b);
				break;
			case INVALID:
				processInvalidToken((InvalidToken) b);
				break;
			case LITERAL:
				processLiteralToken((LiteralToken) b);
				break;
			case OPERATOR:
				processOperatorToken((OperatorToken) b);
				break;
			case PUNCTUATION:
				break;
			case SYMBOL:
				processSymbolToken((SymbolToken) b);
				break;
			case VARIABLE:
				processVariableToken((VariableToken) b);
				break;
			case WHITE_SPACE:
				processWhiteSpaceToken((WhiteSpaceToken) b);
				break;
			default:
				break;
		}
	}
	
	private void processOpen(BaseToken b){
		TokenList toSearch = currentList();
		
		openProcess.add(b);
		
		if(toSearch.nextToken(b).tokenType == TokenType.EOL){
			catchOpen = false;
			
			openProcess.removeTokensOfType(TokenType.WHITE_SPACE);
			
			String funcOutput = "";
			String varOutput = "";
			String fileName = openProcess.get(1).contents.substring(1);
			String variableName = "";
			
			if(openProcess.get(4).contents.equals("for-writing")){
				variableName = "writeFile" + openProcess.get(3).contents;
				varOutput = "private BufferedWriter " + variableName + " = null;\n";
				funcOutput = variableName + " = new BufferedWriter(new FileWriter(" + fileName + "));\n";
			}else{
				variableName = "readFile" + openProcess.get(3).contents;
				varOutput = "private BufferedInputStream " + variableName + " = null;\n";
				funcOutput = variableName + " = new BufferedInputStream(new FileInputStream(" + fileName + "));\n";
			}
			
			printTabChar(curTab);
			finalOutput.add(varLineNo, varOutput);
			varLineNo++;
			
			printTabChar(curTab);
			finalOutput.add("try{\n");
			curTab++;
			inTry = true;
			printTabChar(curTab);
			finalOutput.add(funcOutput);
		}
	}
	
	private void processWrite(BaseToken b){
		TokenList toSearch = currentList();
		
		methodCall.add(b);
		
		if(toSearch.nextToken(b).tokenType == TokenType.EOL){
			catchWrite = false;
			
			methodCall.removeTokensOfType(TokenType.WHITE_SPACE);
			
			String variableName = "writeFile" + methodCall.get(1).contents;
			String fileTextVar = methodCall.get(3).contents.substring(1);
			
			printTabChar(curTab);
			finalOutput.add(variableName + ".write(" + fileTextVar + ");");
		}
	}
}
