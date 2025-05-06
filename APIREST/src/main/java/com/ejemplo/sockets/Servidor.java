package com.ejemplo.sockets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class Servidor {
    static ServerSocket ss;
    static ManejadorBd manejadorBd;
    static S3Client s3Client;
    public  static void main(String args[]){
        try{
            Region region=Region.US_EAST_2;
            s3Client=S3Client.builder()
                     .region(region)
                     .credentialsProvider( ProfileCredentialsProvider.create())
                     .build();
            ss = new ServerSocket(1234);
            manejadorBd= new ManejadorBd();
            System.out.println("Esperando conexiones");
            for(;;){
                Socket socket = ss.accept();
                DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
                int operacion=dataInputStream.readInt();
                if(operacion==1)Catalogo(socket);
                else if(operacion==2) addLibro(socket);
                else if(operacion==3)eliminarLibroCarrito(socket);
                else if(operacion==4)generarTicket(socket);
                else enviarImgane(socket);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void Catalogo(Socket socket){
        Vector<Libro> libros = manejadorBd.ConsultarTodos();
        try {
            System.out.println("Imprimeindo catalogo");
            ObjectOutputStream dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            if (libros.size() >= 1) dataOutputStream.writeUTF("Se ejecuto con exito la consulta");
            else dataOutputStream.writeUTF("No se pudo hacer la consulta");
            dataOutputStream.flush();
            dataOutputStream.writeObject(libros);
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void addLibro(Socket socket){
        try {
            System.out.println("Agregando libro al carrito");
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            int id=dataInputStream.readInt();
            int result=manejadorBd.agregarLibroCarrito(id);
            ObjectOutputStream dataOutputStream=new ObjectOutputStream(socket.getOutputStream());
            if(result!=-1) dataOutputStream.writeUTF("Se agrego al carrito");
            else dataOutputStream.writeUTF("Ocurrio un error al agregar posiblemente no hay existencias");
            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  static void eliminarLibroCarrito(Socket socket){
        try {
            System.out.println("Eliminar libro al carrito");
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            int id=dataInputStream.readInt();
            int result=manejadorBd.eliminarLibroCarrito(id);
            ObjectOutputStream dataOutputStream=new ObjectOutputStream(socket.getOutputStream());
            if(result!=-1) dataOutputStream.writeUTF("Se agrego al carrito");
            else dataOutputStream.writeUTF("Ocurrio un error al agregar posiblemente no hay existencias");
            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void generarTicket(Socket socket){
        try {
            System.out.println("Generando ticket");
            Vector<Libro>libros=manejadorBd.getLibrosCompra();
            if(manejadorBd.compra()==1)System.out.print("Se realizo la compra");
            else System.out.println("No se realizo la compra");
            PDDocument pdDocument=new PDDocument();
            PDPage pdPage=new PDPage();
            pdDocument.addPage(pdPage);
            PDPageContentStream pdPageContentStream=new PDPageContentStream(pdDocument, pdPage);
            LocalDateTime localDateTime=LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy HH");
            String date=localDateTime.format(dateTimeFormatter);
            pdPageContentStream.beginText();
            pdPageContentStream.setFont(PDType1Font.HELVETICA, 12);
            pdPageContentStream.newLineAtOffset(25, 700);
            pdPageContentStream.showText("Fecha: "+date);
            
            pdPageContentStream.newLineAtOffset(0, -30);
            pdPageContentStream.showText("Libros comprados:");
            pdPageContentStream.newLineAtOffset(0, -20);                                    
            for(Libro libro:libros){
                pdPageContentStream.showText("Titulo: "+libro.titulo);        
                pdPageContentStream.showText(". Caracteristicas: "+libro.caracteristicas);
                pdPageContentStream.newLineAtOffset(0, -20);        
            }
            pdPageContentStream.endText();
            pdPageContentStream.close();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            pdDocument.save(byteArrayOutputStream);
            pdDocument.close();      
            ObjectOutputStream dataOutputStream=new ObjectOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("Se genero el ticket con exito");
            dataOutputStream.flush();
            dataOutputStream.writeObject(byteArrayOutputStream.toByteArray());
            dataOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void enviarImgane(Socket socket){
        try {
            System.out.println("Se esta enviando el archvio");
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            int id=dataInputStream.readInt();
            GetObjectRequest getObjectRequest=GetObjectRequest.builder()
                            .bucket("imganes-carrito")
                            .key(id+".png")
                            .build();
            ResponseInputStream<GetObjectResponse> s3Object=s3Client.getObject(getObjectRequest);
            byte arr[]=s3Object.readAllBytes();
            ObjectOutputStream objectInputStream=new ObjectOutputStream(socket.getOutputStream());
            objectInputStream.writeUTF("Se envio el archivo correctamente");
            objectInputStream.flush();
            objectInputStream.writeObject(arr);
            objectInputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
