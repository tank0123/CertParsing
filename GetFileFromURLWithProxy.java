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

// ������ URL�� ���� ������ �ٿ�޴� Ŭ����
public class GetFileFromURLWithProxy {
	private String proxyIp[] = new String[10];
	private String proxyPort[] = new String[10];
	private int index = 0;
	private int code = 0;
	
	Random ran = new Random();
	final static int size = 1024;

	// �����ڸ� ���� �ѹ��� �����
	public GetFileFromURLWithProxy() {
		readIPAndDownload();
	}

	// ����� TXT������ �Ľ��Ͽ� ������ �ٿ�޴� �޼���
	private void readIPAndDownload() {
		try {
			System.out.println("-------Download Start------");
			// ����������� ���� ���� ��ü ����
			File file = new File("C://BoBTest//CertDown//IPList.txt");

			// Buffered Reader�� ���� ���پ� �Է�
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);

			String line = new String();
			String tabSplit[];

			// ��� ���� ������ ���� ���� �б�
			while ((line = bfr.readLine()) != null) {
				// ���� �������� ���ڿ��� �ڸ�
				tabSplit = line.split("\t");
				// �ڸ� ���ڿ��� ù��° ���� �������ּҿ� URL�ּҸ� �����Ͽ� �ٿ� ��ũ �ϼ�
				String Buf = "http://fl0ckfl0ck.work/cert/" + tabSplit[0];
				// �ϼ��� �ٿ� ��ũ�� �����̸�, ���� �ٿ� ��η� �Է¹��� ���� ���� ���ڷ� �־� ���� �ٿ� �޼��� ȣ��
				fileUrlReadAndDownload(Buf, tabSplit[0], "C://BoBTest//Proxy");
			}
			System.out.println("It's Done!!");
			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ������ URL�ּҿ� �����̸�, �ٿ�ε� ��θ� �޾� ������ �ٿ�޴� �޼���
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
			// �Է¹��� URL�ּҷ� URL��ü ����
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


			// ������ ���� ���� �ٿ�ε� ����
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
