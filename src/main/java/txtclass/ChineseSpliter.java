package txtclass;

import java.util.Vector;

/**
 * 中文分词器
 */
public class ChineseSpliter {
	/**
	 * 对给定的文本进行中文分词
	 * 
	 * @param text
	 *            给定的文本
	 * @param splitToken
	 *            用于分割的标记,如"|"
	 * @return 分词完毕的文本
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
		// 分词
		int maxlen = 11;

		
		Vector<String> dictionary = new Vector<String>();
		dictionary = ReadFileToVector.ReadFile("dic.txt");// 从文件中加载词典，结果放到vector向量中
		
		int i = 0, j = 0;
		for (i = 0; i < text.length();) {
			if (text.length() - i == 1) // 最后剩一个单词
			{
				result = result+text.substring(i);
				break;
			} 
			else if (text.length() - i < maxlen)
				maxlen = text.length() - i;
			j = i + maxlen - 1;
			String key = text.substring(i, j);
			if (dictionary.contains(key))// 最大长度时，在词典中存在,不在停用词典中存在
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
