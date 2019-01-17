package CertParsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	public Crawler() {
		getHTMLSourceFlock();
		// getHTMLSource();
	}

	private void getHTMLSource() {
		int i = 0;
		try {
			String proxyIp[] = new String[10];
			String proxyPort[] = new String[10];

			String urlStr = "https://free-proxy-list.net/";

			Document doc = Jsoup.connect(urlStr).get();

			// System.out.println(doc.toString());

			Pattern ipPattern = Pattern.compile("(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
			Pattern portPattern = Pattern.compile(">[0-9]{1,5}<");

			Matcher ipMatcher = ipPattern.matcher(doc.toString());
			Matcher portMatcher = portPattern.matcher(doc.toString());

			// 추출된 시간정보와 아이피주소는 ipNTime이라는 문자열에 저장됨
			while (ipMatcher.find() && (i < 10)) {
				proxyIp[i++] = ipMatcher.group();
			}

			i = 0;
			while (portMatcher.find() && (i < 10)) {
				// System.out.println(portMatcher.group());
				// i++;
				proxyPort[i] = portMatcher.group();
				proxyPort[i] = proxyPort[i].substring(1, proxyPort[i].length() - 1);
				i++;
			}

			for (i = 0; i < 10; i++) {
				System.out.println(proxyIp[i]);
				System.out.println(proxyPort[i]);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getHTMLSourceFlock() {
		String proxyIp[] = new String[10];
		String proxyPort[] = new String[10];
		int i = 0;
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

			// 보통 Document doc = Jsoup.connect(urlStr).post();
			// 를 통해 HTML값을 긁어오지만, 워낙 대용량 데이터 이기 때문에아래와 같은 코드를 사용
			Document doc = Jsoup.connect(urlStr).header("Accept-Encoding", "gzip, deflate")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
					.maxBodySize(0).timeout(600000).get();

			
			// txt파일로 출력함
			//outputFile(doc.toString()); 
			
			// 테스트 용 콘솔 출력
			// System.out.println(doc.toString());
			
			//웹 사이트에서 tr값을 선택한다
			Elements articles = doc.select("tr");
			int size = articles.size();
			//size -= 4;
			String valueBuffer[] = new String[(size - 5)];
			
			int countBuff = 0;
			int index =0;
			
			//앞 3 뒤 1
			System.out.println(size);
			
			for(Element article : articles) {
				//System.out.println(article);
				
				// 웹 사이트에서 f12로 확인된 값을통해 셀렉터 값을 넣어주고, 순수 값만 뽑아낸다.
				if(countBuff < 3) {
					countBuff++;
					continue;
				}
				if(countBuff >= (size-2)) {
					break;
				}
				
				/*try {
					Thread.sleep(100);
				} catch (Exception e) {
					// TODO: handle exception
				}*/
				valueBuffer[index++] = article.select("td:nth-child(2)").text()+"\t"+article.select("td:nth-child(3)").text()+"\r\n"; 
				/*System.out.println(article.select("td:nth-child(2)").text());
				System.out.println(article.select("td:nth-child(3)").text());*/
				
				countBuff++;
				
				
				/*String href = article.tag("td");
						//.attr("href");
				String time = article.attr("align");
				
				System.out.println(href);
				System.out.println(time);*/
			}
			
			/*for (int j = 0; j < valueBuffer.length; j++) {
				System.out.print(valueBuffer[j]);
			}*/
			
			outputFile(valueBuffer);
			

			/*// ----------여기 아래부터는 정규식 이용 파서 구현-------------------
			Pattern ipPattern = Pattern.compile("(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
			Pattern portPattern = Pattern.compile(">[0-9]{1,5}-[0-9]{1,2}-[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}<");

			Matcher ipMatcher = ipPattern.matcher(doc.toString());
			Matcher portMatcher = portPattern.matcher(doc.toString());

			// 추출된 시간정보와 아이피주소는 ipNTime이라는 문자열에 저장됨 
			while (ipMatcher.find() && (i < 10))
			{
				proxyIp[i++] = ipMatcher.group();
			}

			i = 0;
			while (portMatcher.find() && (i < 10)) {
				// System.out.println(portMatcher.group()); //i++; proxyPort[i] =
				portMatcher.group();
				proxyPort[i] = proxyPort[i].substring(1, proxyPort[i].length() - 1);
				i++;
			}

			for (i = 0; i < 10; i++) {
				System.out.println(proxyIp[i]);
				System.out.println(proxyPort[i]);
			}*/

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void outputFile(String[] HTMLSource) {
		try {
			// 인자로 받은 인증서 경로를 받아 인증서 내용 파싱
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
