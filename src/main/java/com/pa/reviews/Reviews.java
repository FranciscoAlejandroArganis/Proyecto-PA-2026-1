/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.csv.CSVParser;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.table.Cell;
import com.pa.table.Column;
import com.pa.table.Header;
import com.pa.table.Row;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneOffset;

/**
 *
 * @author francisco-alejandro
 */
public class Reviews {

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

    public static void main(String[] args) throws IOException {
        Accumulator acc = new Accumulator(12);
        CSVParser parser = new CSVParser();
        Row row = new Row(HEADER);
        QueryReader qr = new QueryReader();
        SelectFromWhere query = qr.readQuery();
        double[] values = new double[12];
        try (
                BufferedReader reader = new BufferedReader(new FileReader("reviews.csv")); BufferedWriter writer = new BufferedWriter(new FileWriter("out.csv"))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                try {
                    row.fill(parser.parseLine(line));
                    values[0] = row.getCell(4).getInt();
                    values[1] = row.getCell(5).getInt();
                    values[2] = row.getCell(6).getInt();
                    values[3] = row.getCell(7).getInt();
                    values[4] = row.getCell(8).getInt();
                    values[5] = row.getCell(9).getTime().toEpochSecond(ZoneOffset.UTC);
                    values[6] = row.getCell(12).getTime().toEpochSecond(ZoneOffset.UTC);
                    values[7] = row.getCell(13).getTime().toEpochSecond(ZoneOffset.UTC);
                    values[8] = row.getCell(15).getInt();
                    values[9] = row.getCell(16).getInt();
                    values[10] = row.getCell(17).getFloat();
                    values[11] = row.getCell(18).getInt();
                    acc.add(values);
                    Row result = query.apply(row);
                    if (row != null) {
                        writer.write(row.toString());
                        writer.newLine();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

}
