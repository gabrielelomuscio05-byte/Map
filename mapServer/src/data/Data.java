package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.Column;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.Example;
import database.TableData;
import database.TableSchema;

public class Data {

    private List<Example> data = new ArrayList<Example>();
    private List<Attribute> explanatorySet = new LinkedList<Attribute>();
    private ContinuousAttribute classAttribute;

    public Data(String tableName) throws TrainingDataException {
        DbAccess db = new DbAccess();
        try {
            db.initConnection();
        } catch (DatabaseConnectionException e) {
            throw new TrainingDataException("Connessione al database fallita: " + e.getMessage());
        }

        try {
            TableSchema tSchema = new TableSchema(db, tableName);
            int numberOfAttributes = tSchema.getNumberOfAttributes();

            if (numberOfAttributes == 0) {
                throw new TrainingDataException("La tabella '" + tableName + "' non esiste nel database o non ha colonne.");
            }

            if (numberOfAttributes < 2) {
                throw new TrainingDataException("La tabella '" + tableName + "' ha meno di due colonne.");
            }

            Column targetCol = tSchema.getColumn(numberOfAttributes - 1);
            if (!targetCol.isNumber()) {
                throw new TrainingDataException("L'attributo corrispondente all'ultima colonna della tabella ("
                        + targetCol.getColumnName() + ") non è numerico.");
            }

            TableData tData = new TableData(db);

            // Popolamento explanatorySet
            for (int i = 0; i < numberOfAttributes - 1; i++) {
                Column c = tSchema.getColumn(i);
                if (c.isNumber()) {
                    explanatorySet.add(new ContinuousAttribute(c.getColumnName(), i));
                } else {
                    Set<Object> distinctValues = tData.getDistinctColumnValues(tableName, c);
                    Set<String> stringValues = new TreeSet<String>();
                    for (Object val : distinctValues) {
                        stringValues.add(val.toString());
                    }
                    explanatorySet.add(new DiscreteAttribute(c.getColumnName(), i, stringValues));
                }
            }

            // Popolamento classAttribute
            classAttribute = new ContinuousAttribute(targetCol.getColumnName(), numberOfAttributes - 1);

            // Caricamento transazioni
            try {
                data = tData.getTransazioni(tableName);
            } catch (EmptySetException e) {
                throw new TrainingDataException("La tabella '" + tableName + "' ha zero tuple.");
            }

            if (data == null || data.isEmpty()) {
                throw new TrainingDataException("La tabella '" + tableName + "' ha zero tuple.");
            }

        } catch (SQLException e) {
            throw new TrainingDataException("Errore SQL durante il caricamento della tabella '" + tableName + "': " + e.getMessage());
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public String toString() {
        String value = "";
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < explanatorySet.size(); j++)
                value += data.get(i).get(j) + ",";

            value += data.get(i).get(explanatorySet.size()) + "\n";
        }
        return value;
    }

    public int getNumberOfExamples() {
        return data.size();
    }

    public int getNumberOfExplanatoryAttributes() {
        return explanatorySet.size();
    }

    public Double getClassValue(int exampleIndex) {
        return (Double) data.get(exampleIndex).get(explanatorySet.size());
    }

    public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
        return data.get(exampleIndex).get(attributeIndex);
    }

    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet.get(index);
    }

    public ContinuousAttribute getClassAttribute() {
        return classAttribute;
    }

    public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    private void swap(int i, int j) {
        Collections.swap(data, i, j);
    }

    private int partition(DiscreteAttribute attribute, int inf, int sup) {
        int i = inf;
        int j = sup;
        int med = (inf + sup) / 2;
        String x = (String) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (true) {
            while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
                i++;
            }

            while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }

            if (i < j) {
                swap(i, j);
            } else
                break;
        }
        swap(inf, j);
        return j;
    }

    private int partition(ContinuousAttribute attribute, int inf, int sup) {
        int i = inf;
        int j = sup;
        int med = (inf + sup) / 2;
        Double x = (Double) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (true) {
            while (i <= sup && ((Double) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
                i++;
            }

            while (((Double) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
                j--;
            }

            if (i < j) {
                swap(i, j);
            } else
                break;
        }
        swap(inf, j);
        return j;
    }

    private void quicksort(Attribute attribute, int inf, int sup) {
        if (sup >= inf) {
            int pos;

            if (attribute instanceof DiscreteAttribute)
                pos = partition((DiscreteAttribute) attribute, inf, sup);
            else
                pos = partition((ContinuousAttribute) attribute, inf, sup);

            if ((pos - inf) < (sup - pos + 1)) {
                quicksort(attribute, inf, pos - 1);
                quicksort(attribute, pos + 1, sup);
            } else {
                quicksort(attribute, pos + 1, sup);
                quicksort(attribute, inf, pos - 1);
            }
        }
    }
}
