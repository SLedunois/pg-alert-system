package fr.sledunois.pgas.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class PgResult {

    private PgResult() {
        throw new IllegalStateException("Utility class");
    }

    private static List<JsonObject> jsonResult(RowSet<Row> rows) {
        List<JsonObject> values = new ArrayList<>();
        for (Row row : rows) {
            values.add(transformToJsonObject(row, rows.columnsNames()));
        }

        return values;
    }

    public static JsonObject rowSetToJsonObject(RowSet<Row> rows) {
        List<JsonObject> values = jsonResult(rows);
        return !values.isEmpty() ? values.get(0) : null;
    }

    public static List<JsonObject> rowSetToList(RowSet<Row> rows) {
        return jsonResult(rows);
    }

    private static JsonObject transformToJsonObject(Row row, List<String> columNames) {
        JsonObject tuple = new JsonObject();
        for (int i = 0; i < columNames.size(); i++) {
            Object value = row.getValue(i);
            if (value instanceof LocalDateTime)
                value = row.getValue(i).toString();
            tuple.put(columNames.get(i), value);
        }

        return tuple;
    }

}
