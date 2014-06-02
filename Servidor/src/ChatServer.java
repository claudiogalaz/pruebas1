/* ChatServer.java */
import java.net.ServerSocket;
import java.net.Socket;
 
import java.nio.charset.Charset;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
 
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
 
public class ChatServer {
    private static int port = 8080; /* port to listen on */
 
    public static void main (String[] args) throws IOException {
 
        ServerSocket server = null;
        try {
            server = new ServerSocket(port); /* start listening on the port */
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }
 
        Socket client = null;
        while(true) {
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            /* start a new thread to handle this client */
            Thread t = new Thread(new ClientConn(client));
            t.start();
        }
      
    }
}
 
class ChatServerProtocol {
    private String nick;
    private ClientConn conn;
 
    /* a hash table from user nicks to the corresponding connections */
    private static Hashtable<String, ClientConn> nicks = 
        new Hashtable<String, ClientConn>();
 
    private static final String msg_OK = "OK";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "INVALID COMMAND";
    private static final String msg_SEND_FAILED = "FAILED TO SEND";
 
    /**
     * Adds a nick to the hash table 
     * returns false if the nick is already in the table, true otherwise
     */
    private static boolean add_nick(String nick, ClientConn c) {
        if (nicks.containsKey(nick)) {
            return false;
        } else {
            nicks.put(nick, c);
            return true;
        }
    }
 
    public ChatServerProtocol(ClientConn c) {
        nick = null;
        conn = c;
    }
 
    private void log(String msg) {
        System.err.println(msg);
    }
 
    public boolean isAuthenticated() {
        return ! (nick == null);
    }
 
    /**
     * Implements the authentication protocol.
     * This consists of checking that the message starts with the NICK command
     * and that the nick following it is not already in use.
     * returns: 
     *  msg_OK if authenticated
     *  msg_NICK_IN_USE if the specified nick is already in use
     *  msg_SPECIFY_NICK if the message does not start with the NICK command 
     */
    private String authenticate(String msg) {
        if(msg.startsWith("NICK")) {
            String tryNick = msg.substring(5);
            if(add_nick(tryNick, this.conn)) {
                log("Nick " + tryNick + " joined.");
                this.nick = tryNick;
                return msg_OK;
            } else {
                return msg_NICK_IN_USE;
            }
        } else {
            return msg_SPECIFY_NICK;
        }
    }
 
    /**
     * Send a message to another user.
     * @recepient contains the recepient's nick
     * @msg contains the message to send
     * return true if the nick is registered in the hash, false otherwise
     */
    private boolean sendMsg(String recipient, String msg) {
        if (nicks.containsKey(recipient)) {
            ClientConn c = nicks.get(recipient);
            c.sendMsg(nick + ": " + msg);
            return true;
        } else {
            return false;
        }
    }
 
    /**
     * Process a message coming from the client
     */
    public String process(String msg) {
        if (!isAuthenticated()) 
            return authenticate(msg);
 
        String[] msg_parts = msg.split(" ", 3);
        String msg_type = msg_parts[0];
 
        if(msg_type.equals("MSG")) {
            if(msg_parts.length < 3) return msg_INVALID;
            if(sendMsg(msg_parts[1], msg_parts[2])) return msg_OK;
            else return msg_SEND_FAILED;
        } else {
            return msg_INVALID;
        }
    }
}
 
class ClientConn implements Runnable {
    private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
 
    ClientConn(Socket client) {
        this.client = client;
        try {
            /* obtain an input stream to this client ... */
            in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
            /* ... and an output stream to the same client */
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
    }
 
    public void run() {
        String msg, response;
        ChatServerProtocol protocol = new ChatServerProtocol(this);
        try {
            /* loop reading lines from the client which are processed 
             * according to our protocol and the resulting response is 
             * sent back to the client */
            while ((msg = in.readLine()) != null) {
                response = protocol.process(msg);
                //System.out.println("Cliente puso: " + msg + "\n");
               
                StringTokenizer st = new StringTokenizer(msg);
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
     			
     			if (uri.equals("/"))
     			{
     				home(out);
     				System.out.println("agsdglafbladbg funcionó creo");
     			}
     			else if (uri.equals("/ingresar"))
     			{
     				InputStream archivo = new FileInputStream ("form.html");
     	 			String form = IOUtils.toString(archivo, "UTF-8");
     				out.println(form);
     			}
     			if (uri.equals("/chat"))
     			{
     				chatear(out);
     			}
     			
                out.println("SERVER: " + response);
            }
        } catch (IOException e) {
            System.err.println(e);
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
 
    private void chatear(PrintWriter out) {
    	out.println("HTTP/1.1 200 OK");
		out.println("Content-Type: text/html\n");
		out.println
		(
				"<html>"+
						"<head >"+
							"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>"+
							"<title>Chat</title>"+
						"</head>"+
				"<body>"+
				"<center>"+
				"<h1>Chat</h1>"+
				"<table style = \"width:300px\" border= \"1\" align= \"center\" "+
					"<tr>"+
						"<td>probandosagsdba oadsbglsd</td>"+
					"</tr>"						
		);
		//leer(out);
		out.println
		(
				"</table>"+
				"<form action= '/'>"+
			    "<input type='submit' value='Home'>"+
			    "</form>"+
				"</center>"+
				"</body>"+
				"</html>"
		);		
		
	}

	private void decodeParms(String parms, Properties p) throws InterruptedException {
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
		

	private String decodePercent(String str) {
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
	
	public void home(PrintWriter out)
	{
		out.println("HTTP/1.1 200 OK");
			out.println("Content-Type: text/html\n");
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
				    "<form action= '/chat'>"+
				    "<input type='submit' value='Ingresar al Chat'>"+
				    "</form>"+
					"</center>"+
					"</body>"+
					"</html>"
			);		
	}
	private void leer(PrintWriter out) {
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

	public void sendMsg(String msg) {
        out.println(msg);
    }
}