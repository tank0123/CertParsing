package CertParsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler {
	
	public Crawler() {
		getHTMLSource();
	}
	
	private void getHTMLSource() {
		int i = 0;
		try {
			String proxyIp[] = new String[10];
			String proxyPort[] = new String[10];
			
			String urlStr = "https://free-proxy-list.net/";
			
			Document doc = Jsoup.connect(urlStr).get();
			
			//System.out.println(doc.toString());
			
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
				//System.out.println(portMatcher.group());
				//i++;
				proxyPort[i] = portMatcher.group();
				proxyPort[i] = proxyPort[i].substring(1, proxyPort[i].length()-1);
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
}
