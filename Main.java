
package CertParsing;

import CertParsing.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//new Crawler();
		//new GetFileFromURLWithProxy();
		//new GetFileFromURL();
		//new ParsingAndSavaSQL();
		//new ParsingAndInsertMysql();
		//new UnzipModuleTest() ;
		
		//1. CrawlerFromSuspectURL을 이용하여 용의자 사이트에서 HTML소스 부른뒤 파싱하여 TXT파일로 저장
		CrawlerFromSuspectURL cFs = new CrawlerFromSuspectURL();
		//2. 저장한 TXT파일을 읽어들여 프록시를 통해 IP를 숨기고 인증서 다운로드
		new GetFileFromURLWithProxy();
		// 3. 다운로드 한 파일들의 압축을 풀고, 인증서 값을 파싱하여 Mysql DB에 저장
		new ParsingAndInsertMysql();
		
		
	}

}
