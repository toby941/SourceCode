package txtclass;

import java.util.Vector;

import txtclass.ChineseSpliter;

public class text {

	/**
	 * @param args
	 */
	
	/**
	* ȥ��ͣ�ô�
	* @param text �������ı�
	* @return ȥͣ�ôʺ���
	*/
	public static String[] DropStopWords(String[] oldWords)
	{
		Vector<String> stopdictionary = new Vector<String>();
		stopdictionary = ReadFileToVector.ReadFile("stoplis.txt");// ���ļ��м��شʵ䣬����ŵ�vector������
		
		Vector<String> v1 = new Vector<String>();
		for(int i=0;i<oldWords.length;++i)
		{
			if(!stopdictionary.contains(oldWords[i])) //����ͣ�ô�
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
		
		String text = "����Ѷ (���� ����) ����THINKPAD���ڼ���ȫϵ�бʼǱ����Խ��۴�������߽����ﵽ800��Ԫ�������ﵽ42%�����Ǽ�����������������ٷ���վ���ֵġ����������ʿ��ʾ������Ϊ�������������1������������г��Ƴ��Ĵ�������Ʒ����THINKPAD T��X�Լ�Zϵ�бʼǱ����������Ǵ�۸�ս��THINKƷ���߸߶�����·�߷��򲻻�ı䡣";
		String[] terms = null;
		terms= ChineseSpliter.split(text, " ").split(" ");//���ķִʴ���(�ִʺ������ܻ�������ͣ�ôʣ�
		terms = DropStopWords(terms);//ȥ��ͣ�ôʣ�����Ӱ�����
		
		for(int i=0;i<terms.length;i++)
		System.out.println(terms[i]);

	}

}
