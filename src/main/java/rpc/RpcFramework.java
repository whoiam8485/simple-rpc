package rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {
	
	
	
	/**
	 * 
	 * @param service
	 * @param port
	 * @throws Exception
	 */
    public static void publicRpcServices(final Object service, int port) throws Exception {  
        if (service == null)  {
            throw new IllegalArgumentException("service instance == null");  
        }
        if (port <= 0 || port > 65535)  {
            throw new IllegalArgumentException("Invalid port " + port);  
        }
        System.out.println("Export service " + service.getClass().getName() + " on port " + port);  
        ServerSocket server = new ServerSocket(port);  
        for(;;) {  
            try {  
                final Socket socket = server.accept();  
                new Thread(new Runnable() {  
                    public void run() {  
                        try {  
                            try {  
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());  
                                try {  
                                    String methodName = input.readUTF();  
                                    Class<?>[] parameterTypes = (Class<?>[])input.readObject();  
                                    Object[] arguments = (Object[])input.readObject();  
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());  
                                    try {  
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);  
                                        Object result = method.invoke(service, arguments);  
                                        output.writeObject(result);  
                                    } catch (Throwable t) {  
                                        output.writeObject(t);  
                                    } finally {  
                                        output.close();  
                                    }  
                                } finally {  
                                    input.close();  
                                }  
                            } finally {  
                                socket.close();  
                            }  
                        } catch (Exception e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }).start();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    
    /**
     * 
     * @param interfaceClass
     * @param host
     * @param port
     * @return
     * @throws Exception
     */
    public static <T> T getRpcService(final Class<T> interfaceClass, final String host, final int port) throws Exception {  
        if (interfaceClass == null)  {
            throw new IllegalArgumentException("Interface class == null");  
        }
        if (! interfaceClass.isInterface())  {
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");  
        }
        if (host == null || host.length() == 0)  {
            throw new IllegalArgumentException("Host == null!");  
        }
        if (port <= 0 || port > 65535)  {
            throw new IllegalArgumentException("Invalid port " + port);  
        }
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);  
        return RpcProxyTool.generateProxy(interfaceClass, host, port);
    }  
  

}
