import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	static ArrayList<CustomFile> tiedostot = new ArrayList<>();

	static HashMap<SocketAddress, Integer> BlackList = new HashMap<SocketAddress, Integer>();

	private static byte[] Passwordi;

	protected static File config = new File("src/cfg.config");

	private static String PORT;
	private static String FilePath;
	
	public static class HashUtil {

		public static byte[] getSHA256Hash(String input) throws NoSuchAlgorithmException {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(input.getBytes());
			byte[] hash = md.digest();
			return hash;
		}
	}
	public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException {

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("--pass")) {
					Passwordi = HashUtil.getSHA256Hash(args[i + 1]);
				}
				if (args[i].equalsIgnoreCase("--PORT")) {
					PORT = args[i + 1];
				}
				if (args[i].equalsIgnoreCase("--FILE")) {
					FilePath = args[i + 1];
				}
			}
		}

		try {
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(config);
			while (sc.hasNextLine()) {
				String[] argus = sc.nextLine().split(" ");
				if (argus.length == 2) {

					if (argus[0].equals("--pass")) {

						Passwordi = HashUtil.getSHA256Hash(argus[1]);
						
					}
					if (argus[0].equals("--path")) {
						FilePath = argus[1];
					}
					if (argus[0].equals("--port")) {
						PORT = argus[1];
					}				}
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Please add arguments to cfg.config");
		}

		System.out.println("Loaded Config File with data:\n");

		System.out.println("PORT: " + PORT);
		System.out.println("FilePath: " + FilePath);

		System.out.println("File Server online");
		boolean käynnissä = true;
		boolean Kirjautunut = true;

		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(Integer.parseInt(PORT));

			while (käynnissä) {

				Socket socket = serverSocket.accept();
				SocketAddress ip = socket.getRemoteSocketAddress();

				if (BlackList.containsKey(ip) && BlackList.get(ip) > 2) {
					System.out.println(ip.toString() + ": Blacklisted user is trying to connect...");
					socket.close();
				}

				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				int securityLenght = dataInputStream.readInt();

				if (securityLenght == 32) {
					byte[] SecurityBytes = Passwordi;
					byte[] tryBytes = new byte[32];
					dataInputStream.readFully(tryBytes, 0, securityLenght);
					for (int i = 0; i < securityLenght; i++) {
						if (SecurityBytes[i] == tryBytes[i]) {
							Kirjautunut &= true;
						} else {
							Kirjautunut = false;
							System.out.println(ip.toString() + ": Tried to login with wrong password!");
							if (!BlackList.containsKey(ip)) {
								BlackList.put(ip, 1);
							} else if (BlackList.containsKey(ip)) {
								int valu = BlackList.get(ip);
								valu++;
								BlackList.put(ip, valu);
							}

							break;
						}
					}
				}

				int fileNameLength = dataInputStream.readInt();

				if (fileNameLength > 0 && Kirjautunut) {

					System.out.println(ip.toString() + ": Connected with password.");
					byte[] fileNameBytes = new byte[fileNameLength];
					dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);

					String fileName = new String(fileNameBytes);
					System.out
							.println(socket.getRemoteSocketAddress().toString() + ": Sending File: " + fileName);
					int fileContentLength = dataInputStream.readInt();

					if (fileContentLength > 0) {
						byte[] fileContentBytes = new byte[fileContentLength];

						dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

						if (getFileExtension(fileName).equalsIgnoreCase("gif")) {

							File fileDownload = new File(FilePath + fileName);

							try {
								FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);

								fileOutputStream.write(fileContentBytes);
								fileOutputStream.close();

								System.out.println(
										socket.getRemoteSocketAddress().toString() + ": " + fileName + " Received.");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

			}

			serverSocket.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static String getFileExtension(String fileName) {

		int i = fileName.lastIndexOf('.');

		if (i > 0)
			return fileName.substring(i + 1);
		else
			return "Nyt meni tiedosto vituiks";
	}

}
