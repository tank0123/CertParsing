
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
		
		//1. CrawlerFromSuspectURL�� �̿��Ͽ� ������ ����Ʈ���� HTML�ҽ� �θ��� �Ľ��Ͽ� TXT���Ϸ� ����
		CrawlerFromSuspectURL cFs = new CrawlerFromSuspectURL();
		//2. ������ TXT������ �о�鿩 ���Ͻø� ���� IP�� ����� ������ �ٿ�ε�
		new GetFileFromURLWithProxy();
		// 3. �ٿ�ε� �� ���ϵ��� ������ Ǯ��, ������ ���� �Ľ��Ͽ� Mysql DB�� ����
		new ParsingAndInsertMysql();
		
		
	}

}
