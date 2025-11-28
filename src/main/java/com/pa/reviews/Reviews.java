/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.table.Cell;
import com.pa.table.Column;
import com.pa.table.Header;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author francisco-alejandro
 */
public class Reviews {

    public static final Logger LOGGER = Logger.getLogger("Reviews");

    public static void main(String[] args) throws Exception {
        // Inicializar log
        LOGGER.setUseParentHandlers(false);
        Handler handler = new FileHandler("Reviews.log");
        Formatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);
        LOGGER.addHandler(handler);

        // Procesar el conjunto de datos
        int numFrags = 200 * Runtime.getRuntime().availableProcessors();
        Header dataHeader = new Header(
            new Column[]{
                new Column("Review id", Cell.Type.Int),
                new Column("App id", Cell.Type.Int),
                new Column("Game", Cell.Type.Str),
                new Column("Author id", Cell.Type.Int),
                new Column("Games owned", Cell.Type.Int),
                new Column("Author reviews", Cell.Type.Int),
                new Column("Total playtime", Cell.Type.Int),
                new Column("Playtime last 2 weeks", Cell.Type.Int),
                new Column("Playtime at review", Cell.Type.Int),
                new Column("Last played", Cell.Type.Time),
                new Column("Language", Cell.Type.Str),
                new Column("Content", Cell.Type.Str),
                new Column("Created", Cell.Type.Time),
                new Column("Updated", Cell.Type.Time),
                new Column("Positive", Cell.Type.Bool),
                new Column("Votes up", Cell.Type.Int),
                new Column("Votes funny", Cell.Type.Int),
                new Column("Weighted vote score", Cell.Type.Float),
                new Column("Comments", Cell.Type.Int),
                new Column("Steam purchase", Cell.Type.Bool),
                new Column("Receive for free", Cell.Type.Bool),
                new Column("Written during early access", Cell.Type.Bool),
                new Column("Hidden in Steam China", Cell.Type.Bool),
                new Column("Steam China location", Cell.Type.Str)
            }
        );
        Header colsToTally = dataHeader.subset(new String[] {"Game", "Language"});
        String dataset = "all_reviews.csv";
        String tempDir = "temp";
        String filtered = "filtered.csv";
        String results = "results.txt";
        Program.FilesInfo filesInfo = new Program.FilesInfo(dataset, tempDir, filtered, results);
        int maxUniques = 8;
        int timeColIndex = 12;
        int boolColIndex = 14;
        int numSegments = 1000;
        Program.AnalysisInfo analysisInfo = new Program.AnalysisInfo(dataHeader, colsToTally, maxUniques, timeColIndex, boolColIndex, numSegments);
        Program program = new SequentialProgram(filesInfo, numFrags, analysisInfo);
        program.execute();
    }

}