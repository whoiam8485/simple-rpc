package rpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/**
 * 
 * @author gshen
 *
 */
public class ObjectReader  {

	
	/**
	 * 
	 * @param incoming
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readObject(Socket incoming) {
		T object = null;
		try {
			SocketChannel sc = incoming.getChannel();
			ByteBuffer bbIn = ByteBuffer.allocate(85); // ����ʵ�������������Ҫ���ֽ��������õ�85�Ǵ�cli��ӡ�������ֽ���Ŀ
			sc.read(bbIn);
			sc.close();
			ByteArrayInputStream bIn = new ByteArrayInputStream(bbIn.array());
			ObjectInputStream in = new ObjectInputStream(bIn);
			object = (T) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

}
