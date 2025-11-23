/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.table.Cell;
import com.pa.table.Column;
import com.pa.table.Header;

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

    public static void main(String[] args) {
    }

}
