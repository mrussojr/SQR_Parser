package sqr_parser;

import java.io.*;

public class Sqr_Parser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileLocation = "U:\\SQR_Working\\sp3003r.sqr";
		String fileOutLocation = fileLocation.replaceAll("sqr", "java");
		BufferedInputStream bs = null;
		BufferedWriter bw = null;
		TokenParser t = null;
		
		try{
			bs = new BufferedInputStream(new FileInputStream(fileLocation));
			t = new TokenParser(bs);
			
			t.parse_tokens();
			
			try{
				bw = new BufferedWriter(new FileWriter(fileOutLocation));
	
				String programName = fileLocation.substring(fileLocation.lastIndexOf("\\")+1);
				programName = programName.substring(0, programName.lastIndexOf(".")).toUpperCase();
				
				TokenTranslator tt = new TokenTranslator(t.getTokenList(), bw, programName);
				tt.startTranslation();
				tt.printFinalOuput();
			}catch(Exception e){
				System.out.println(e);
			}
			finally{
				if (bw != null){
					try{
						bw.close();
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		finally{
			if (bs != null){
				try{
					bs.close();
				}catch(Exception e){
					System.out.println(e);
				}
			}
		}
		
		System.out.println("just so i know it's done");
	}
}
