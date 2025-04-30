package com.ejemplo.sockets;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;

public class Cliente {
    static Socket socket;
    public static void main(String arg[]){
        Scanner scanner=new Scanner(System.in);
        try {
            for(;;){
                System.out.println("__________________________");
                socket=new Socket("127.0.0.1", 1234);
                System.out.println("Escribe el tipo de operacion que deseas realizar");
                System.out.println("1 Catalogo");
                System.out.println("2 Agreagar Carrito Articulo");
                System.out.println("3 Eliminar Carrito Articulo");
                System.out.println("4 Generar tiket");
                System.out.println("5 Generar tiket");
                DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
                System.out.print("Escribe el tipo de operacion: ");
                int type=scanner.nextInt();
                dataOutputStream.writeInt(type);
                if(type==1) getCatalogo();
                else if(type==2 ||  type==3) updateTicket(dataOutputStream, scanner);
                else if(type==4) getTicket();
                else getImagenCatalogo(dataOutputStream, scanner);
                System.out.println("__________________________");
                dataOutputStream.flush();
                dataOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void getCatalogo()  throws Exception{
        ObjectInputStream dataInputStream = new ObjectInputStream(socket.getInputStream());
        System.out.println(dataInputStream.readUTF());
        Vector<Libro> libros=(Vector<Libro>) dataInputStream.readObject();
        System.out.println("__________________________");
        libros.forEach((libro)->System.out.println(libro));
        System.out.println("__________________________");
        dataInputStream.close();
    }
    static void updateTicket(DataOutputStream dataOutputStream, Scanner scanner) throws Exception{
        System.out.print("Escribe el id del libro: ");
        int id=scanner.nextInt();
        dataOutputStream.writeInt(id);
        dataOutputStream.flush();
        ObjectInputStream dataInputStream=new ObjectInputStream(socket.getInputStream());
        System.out.println(dataInputStream.readUTF());
        dataInputStream.close();
    }
    static void getTicket() throws Exception{
        ObjectInputStream dataIntputStream=new ObjectInputStream(socket.getInputStream());
        System.out.println(dataIntputStream.readUTF());
        byte[] fileBytes = (byte[]) dataIntputStream.readObject();
        FileOutputStream fileOutputStream=new FileOutputStream("tiket.pdf");
        fileOutputStream.write(fileBytes);
    }
    static void getImagenCatalogo( DataOutputStream dataOutputStream, Scanner scanner) throws Exception{
        System.out.print("Escribe el id del libro: ");
        int id=scanner.nextInt();
        dataOutputStream.writeInt(id);
        dataOutputStream.flush();
        ObjectInputStream dataInputStream=new ObjectInputStream(socket.getInputStream());
        System.out.println(dataInputStream.readUTF());
        byte img[]=(byte[])dataInputStream.readObject();
        Files.write(Paths.get("imagenes/cliente/"+id+".jpg"), img);        
    }
}
