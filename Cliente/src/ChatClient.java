/* ChatClient.java */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

public class ChatClient {

private static int port = 8080; /* port to connect to */

private static String host = "192.168.1.161"; /* host to connect to */

private static BufferedReader stdIn;

private static String nick;

/**
 * Read in a nickname from stdin and attempt to authenticate with the 
 * server by sending a NICK command to @out. If the response from @in
 * is not equal to "OK" go bacl and read a nickname again
 */
private static String getNick(BufferedReader in, 
                              PrintWriter out, Socket s) throws IOException {
    System.out.print("Enter your nick: ");
    String msg = stdIn.readLine();
   /* 
    String msg = in.readLine();
    StringTokenizer st = new StringTokenizer(msg);
	String method = st.nextToken();
	//Si es POST entra, en caso contrario (GET) hace solo el resto
	if (method.equalsIgnoreCase("POST")) 
	{
		String uri = decodePercent(st.nextToken());
        Properties parms = new Properties();
        int qmi = uri.indexOf('?');
        
        if (qmi >= 0) 
        {
        	
        	try {
				decodeParms(uri.substring(qmi + 1), parms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	uri = decodePercent(uri.substring(0, qmi));
        }
		Properties header = new Properties();

		if (st.hasMoreTokens()) 
		{
	        String line = in.readLine();
	          while (line.trim().length() > 0) 
	          {
	        	  int p = line.indexOf(':');
	        	  header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
	        	  line = in.readLine();
	          }
	    }
		long size = 0x7FFFFFFFFFFFFFFFl;
         
        String contentLength = header.getProperty("content-length");
        
        if (contentLength != null)
              size = Integer.parseInt(contentLength);
        

		String postLine = "";
		char buf[] = new char[512];
		int read = in.read(buf);
		while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) 
		{
		size -= read;
		postLine += String.valueOf(buf, 0, read);
		if (size > 0)
			read = in.read(buf);
	        }
	         
			postLine = postLine.trim();
	          
			try {
				decodeParms(postLine, parms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			guardar(parms);
			String name = parms.getProperty("Name");
			out.println("NICK " + name);
			System.out.println(name);
			response = protocol.process("NICK " + name);
            out.println("SERVER: " + response);
	    }
	if(method.equals("GET")){
		
		
		
		
		String uri = decodePercent(st.nextToken());
        Properties parms = new Properties();
    int qmi = uri.indexOf('?');
    if (qmi >= 0) 
    {
    	try {
			decodeParms(uri.substring(qmi + 1), parms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	uri = decodePercent(uri.substring(0, qmi));
    }
    Properties header = new Properties();

		if (st.hasMoreTokens()) 
		{
	        String line = in.readLine();
	          while (line.trim().length() > 0) 
	          {
	        	  int p = line.indexOf(':');
	        	  header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
	        	  line = in.readLine();
	          }
	    }
		
		
        
        
		
		System.out.println("--- Client request: " + msg);

		//out = new PrintWriter(client.getOutputStream(), true);
		
		//el metodo es GET
		if (uri.equals("/"))
		{	
			
			out.println
			(
					"<html>"+
							"<head >"+
								"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>"+
								"<title>Contactos</title>"+
							"</head>"+
					"<body>"+
					"<center>"+
					"<h1>Contactos</h1>"+
					"<table style = \"width:300px\" border= \"1\" align= \"center\" "+
						"<tr>"+
							"<td>Nombre</td>"+
							"<td>IP</td>"+
							"<td>Puerto</td>"+
						"</tr>"						
			);
			leer(out);
			out.println
			(
					"</table>"+
					"<form action= '/ingresar'>"+
				    "<input type='submit' value='Agregar Contacto'>"+
				    "</form>"+
					"</center>"+
					"</body>"+
					"</html>"
			);		
			
		}
		else if (uri.equals("/ingresar"))
		{
			InputStream archivo = new FileInputStream ("form.html");
 			String form = IOUtils.toString(archivo, "UTF-8");
			out.println(form);
		}
		out.flush();
		out.close();
		
		
		
		
		
	}
	*/
	//System.out.println(msg);
    
    
    
    
    
    out.println("NICK " + msg);
    String serverResponse = in.readLine();
    if ("SERVER: OK".equals(serverResponse)) return msg;
    System.out.println(serverResponse);
    return getNick(in, out, s);
}

public static void main (String[] args) throws IOException {

    Socket server = null;

    try {
        server = new Socket(host, port);
    } catch (UnknownHostException e) {
        System.err.println(e);
        System.exit(1);
    }

    stdIn = new BufferedReader(new InputStreamReader(System.in));

    /* obtain an output stream to the server... */
    PrintWriter out = new PrintWriter(server.getOutputStream(), true);
    /* ... and an input stream */
    BufferedReader in = new BufferedReader(new InputStreamReader(
                server.getInputStream()));

    nick = getNick(in, out, server);

    /* create a thread to asyncronously read messages from the server */
    ServerConn sc = new ServerConn(server);
    Thread t = new Thread(sc);
    t.start();

    String msg;
    /* loop reading messages from stdin and sending them to the server */
    while ((msg = stdIn.readLine()) != null) {
        out.println(msg);
    }
  }















private static void Cosas(Socket s) 
{ 
	BufferedReader in;
	PrintWriter out;
	String request;

		try 
		{
			 			
			String webServerAddress = s.getInetAddress().toString();
			System.out.println("New Connection:" + webServerAddress);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			request = in.readLine();
			System.out.println("\n"+request);

			StringTokenizer st = new StringTokenizer(request);
			//ESTO (method) DEFINE SI ES GET O POST
			String method = st.nextToken();
			
	        String uri = decodePercent(st.nextToken());
	        
	        Properties parms = new Properties();
        int qmi = uri.indexOf('?');
        if (qmi >= 0) 
        {
        	decodeParms(uri.substring(qmi + 1), parms);
        	uri = decodePercent(uri.substring(0, qmi));
        }
	        
        Properties header = new Properties();
			
			if (st.hasMoreTokens()) 
			{
		        String line = in.readLine();
		          while (line.trim().length() > 0) 
		          {
		        	  int p = line.indexOf(':');
		        	  header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
		        	  line = in.readLine();
		          }
		    }
			
			//Si es POST entra, en caso contrario (GET) hace solo el resto
			if (method.equalsIgnoreCase("POST")) 
			{
				long size = 0x7FFFFFFFFFFFFFFFl;
		         
		        String contentLength = header.getProperty("content-length");
		        
		        if (contentLength != null)
		              size = Integer.parseInt(contentLength);
		        

			String postLine = "";
			char buf[] = new char[512];
			int read = in.read(buf);
			while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) 
			{
			size -= read;
			postLine += String.valueOf(buf, 0, read);
			if (size > 0)
				read = in.read(buf);
		        }
		         
				postLine = postLine.trim();
		          
				decodeParms(postLine, parms);
				
				guardar(parms);
				String name = parms.getProperty("Name");
				System.out.println(name);
		    }
	        
	        
			
			System.out.println("--- Client request: " + request);

			out = new PrintWriter(s.getOutputStream(), true);
			
			//el metodo es GET
			if (uri.equals("/"))
			{	
				
				out.println
				(
						"<html>"+
								"<head >"+
									"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>"+
									"<title>Contactos</title>"+
								"</head>"+
						"<body>"+
						"<center>"+
						"<h1>Contactos</h1>"+
						"<table style = \"width:300px\" border= \"1\" align= \"center\" "+
							"<tr>"+
								"<td>Nombre</td>"+
								"<td>IP</td>"+
								"<td>Puerto</td>"+
							"</tr>"						
				);
				leer(out);
				out.println
				(
						"</table>"+
						"<form action= '/ingresar'>"+
					    "<input type='submit' value='Agregar Contacto'>"+
					    "</form>"+
						"</center>"+
						"</body>"+
						"</html>"
				);		
				
			}
			else if (uri.equals("/ingresar"))
			{
				InputStream archivo = new FileInputStream ("form.html");
	 			String form = IOUtils.toString(archivo, "UTF-8");
				out.println(form);
			}
			out.flush();
			out.close();
			s.close();
		} 
		catch (IOException e) 
		{ 
			System.out.println("Failed respond to client request: " + e.getMessage());
		} catch (InterruptedException e) {
		e.printStackTrace();
	} 
		finally 
		{ 
			if (s != null) 
			{ 
				try 
				{ 
					s.close();
				} 
				catch (IOException e) 
				{ 
					e.printStackTrace();
				} 
			} 
		} 
		return;
	}

private static String decodePercent(String str) {
      try {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
          char c = str.charAt(i);
          switch (c) {
          case '+':
            sb.append(' ');
            break;
          case '%':
            sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
            i += 2;
            break;
          default:
            sb.append(c);
            break;
          }
        }
        return new String(sb.toString().getBytes());
      } catch (Exception e) {
        
      }
	return str;
}

private static void decodeParms(String parms, Properties p) throws InterruptedException {
      if (parms == null)
        return;

      StringTokenizer st = new StringTokenizer(parms, "&");
      while (st.hasMoreTokens()) {
        String e = st.nextToken();
        int sep = e.indexOf('=');
        if (sep >= 0)
          p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
      }
}

public static void guardar(Properties contactos) {
	try {

		String name = contactos.getProperty("Name");
		if((contactos.getProperty("Name")).equals("")){
			name = "-";
		}
		
		String ip = contactos.getProperty("Aipi");
		if((contactos.getProperty("Aipi")).equals("")){
			ip = "-";
		}
		
		String port = contactos.getProperty("Port");
		if((contactos.getProperty("Port")).equals("")){
			port = "-";
		}
		
		String escribe = new String (name + "," + ip + "," + port + "\n");

		File file = new File("contactos.txt");
		//file.createNewFile();
		
		if (!file.exists()) {
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(escribe);
			bw.close();
		}
		else {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(escribe);
			bw.close();
		}

		System.out.println("Nombre agregado...");
		
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public static void leer(PrintWriter out) {
	 	InputStream fis = null;
		BufferedReader br;
		String line;
		String[] partes;

		try {
			
		
			//para verificar si no hay contactos
			File file = new File("contactos.txt");
			if (!file.exists()) {
				out.println
 				(
 						"<tr>"+
 								"<td>No hay contactos</td>"+
 						"</tr>"	
 				);	
			}
			else 
			{
				fis = new FileInputStream("contactos.txt");
				
				br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
				//imprimir
				while ((line = br.readLine()) != null) {
					partes = line.split(",");
					out.println
	 				(
	 						"<tr>"+
	 								"<td>"+ partes[0] +"</td>"+
	 								"<td>"+ partes[1] +"</td>"+
	 								"<td>"+ partes[2] +"</td>"+
	 						"</tr>"	
	 				);
					
				}
				br.close();
				br = null;
				fis = null;
			}
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	
}




















}//TERMINA LA CLASE

class ServerConn implements Runnable {
private BufferedReader in = null;

public ServerConn(Socket server) throws IOException {
    /* obtain an input stream from the server */
    in = new BufferedReader(new InputStreamReader(
                server.getInputStream()));
}

public void run() {
    String msg;
    try {
        /* loop reading messages from the server and show them 
         * on stdout */
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }
    } catch (IOException e) {
        System.err.println(e);
    }
  }
}
