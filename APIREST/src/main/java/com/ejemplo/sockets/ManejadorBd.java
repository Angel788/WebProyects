package com.ejemplo.sockets;
import java.sql.*;
import java.util.Vector;
public class ManejadorBd {
    Connection connection;
    public ManejadorBd(){
        String url = "jdbc:mysql://localhost:3306/librosdb";
        String usuario = "root";
        String contraseña = "mrtetas";
        try{
            connection=DriverManager.getConnection(url, usuario, contraseña);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    Vector<Libro> ConsultarTodos(){
        Vector<Libro> libros=new Vector<>();
        try{
            String query = "SELECT * FROM libros where libros.existencias>=1";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int id=rs.getInt("id");
                int existencias=rs.getInt("existencias");
                String titulo=rs.getString("titulo");
                String caracteristicas=rs.getString("caracteristicas");
                libros.add(new Libro(id, titulo, caracteristicas, existencias));
            }
            return libros;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return libros;
    }
    Libro ConsultarUno(int id){
        Libro libro = null;
        try{
            String query = "SELECT * FROM libros where id="+id;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int existencias=rs.getInt("existencias");
                String titulo=rs.getString("titulo");
                String caracteristicas=rs.getString("caracteristicas");
                libro=new Libro(id, titulo,caracteristicas, existencias);
            }
            return libro;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return libro;
    }
    int agregarLibroCarrito(int id){
        try{
            String query = "SELECT * FROM libros where id="+id;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int existencias = rs.getInt("existencias");
                if (existencias >= 1) {
                    existencias--;
                    String updateQuery = "UPDATE libros SET existencias=" + existencias + " WHERE id=" + id;
                    stmt.executeUpdate(updateQuery);
                    String insertQuery = "INSERT INTO compras (id_libro) VALUES (" + id + ")";
                    stmt.executeUpdate(insertQuery);
                    return 1;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
    int compra(){
        try {
            String query = "DELETE FROM compras";
            Statement stmt = connection.createStatement();
            int cols = stmt.executeUpdate(query);
            if(cols>=1)return 1;
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    int  eliminarLibroCarrito(int id){
        try{
            String query = "DELETE FROM COMPRAS WHERE id_libro="+id;
            Statement stmt = connection.createStatement();
            int cols = stmt.executeUpdate(query);
            if(cols>=1){
                String getLibros="SELECT * FROM libros where id="+id;
                ResultSet rs=stmt.executeQuery(getLibros);
                if(rs.next()){
                    int existencias=rs.getInt("existencias");
                    existencias++;
                    String updateExistencias="UPDATE libros SET existencias=" + existencias + " WHERE id=" + id;
                    stmt.executeUpdate(updateExistencias);
                    return 1;
                }
            }
            return -1;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
    public  Vector<Libro> getLibrosCompra(){
        Vector<Libro> libros=new Vector<>();
        try{
            String query = "SELECT l.id, l.caracteristicas, l.existencias "
            +"FROM compras AS c INNER JOIN libros AS l ON c.id_libro = l.id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int id=rs.getInt("id");
                libros.add(ConsultarUno(id));
            }
            return libros;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return libros;
    }
}
