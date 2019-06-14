package sqr_parser;

import java.util.*;

public class SQL_Parser {
	private TokenList tl;
	private TokenList keywords = new TokenList();
	private LinkedList<SQL_Tab> tables = new LinkedList<SQL_Tab>();
	private LinkedList<SQL_Col> columns = new LinkedList<SQL_Col>();
	private String actualSQL = "";
	private String statementType = "";
	
	
	public SQL_Parser(TokenList tl)
	{
		this.tl = tl;
		tl.removeTokensOfType(TokenType.WHITE_SPACE);
		
		statementType = determineType();
		
		switch(statementType){
			case "select":
				parseSelect();
				break;
			case "delete":
				break;
			case "drop":
				break;
			case "create":
				break;
			case "update":
				break;
			case "insert":
				break;
			default:
				break;
		}
	}

	@Override
	public String toString(){
		String finalOutputString = "try{\n"
				+ "OracleDriver.isInServer();\n\n"
				+ "Connection con = DriverManager.getConnection(\"jdbc:oracle:thin:@orac-db-scan:1521/testdb\",\"russom\",\"Fender110684!\");\n\n"
				+ "Statement stmt = con.createStatement();\n\n";
//				+ "ResultSet rs = stmt.executeQuery(\"" + actualSQL + ");\n\n"
//				+ "while(rs.next()){\n"
//				+ "\t//assign columns to variables";
		
//		for(SQL_Col c : columns){
//			finalOutputString += "\n\t" + c.colVar + " = rs.getString(\"" + c.colName + "\"));";
//		}
		
		return finalOutputString;
	}
	
	private String determineType(){
		String checker = tl.getFirst().contents;
		String type = "error";
		BaseToken first;
		
		if(checker.equals("begin-select")){
			type = "select";
			tl.removeFirst();
		}else{
			tl.removeFirst();
			
			first = tl.getFirst();
			
			while(first.tokenType != TokenType.SYMBOL){
				tl.removeFirst();
				first = tl.getFirst();
			}

			type = first.contents;
		}
		
		return type;
	}
	
	private void parseSelect(){
		BaseToken nextToken;
		BaseToken b;
		boolean inFrom = false;
		boolean inWhere = false;
		boolean inOrder = false;
		boolean inFunc = false;
		
		for(Iterator<BaseToken> it = tl.iterator(); it.hasNext();){
			b = it.next();
			nextToken = tl.nextToken(b);
			
			if(b.tokenType == TokenType.SYMBOL){
				if(nextToken.tokenType == TokenType.SYMBOL){
					if(b.contents.equals("distinct"))
						keywords.add(b);
					
					if(b.contents.equals("from"))
						inFrom = true;
					
					if(b.contents.equals("where")){
						inFrom = false;
						inWhere = true;
					}
					
					if(b.contents.equals("order")){
						inWhere = false;
						inOrder = true;
					}	
				}
				
				if(nextToken.tokenType == TokenType.OPERATOR){
					if(nextToken.contents.equals("("))
						inFunc = true;
					
				}
			}
		}
	}
	
	private class SQL_Col{
		private String colName;
		private String colVar;
		private boolean isFunc;
		private TokenList params;
		
		private SQL_Col(String cn, String cv, boolean isF){
			this.colName = cn;
			this.colVar = cv;
			this.isFunc = isF;
		}
		
		private SQL_Col(String cn, String cv){
			this.colName = cn;
			this.colVar = cv;
		}
		
		private SQL_Col(String cn){
			this.colName = cn;
		}
	}
	
	private class SQL_Tab{
		private String tabName;
		private String tabAlias;
		
		private SQL_Tab(String tn, String ta){
			this.tabName = tn;
			this.tabAlias = ta;
		}
		
		private SQL_Tab(String tn){
			this.tabName = tn;
		}
		
		
	}
}

