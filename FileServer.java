package cn_P;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
public class FileServer {
 private static final int PORT = 12345;
 private static Map<String, Long> fileProgressMap = new HashMap<>();
 public static void main(String[] args) {
 try {
 ServerSocket serverSocket = new ServerSocket(PORT);
 System.out.println("Server started. Waiting for clients...");
 while (true) {
 Socket clientSocket = serverSocket.accept();
 System.out.println("Client connected: " + 
clientSocket.getInetAddress());
 Thread clientHandler = new Thread(() -> handleClient(clientSocket));
 clientHandler.start();
 }
 } catch (IOException e) {
 e.printStackTrace();
 }
 }
 private static void handleClient(Socket clientSocket) {
 try (ObjectInputStream ois = new
ObjectInputStream(clientSocket.getInputStream());
 ObjectOutputStream oos = new
ObjectOutputStream(clientSocket.getOutputStream())) {
 String fileName = (String) ois.readObject();
 File file = new File(fileName);
 long offset = ois.readLong();
 if (!file.exists()) {
 oos.writeBoolean(false);
 oos.flush();
 return;
 }
 oos.writeBoolean(true);
 oos.flush();
 try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
 randomAccessFile.seek(offset);
 byte[] buffer = new byte[1024];
 int bytesRead;
 while ((bytesRead = randomAccessFile.read(buffer)) != -1) {
 oos.write(buffer, 0, bytesRead);
 oos.flush();
updateFileProgress(fileName, bytesRead);
 }
 System.out.println("File transfer complete: " + fileName);
 }
 } catch (IOException | ClassNotFoundException e)
 {
 e.printStackTrace();
 }
 }
 private static synchronized void updateFileProgress(String fileName, int bytesRead) 
{
 fileProgressMap.put(fileName, fileProgressMap.getOrDefault(fileName, 0L) + 
bytesRead);
 }
}