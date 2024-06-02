package cn_P;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
public class FileClient {
 private static final String SERVER_IP = "localhost";
 private static final int SERVER_PORT = 12345;
 public static void main(String[] args) {
 Scanner scanner = new Scanner(System.in);
 System.out.print("Enter the file path: ");
 String filePath = scanner.nextLine();
 try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
 File file = new File(filePath);
 oos.writeObject(file.getName());
 oos.flush();
 if (!ois.readBoolean()) {
 System.out.println("File not found on the server.");
 return;
 }
 long offset = 0;
 try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) 
{
 offset = randomAccessFile.length();
 } catch (IOException ignored) {
 }
 oos.writeLong(offset);
 oos.flush();
 try (FileOutputStream fos = new FileOutputStream(file, true)) {
 byte[] buffer = new byte[1024];
 int bytesRead;
 while ((bytesRead = ois.read(buffer)) != -1) {
 fos.write(buffer, 0, bytesRead);
 fos.flush();
 }
 System.out.println("File download complete: " + file.getName());
 }
 } catch (IOException e) {

 e.printStackTrace();
 }
 }
}
