package txtclass;

import java.util.Vector;

/**
 * ���ķִ���
 */
public class ChineseSpliter {
	/**
	 * �Ը������ı��������ķִ�
	 * 
	 * @param text
	 *            �������ı�
	 * @param splitToken
	 *            ���ڷָ�ı��,��"|"
	 * @return �ִ���ϵ��ı�
	 */
	public static String split(String text, String splitToken) {
		String result = "";
		// MMAnalyzer analyzer = new MMAnalyzer();
		// try
		// {
		// // result = analyzer.segment(text, splitToken);
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		// �ִ�
		int maxlen = 11;

		
		Vector<String> dictionary = new Vector<String>();
		dictionary = ReadFileToVector.ReadFile("dic.txt");// ���ļ��м��شʵ䣬����ŵ�vector������
		
		int i = 0, j = 0;
		for (i = 0; i < text.length();) {
			if (text.length() - i == 1) // ���ʣһ������
			{
				result = result+text.substring(i);
				break;
			} 
			else if (text.length() - i < maxlen)
				maxlen = text.length() - i;
			j = i + maxlen - 1;
			String key = text.substring(i, j);
			if (dictionary.contains(key))// ��󳤶�ʱ���ڴʵ��д���,����ͣ�ôʵ��д���
			{
				result = result + key + splitToken;
				i = i + key.length();
				continue;
			}
			while (key.length() > 1) {
				j--;
				key = text.substring(i, j);
				if (dictionary.contains(key)) {
					result = result + key + splitToken;
					i = i + key.length();
					break;
				} else if (key.length() == 1) {
					result = result + key + splitToken;
					i = i + key.length();
					break;
				}
			}
		}
		return result;
	}
}
