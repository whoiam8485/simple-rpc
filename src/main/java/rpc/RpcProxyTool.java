package rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * 
 * @author gshen
 *
 */
public class RpcProxyTool {

	/**
	 * 
	 * @param interfaceClass
	 * @param host
	 * @param port
	 * @return
	 */
	public static <T> T generateProxy(final Class<T> interfaceClass, final String host, final Integer port) {
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
						Socket socket = new Socket(host, port);
						try {
							ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
							try {
								output.writeUTF(method.getName());
								output.writeObject(method.getParameterTypes());
								output.writeObject(arguments);
								ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
								try {
									Object result = input.readObject();
									if (result instanceof Throwable) {
										throw (Throwable) result;
									}
									return result;
								} finally {
									input.close();
								}
							} finally {
								output.close();
							}
						} finally {
							socket.close();
						}
					}
				});

		return proxy;
	}

}
