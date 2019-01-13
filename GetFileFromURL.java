package CertParsing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetFileFromURL {
	/**
	 * 버퍼 사이즈
	 */
	final static int size = 1024;
	
	public GetFileFromURL() {
		readIPAndDownload();
	}
	
	private void readIPAndDownload() {
		try {
			System.out.println("-------Download Start------");
			File file = new File("C://BoBTest//CertDown//IPList2.txt");
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);
			
			String line = new String();
			String tabSplit[];
			
			while ( (line = bfr.readLine()) != null) {
				tabSplit = line.split("\t");
				String Buf = "http://fl0ckfl0ck.work/cert/"+tabSplit[0];
				fileUrlReadAndDownload(Buf, tabSplit[0], "C://BoBTest//CertDown");
			}
			System.out.println("It's Done!!");
			bfr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * fileAddress에서 파일을 읽어, 다운로드 디렉토리에 다운로드
	 * 
	 * @param fileAddress
	 * @param localFileName
	 * @param downloadDir
	 */
	private void fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir) {
		
		
		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		try {

			

			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			Url = new URL(fileAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));

			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}

			/*System.out.println("Download Successfully.");
			System.out.println("File name : " + localFileName);
			System.out.println("of bytes  : " + byteWritten);
			System.out.println("-------Download End--------");*/

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

	/**
	 * 
	 * @param fileAddress
	 * @param downloadDir
	 */
	private void fileUrlDownload(String fileAddress, String downloadDir) {

		int slashIndex = fileAddress.lastIndexOf('/');
		int periodIndex = fileAddress.lastIndexOf('.');

		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		String fileName = fileAddress.substring(slashIndex + 1);

		if (periodIndex >= 1 && slashIndex >= 0 && slashIndex < fileAddress.length() - 1) {
			fileUrlReadAndDownload(fileAddress, fileName, downloadDir);
		} else {
			System.err.println("path or file name NG.");
		}
	}
	
	private void txtParsing(String FileDir) {
		try {
			File file = new File(FileDir);
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileReader);
			
			FileWriter fileWriter = new FileWriter("C://BoBTest//certDownload//IPs.txt", true);
			BufferedWriter bfw = new BufferedWriter(fileWriter);
			
			String line = new String();
			
			while ( (line = bfr.readLine()) != null) {
				String strBuf[] = line.split("\t");
				System.out.println(strBuf[0]);
				bfw.write(strBuf[0]+"\r\n");
			}
			
			
			/*FileInputStream fis = new FileInputStream(FileDir);
			StringBuffer strBuf = new StringBuffer();
			char buf;
			
			while ( (buf=(char)fis.read()) != -1) {
				strBuf.append(buf);
			}
			fis.close();*/
			
			bfr.close();
			bfw.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("파일 입출력 동안 에러발생!");
		}
	}
	
	private void parseFile(String strBuf) {
		String strArr[][] = new String[36834][];
		
		for (int i = 0; i < strArr.length; i++) {
			strArr[i] = strBuf.split("\t");
			System.out.println(strArr[i][0]);
		}
		
	}
}
