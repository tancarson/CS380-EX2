import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Ex2Client {
	static Socket socket = null;
	static byte[] singleByte = new byte[1];
	static byte[] serverRaw = new byte[200];
	static byte[] compoundArray = new byte[100];
	
	public static void main(String[] args) {
		//Connect to the server
		try {
			socket = new Socket("codebank.xyz", 38102);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Get the bytes from the server
		try {
			System.out.print("Bytes from server: ");
			for(int i = 0; i < 200; i++){
				socket.getInputStream().read(singleByte);
				serverRaw[i] = singleByte[0];
				System.out.printf("%01x", serverRaw[i]);
			}
			System.out.println();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		//Put the bytes together
		System.out.print("Compound bytes: ");
		for(int i = 0; i < 100; i++){
			byte firstHalf = (byte) serverRaw[i * 2];
			byte secondHalf = (byte) serverRaw[i * 2 + 1];
			compoundArray[i] = (byte) ((byte) (firstHalf << 4) + (secondHalf));
			System.out.printf("%02x ",compoundArray[i]);
		}
		System.out.println();

		//Generate the CRC
		CRC32 crc = new CRC32();
		crc.update(compoundArray);
		
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt((int) crc.getValue());
		
		//Send the CRC
		try {
			System.out.print("Sending Bytes: ");
			for(int i = 0; i < 4; i++){
				socket.getOutputStream().write(b.array()[i]);
				System.out.printf("%02x ",b.array()[i]);
			}	
			System.out.println();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Get the server Response
		try {
			socket.getInputStream().read(serverRaw);
			System.out.println("Server response: " + serverRaw[0]);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Close the socket
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
