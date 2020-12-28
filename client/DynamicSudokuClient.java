import java.util.Properties;
import java.rmi.server.RMIClassLoader;
import java.lang.reflect.Constructor;
import java.rmi.RMISecurityManager;


public class DynamicSudokuClient {

	public static void main(String args[]) {
		try {

			System.setProperty("java.security.policy","client.policy");
			System.setProperty("java.rmi.server.codebase","file:../www/");

			//set security manager
			if (System.getSecurityManager() == null){
				System.setSecurityManager(new RMISecurityManager());
			}

			//get the path of the codebase:
			Properties p = System.getProperties();
			String url = p.getProperty("java.rmi.server.codebase");

			//load client class from the server
			Class clientClass = RMIClassLoader.loadClass(url, "SudokuClient");
			clientClass.newInstance();

			//get constructors
			//Constructor[] c = clientClass.getConstructors();
			//c[0].newInstance();
		}

		catch(Exception e){
			System.out.println("error:");
			System.out.println(e.toString());
		}
	}
}
