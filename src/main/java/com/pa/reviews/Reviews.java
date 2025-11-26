/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.query.SelectFromWhere;
import com.pa.table.Cell;
import com.pa.table.Column;
import com.pa.table.Header;
import java.io.File;
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

    public static final Logger LOGGER = Logger.getLogger("ReviewsLogger");

    public static final Header HEADER = new Header(
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

    public static final String[] NUMERIC_VARS = {
        "Games owned",
        "Author reviews",
        "Total playtime",
        "Playtime last 2 weeks",
        "Playtime at review",
        "Last played",
        "Created",
        "Updated",
        "Votes up",
        "Votes funny",
        "Weighted vote score",
        "Comments"
    };

    public static void main(String[] args) throws Exception {
        // Inicializar log
        LOGGER.setUseParentHandlers(false);
        Handler handler = new FileHandler("Reviews.log");
        Formatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);
        LOGGER.addHandler(handler);
        
        // Procesar los datos
        QueryReader qr = new QueryReader();
        SelectFromWhere query = qr.readQuery();
        Manager manager = new Manager(1024, new File("reviews.csv"), new File("temp"), new File("out.csv"), new File("stats.txt"), query);
        manager.partition();
        manager.manageWorkers();
    }

}
