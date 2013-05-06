package txtclass;

import java.util.Vector;

import txtclass.ChineseSpliter;

public class text {

	/**
	 * @param args
	 */
	
	/**
	* 去掉停用词
	* @param text 给定的文本
	* @return 去停用词后结果
	*/
	public static String[] DropStopWords(String[] oldWords)
	{
		Vector<String> stopdictionary = new Vector<String>();
		stopdictionary = ReadFileToVector.ReadFile("stoplis.txt");// 从文件中加载词典，结果放到vector向量中
		
		Vector<String> v1 = new Vector<String>();
		for(int i=0;i<oldWords.length;++i)
		{
			if(!stopdictionary.contains(oldWords[i])) //不是停用词
			{
				v1.add(oldWords[i]);
			}
		}
		String[] newWords = new String[v1.size()];
		v1.toArray(newWords);
		return newWords;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String text = "本报讯 (记者 王京) 联想THINKPAD近期几乎全系列笔记本电脑降价促销，最高降幅达到800美元，降幅达到42%。这是记者昨天从联想美国官方网站发现的。联想相关人士表示，这是为纪念新联想成立1周年而在美国市场推出的促销，产品包括THINKPAD T、X以及Z系列笔记本。促销不是打价格战，THINK品牌走高端商务路线方向不会改变。";
		String[] terms = null;
		terms= ChineseSpliter.split(text, " ").split(" ");//中文分词处理(分词后结果可能还包含有停用词）
		terms = DropStopWords(terms);//去掉停用词，以免影响分类
		
		for(int i=0;i<terms.length;i++)
		System.out.println(terms[i]);

	}

}
