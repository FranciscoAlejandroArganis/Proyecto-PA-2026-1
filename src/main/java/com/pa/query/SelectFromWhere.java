/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.query;

import com.pa.table.Header;
import com.pa.table.Row;
import java.util.function.Predicate;

/**
 *
 * @author francisco-alejandro
 */
public class SelectFromWhere extends SelectFrom {

    protected Predicate<Row> where;

    public SelectFromWhere(Header select, Header from, Predicate<Row> where) {
        super(select, from);
        this.where = where;
    }

    @Override
    public Row apply(Row row) {
        if (!where.test(row)) {
            return null;
        }
        return super.apply(row);
    }

}
