package txtclass;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
 class ReadFileToVector {
	 /**
     * @param str
     * @author:
     * <p>Description�����ļ��ж�ȡһ�����ݣ��Ѷ�ȡ�����ݷ���vector�з���
     */
    public static Vector<String> ReadFile(String str)
    {   
        String word=null;
        Vector<String> vectors = new Vector<String>();
//        int i=0;
        try {
            FileInputStream fi=new FileInputStream(str);
            BufferedReader  input=new BufferedReader (new InputStreamReader(fi));
            while((word=input.readLine())!=null){
               vectors.add(word);
            }
    System.out.println("�����ֵ䵥��������:"+vectors.size());
        } catch (Exception e) {
            e.printStackTrace();
        }    
        return vectors;
        
    }
   /* public static void main(String[] args) {
        ReadFileToVector.ReadFile("D:\\dic.txt");

    }*/

}



