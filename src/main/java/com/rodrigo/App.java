package com.rodrigo;

import com.rodrigo.model.User;
import com.sleepycat.je.*;

import java.io.*;

public class App {

    public static void main(String[] args) {
        // Ruta donde se guardará la base de datos
        String dbPath = "./berkeleydb";

        // Crear el entorno de la base de datos
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);

        Environment dbEnv = null;
        Database db = null;

        try {
            // Abrir o crear el entorno de la base de datos
            dbEnv = new Environment(new File(dbPath), envConfig);

            // Configuración de la base de datos
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);

            // Abrir o crear la base de datos
            db = dbEnv.openDatabase(null, "userDatabase", dbConfig);

            // Crear un objeto User
            User user = new User(1, "Juan Pérez", "juan@example.com");

            // Serializar el objeto User
            DatabaseEntry key = new DatabaseEntry(String.valueOf(user.getId()).getBytes());
            DatabaseEntry data = new DatabaseEntry(serialize(user));

            // Insertar el objeto User en la base de datos
            db.put(null, key, data);
            System.out.println("Usuario insertado: " + user);

            // Recuperar el objeto User de la base de datos
            DatabaseEntry foundData = new DatabaseEntry();
            db.get(null, key, foundData, LockMode.DEFAULT);

            // Deserializar el objeto User
            User foundUser = (User) deserialize(foundData.getData());
            System.out.println("Usuario recuperado: " + foundUser);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar la base de datos y el entorno
            if (db != null) {
                db.close();
            }
            if (dbEnv != null) {
                dbEnv.close();
            }
        }
    }

    // Método para serializar el objeto
    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        }
    }

    // Método para deserializar el objeto
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
}

