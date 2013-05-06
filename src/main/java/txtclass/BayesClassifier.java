package txtclass;

import txtclass.ChineseSpliter;
import txtclass.ClassConditionalProbability;
import txtclass.PriorProbability;
import txtclass.TrainingDataManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
* 朴素贝叶斯分类器
*/
public class BayesClassifier 
{
	private TrainingDataManager tdm;//训练集管理器
	private static double zoomFactor = 10.0f;
	/**
	* 默认的构造器，初始化训练集
	*/
	public BayesClassifier() 
	{
		tdm =new TrainingDataManager();
	}

	/**
	* 计算给定的文本属性向量X在给定的分类Cj中的类条件概率
	* <code>ClassConditionalProbability</code>连乘值
	* @param X 给定的文本属性向量
	* @param Cj 给定的类别
	* @return 分类条件概率连乘值，即<br>
	*/
	float calcProd(String[] X, String Cj) 
	{
		float ret = 1.0F;
		// 类条件概率连乘
		for (int i = 0; i <X.length; i++)
		{
			String Xi = X[i];
			//因为结果过小，因此在连乘之前放大10倍，这对最终结果并无影响，因为我们只是比较概率大小而已
			ret *=ClassConditionalProbability.calculatePxc(Xi, Cj)*zoomFactor;
		}
		// 再乘以先验概率
		ret *= PriorProbability.calculatePc(Cj);
		return ret;
	}
	/**
	* 去掉停用词
	* @param text 给定的文本
	* @return 去停用词后结果
	*/
	public String[] DropStopWords(String[] oldWords)
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
	/**
	* 对给定的文本进行分类
	* @param text 给定的文本
	* @return 分类结果
	*/
	@SuppressWarnings("unchecked")
	public String classify(String text) 
	{
		String[] terms = null;
		terms= ChineseSpliter.split(text, " ").split(" ");//中文分词处理(分词后结果可能还包含有停用词）
		terms = DropStopWords(terms);//去掉停用词，以免影响分类
		
		String[] Classes = tdm.getTraningClassifications();//分类
		float probility = 0.0F;
		List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//分类结果
//		for (int i = 0; i <Classes.length; i++) 
//			System.out.println(Classes[i]);
		for (int i = 0; i <Classes.length; i++) 
		{
			String Ci = Classes[i];//第i个分类
			probility = calcProd(terms, Ci);//计算给定的文本属性向量terms在给定的分类Ci中的分类条件概率
			//保存分类结果
			ClassifyResult cr = new ClassifyResult();
			cr.classification = Ci;//分类
			cr.probility = probility;//关键字在分类的条件概率
			System.out.println("In process....");
			System.out.println(Ci + "：" + probility);
			crs.add(cr);
		}
		//对最后概率结果进行排序
		java.util.Collections.sort(crs,new Comparator() 
		{
			public int compare(final Object o1,final Object o2) 
			{
				final ClassifyResult m1 = (ClassifyResult) o1;
				final ClassifyResult m2 = (ClassifyResult) o2;
				final double ret = m1.probility - m2.probility;
				if (ret < 0) 
				{
					return 1;
				} 
				else 
				{
					return -1;
				}
			}
		});
		//返回概率最大的分类
		return crs.get(0).classification;
	}
	
	public static void main(String[] args)
	{
//		ChineseTextGet chineseTextGet=new ChineseTextGet("Test Dataset\\1.txt");
		String text = "本报讯 (记者 王京) 联想THINKPAD近期几乎全系列笔记本电脑降价促销，最高降幅达到800美元，降幅达到42%。这是记者昨天从联想美国官方网站发现的。联想相关人士表示，这是为纪念新联想成立1周年而在美国市场推出的促销，产品包括THINKPAD T、X以及Z系列笔记本。促销不是打价格战，THINK品牌走高端商务路线方向不会改变。";
//		String text=chineseTextGet.gettext();
//		System.out.println(text);
		BayesClassifier classifier = new BayesClassifier();//构造Bayes分类器
		String result = classifier.classify(text);//进行分类
		System.out.println("此项属于["+result+"]");
		
	}
}