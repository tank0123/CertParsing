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

// ������ URL�� ���� ������ �ٿ�޴� Ŭ����
public class GetFileFromURLWithProxy {
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

		int code = 0;

		try {
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			// �Է¹��� URL�ּҷ� URL��ü ����
			Url = new URL(fileAddress);
			
			do {
				setProxy();
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

		String proxyIpList[] = { "35.236.37.114", "31.47.189.14", "118.172.201.27", "93.91.218.72" };
		String proxyPortList[] = { "8080", "38473", "59332", "46292" };

		int index = ran.nextInt(4);
		
		System.out.println("changing proxy : "+index);

		try {
			System.setProperty("http.proxyHost", proxyIpList[index]);
			System.setProperty("http.proxyPort", proxyPortList[index]);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
