package CertParsing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// 지정된 URL을 통해 파일을 다운받는 클래스
public class GetFileFromURLWithProxy {
	private String proxyIp[] = new String[10];
	private String proxyPort[] = new String[10];
	private int index = 0;
	private int code = 0;
	
	Random ran = new Random();
	final static int size = 1024;

	// 생성자를 통해 한번만 실행됨
	public GetFileFromURLWithProxy() {
		readIPAndDownload();
	}

	// 저장된 TXT파일을 파싱하여 파일을 다운받는 메서드
	private void readIPAndDownload() {
		try {
			System.out.println("-------Download Start------");
			// 파일입출력을 위한 파일 객체 생성
			File file = new File("C://BoBTest//CertDown//IPList.txt");

			// Buffered Reader를 통해 한줄씩 입력
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			String tabSplit[];

			// 모든 줄이 끝날때 까지 파일 읽기
			while ((line = bfr.readLine()) != null) {
				// 탭을 기준으로 문자열을 자름
				tabSplit = line.split("\t");
				// 자른 문자열의 첫번째 값인 아이피주소와 URL주소를 결합하여 다운 링크 완성
				String Buf = "http://fl0ckfl0ck.work/cert/" + tabSplit[0];
				// 완성된 다운 링크와 파일이름, 파일 다운 경로로 입력받을 값을 각각 인자로 넣어 파일 다운 메서드 호출
				fileUrlReadAndDownload(Buf, tabSplit[0], "C://BoBTest//CertDown");
			}
			System.out.println("It's Done!!");
			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 파일의 URL주소와 파일이름, 다운로드 경로를 받아 파일을 다운받는 메서드
	private void fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir) {

		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		HttpURLConnection huc = null;

		

		try {
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			// 입력받은 URL주소로 URL객체 생성
			Url = new URL(fileAddress);
			
			do {
				if(code != 200) {
					setProxy();
				}
				huc = (HttpURLConnection) Url.openConnection();
				huc.setRequestMethod("GET"); // OR huc.setRequestMethod ("HEAD");
				huc.connect();
				code = huc.getResponseCode();
				System.out.println(code);
			}
			while (code != 200);

			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));


			// 연결한 값을 통해 다운로드 진행
			is = huc.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setProxy() {
		// https://free-proxy-list.net/
		if(index <= 10) {
			getHTMLSource();
			index = 0;
		}
		System.out.println("changing proxy : "+index);

		try {
			System.setProperty("http.proxyHost", proxyIp[index]);
			System.setProperty("http.proxyPort", proxyPort[index]);
			index++;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	// Get Free Proxy information from https://free-proxy-list.net/
	private void getHTMLSource() {
		int i = 0;
		try {
			String urlStr = "https://free-proxy-list.net/";
			
			Document doc = Jsoup.connect(urlStr).get();
			
			
			Pattern ipPattern = Pattern.compile("(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))");
			Pattern portPattern = Pattern.compile(">[0-9]{1,5}<");
					
			Matcher ipMatcher = ipPattern.matcher(doc.toString());
			Matcher portMatcher = portPattern.matcher(doc.toString());
			
			while (ipMatcher.find() && (i < 10)) {
				proxyIp[i++] = ipMatcher.group();
			}
			
			i = 0;
			while (portMatcher.find() && (i < 10)) {
				proxyPort[i] = portMatcher.group();
				proxyPort[i] = proxyPort[i].substring(1, proxyPort[i].length()-1);
				i++;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
