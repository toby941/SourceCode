package txtclass;

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChineseTextGet {
	String wenjianlujin="";
	ChineseTextGet(String filepath){
		wenjianlujin=filepath;
	}
	
	public String gettext()
	{
		String strtext="";
		FileInputStream fi;
        try {
			fi=new FileInputStream(wenjianlujin);
	        BufferedReader  input=new BufferedReader (new InputStreamReader(fi));
	        String r=input.readLine();
	        while(r!=null)
		        {
		        	strtext+=r;
		        	r=input.readLine();
		        }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return strtext;
	}
	

}
