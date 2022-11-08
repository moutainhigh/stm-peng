import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;


public class Test {

	public static void main(String[] args) throws UnknownHostException, IOException {
//		Socket s = new Socket("172.16.8.179", 9999);
//		s.close();
		Socket s = new Socket();
		SocketAddress endpoint = new InetSocketAddress("172.16.8.179", 9999);
		s.connect(endpoint);
		s.close();
		System.out.println("ok");
	}

}
