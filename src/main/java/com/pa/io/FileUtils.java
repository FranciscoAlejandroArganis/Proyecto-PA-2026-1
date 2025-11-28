/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Clase para la manipulación de archivos
 * @author francisco-alejandro
 */
public class FileUtils {

    /**
     * Constructor privado para evitar la creación de objetos de la clase
     */
    private FileUtils() {
    }

    /**
     * Cuenta la cantidad de líneas en un archivo
     * @param file es un archivo de texto
     * @return la cantidad de líneas en el archivo
     * @throws IOException
     */
    public static long countLines(File file) throws IOException {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Separa las líneas de un archivo en múltiples fragmentos
     * @param file es un archivo de texto
     * @param dir es el directorio donde se escriben los fragmentos
     * @param totalLines es la cantidad total de líneas del archivo
     * @param fragLines es la cantidad de líneas de cada fragmento
     * @param prefix es el prefijo con el que son nombrados los fragmentos
     * @returns la cantidad de fragmentos escritos
     * @throws IOException
     */
    public static int splitLines(File file, File dir, long totalLines, long fragLines, String prefix) throws IOException {
        File fragment;
        long start = 0;
        long end = fragLines;
        int id = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (end < totalLines) {
                fragment = new File(dir, prefix + id);
                writeFragment(fragment, reader, fragLines);
                start = end;
                end += fragLines;
                id++;
            }
            if (end >= totalLines) {
                fragment = new File(dir, prefix + id);
                writeFragment(fragment, reader, totalLines - start);
                id++;
            }
        }
        return id;
    }

    /**
     * Mezcla las líneas de múltiples fragmentos en un solo archivo
     * @param file es el archivo de texto donde se escriben las líneas de los
     * fragmentos
     * @param dir es el directorio donde se encuentran los fragmentos
     * @param prefix es el prefijo que deben tener los fragmentos para ser
     * leidos
     * @throws IOException
     */
    public static void mergeLines(File file, File dir, String prefix) throws IOException {
        File[] children = dir.listFiles();
        Arrays.sort(children, (a, b) -> a.getName().compareTo(b.getName()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (File child : children) {
                if (child.isFile() && child.getName().startsWith(prefix)) {
                    readFragment(child, writer);
                }
            }
        }
    }

    /**
     * Elimina recursivamente todo el contenido de un directorio o archivo
     * @param node el directorio o archivo a eliminar
     */
    public static void recursiveDelete(File node) {
        if (node.isDirectory()) {
            for (File child : node.listFiles()) {
                recursiveDelete(child);
            }
        }
        node.delete();
    }

    /**
     * Lee las líneas de un fragmento y las escribe en el archivo mezclado
     * @param fragment es el fragmento a leer
     * @param writer es el escritor del archivo mezclado
     * @throws IOException
     */
    private static void readFragment(File fragment, BufferedWriter writer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fragment))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Escribe las líneas de un fragmento, leidas desde el archivo a separar
     * @param fragment es el fragmento donde se escribe
     * @param reader es el lector del archivo a separar
     * @param lines es la cantidad de línea a escribir en el fragmento
     * @throws IOException
     */
    private static void writeFragment(File fragment, BufferedReader reader, long lines) throws IOException {
        fragment.createNewFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fragment))) {
            while (lines > 0) {
                String line = reader.readLine();
                writer.write(line);
                writer.newLine();
                lines--;
            }
        }
    }

}
