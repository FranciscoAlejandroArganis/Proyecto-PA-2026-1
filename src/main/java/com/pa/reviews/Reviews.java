/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.table.Cell;
import com.pa.table.Column;
import com.pa.table.Header;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Applicación para el procesamiento de reseñas de Steam
 * @author francisco-alejandro
 */
public class Reviews {
    
    /**
     * Logger usado durante la ejecución de la apliación
     */
    public static final Logger LOGGER = Logger.getLogger("Reviews");

    /**
     * Inicia la ejecución de la aplicación
     * @param args son los argumentos de la línea de comandos
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // Inicializar log
        LOGGER.setUseParentHandlers(false);
        Handler handler = new FileHandler("Reviews.log");
        Formatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);
        LOGGER.addHandler(handler);

        // Procesar el conjunto de datos
        Header dataHeader = new Header(
            new Column[]{
                new Column("Review id", Cell.Type.INT),
                new Column("App id", Cell.Type.INT),
                new Column("Game", Cell.Type.STR),
                new Column("Author id", Cell.Type.INT),
                new Column("Games owned", Cell.Type.INT),
                new Column("Author reviews", Cell.Type.INT),
                new Column("Total playtime", Cell.Type.INT),
                new Column("Playtime last 2 weeks", Cell.Type.INT),
                new Column("Playtime at review", Cell.Type.INT),
                new Column("Last played", Cell.Type.TIME),
                new Column("Language", Cell.Type.STR),
                new Column("Content", Cell.Type.STR),
                new Column("Created", Cell.Type.TIME),
                new Column("Updated", Cell.Type.TIME),
                new Column("Positive", Cell.Type.BOOL),
                new Column("Votes up", Cell.Type.INT),
                new Column("Votes funny", Cell.Type.INT),
                new Column("Weighted vote score", Cell.Type.FLOAT),
                new Column("Comments", Cell.Type.INT),
                new Column("Steam purchase", Cell.Type.BOOL),
                new Column("Receive for free", Cell.Type.BOOL),
                new Column("Written during early access", Cell.Type.BOOL),
                new Column("Hidden in Steam China", Cell.Type.BOOL),
                new Column("Steam China location", Cell.Type.STR)
            }
        );
        Header groupColumns = dataHeader.subset(new String[] {"Game", "Language"});
        String dataset = "all_reviews.csv";
        String tempDir = "temp";
        String filtered = "filtered.csv";
        String results = "results.txt";
        Program.FilesInfo filesInfo = new Program.FilesInfo(dataset, tempDir, filtered, results);
        int maxUniques = 16;
        int timeColIndex = 12;
        int boolColIndex = 14;
        int numSegments = 100;
        int numCores = Runtime.getRuntime().availableProcessors();
        int numFrags = 200 * numCores;
        System.out.println("Unidades de procesamiento detectadas: " + numCores);
        System.out.println("Número de fragmentos: " + numFrags);
        Program.AnalysisInfo analysisInfo = new Program.AnalysisInfo(dataHeader, groupColumns, maxUniques, timeColIndex, boolColIndex, numSegments);
        Program program = new ConcurrentProgram(filesInfo, numFrags, analysisInfo);
        program.execute();
    }

}