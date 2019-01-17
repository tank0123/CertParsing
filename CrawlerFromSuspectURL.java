package CertParsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerFromSuspectURL {
	
	public CrawlerFromSuspectURL() {
		getHTMLSourceFlock();
	}
	
	private void getHTMLSourceFlock() {
		try {
			String urlStr = "http://fl0ckfl0ck.work/cert";
			
			

			/*
			 * GET HTML DATA 1. org.jsoup.Connection.Response response =
			 * Jsoup.connect("http://fl0ckfl0ck.work/cert").
			 * method(org.jsoup.Connection.Method.GET).execute(); Document doc3 =
			 * response.parse(); String html = doc3.html();
			 * 
			 * 2. Document doc2 = Jsoup.connect(urlStr).post();
			 * 
			 * 3. Document doc = Jsoup.connect(urlStr).post();
			 */

			// ���� Document doc = Jsoup.connect(urlStr).post();
			// �� ���� HTML���� �ܾ������, ���� ��뷮 ������ �̱� �������Ʒ��� ���� �ڵ带 ���
			Document doc = Jsoup.connect(urlStr).header("Accept-Encoding", "gzip, deflate")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
					.maxBodySize(0).timeout(600000).get();

			
			// txt���Ϸ� �����
			//outputFile(doc.toString()); 
			
			// �׽�Ʈ �� �ܼ� ���
			// System.out.println(doc.toString());
			
			//1. �� ����Ʈ���� tr���� �����Ѵ�
			Elements articles = doc.select("tr");
			// 2. tr���� ������ ���Ѵ�.
			int size = articles.size();
			
			// ���� ����� ���� ���� �迭�� �ӽ÷� �����Ѵ�.
			String valueBuffer[] = new String[(size - 5)];
			
			int countBuff = 0;
			int index =0;
			
			//�� 3 �� 1
			System.out.println(size);
			
			for(Element article : articles) {
				//System.out.println(article);
				
				// �� ����Ʈ���� f12�� Ȯ�ε� �������� ������ ���� �־��ְ�, ���� ���� �̾Ƴ���.
				if(countBuff < 3) {
					countBuff++;
					continue;
				}
				if(countBuff >= (size-2)) {
					break;
				}
				
				valueBuffer[index++] = article.select("td:nth-child(2)").text()+"\t"+article.select("td:nth-child(3)").text()+"\r\n"; 
				countBuff++;
				
			}
			
			outputFile(valueBuffer);
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void outputFile(String[] HTMLSource) {
		try {
			// ���ڷ� ���� ������ ��θ� �޾� ������ ���� �Ľ�
			File file = new File("C:\\BoBTest\\Proxy\\IPList.txt");

			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bfr = new BufferedWriter(fileWriter);
			
			for (int i = 0; i < HTMLSource.length; i++) {
				bfr.write(HTMLSource[i]);
			}

			bfr.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
}
