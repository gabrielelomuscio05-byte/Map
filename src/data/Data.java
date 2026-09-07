package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Data {

    private Object data[][];
    private int numberOfExamples;
    private Attribute explanatorySet[];
    private ContinuousAttribute classAttribute;

    public Data(String fileName) throws TrainingDataException {
        Scanner sc = null;

        try {
            File inFile = new File(fileName);
            sc = new Scanner(inFile);

            if (!sc.hasNextLine())
                throw new TrainingDataException("Missing schema in training set");

            String line = sc.nextLine();
            if (!line.contains("@schema"))
                throw new TrainingDataException("Missing schema in training set");

            String s[] = line.trim().split("\\s+");
            if (s.length < 2)
                throw new TrainingDataException("Invalid schema in training set");

            int numberOfExplanatoryAttributes;
            try {
                numberOfExplanatoryAttributes = Integer.parseInt(s[1]);
            } catch (NumberFormatException e) {
                throw new TrainingDataException("Invalid schema in training set");
            }

            explanatorySet = new Attribute[numberOfExplanatoryAttributes];
            short iAttribute = 0;
            boolean targetFound = false;
            boolean dataFound = false;

            while (sc.hasNextLine()) {
                line = sc.nextLine();

                if (line.contains("@data")) {
                    dataFound = true;
                    break;
                }

                s = line.trim().split("\\s+");
                if (s.length == 0 || s[0].isEmpty())
                    continue;

                if (s[0].equals("@desc")) {
                    if (s.length < 3 || iAttribute >= explanatorySet.length)
                        throw new TrainingDataException("Invalid schema in training set");

                    String discreteValues[] = s[2].split(",");
                    explanatorySet[iAttribute] = new DiscreteAttribute(s[1], iAttribute, discreteValues);
                    iAttribute++;
                } else if (s[0].equals("@target")) {
                    if (s.length < 2)
                        throw new TrainingDataException("Training set without numeric target variable");

                    classAttribute = new ContinuousAttribute(s[1], iAttribute);
                    targetFound = true;
                }
            }

            if (!dataFound)
                throw new TrainingDataException("Missing data section in training set");

            if (!targetFound || classAttribute == null)
                throw new TrainingDataException("Training set without numeric target variable");

            s = line.trim().split("\\s+");
            if (s.length < 2)
                throw new TrainingDataException("Invalid data section in training set");

            try {
                numberOfExamples = Integer.parseInt(s[1]);
            } catch (NumberFormatException e) {
                throw new TrainingDataException("Invalid number of training examples");
            }

            if (numberOfExamples <= 0)
                throw new TrainingDataException("Empty training set");

            data = new Object[numberOfExamples][explanatorySet.length + 1];
            short iRow = 0;

            while (sc.hasNextLine()) {
                line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;

                if (iRow >= numberOfExamples)
                    throw new TrainingDataException("The number of examples is inconsistent with @data");

                s = line.split(",");
                if (s.length != explanatorySet.length + 1)
                    throw new TrainingDataException("Invalid training example at row " + (iRow + 1));

                for (short jColumn = 0; jColumn < s.length - 1; jColumn++)
                    data[iRow][jColumn] = s[jColumn].trim();

                try {
                    data[iRow][s.length - 1] = Double.valueOf(s[s.length - 1].trim());
                } catch (NumberFormatException e) {
                    throw new TrainingDataException("Training set without numeric target variable");
                }

                iRow++;
            }

            if (iRow == 0)
                throw new TrainingDataException("Empty training set");

            if (iRow != numberOfExamples)
                throw new TrainingDataException("The number of examples is inconsistent with @data");

        } catch (FileNotFoundException e) {
            throw new TrainingDataException(e);
        } finally {
            if (sc != null)
                sc.close();
        }
    }

    @Override
    public String toString() {
        String value = "";
        for (int i = 0; i < numberOfExamples; i++) {
            for (int j = 0; j < explanatorySet.length; j++)
                value += data[i][j] + ",";

            value += data[i][explanatorySet.length] + "\n";
        }
        return value;
    }

    public int getNumberOfExamples() {
        return numberOfExamples;
    }

    public int getNumberOfExplanatoryAttributes() {
        return explanatorySet.length;
    }

    public Double getClassValue(int exampleIndex) {
        return (Double) data[exampleIndex][explanatorySet.length];
    }

    public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
        return data[exampleIndex][attributeIndex];
    }

    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet[index];
    }

    public ContinuousAttribute getClassAttribute() {
        return classAttribute;
    }

    public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    private void swap(int i, int j) {
        Object temp;
        for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++) {
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }
    }

    private int partition(DiscreteAttribute attribute, int inf, int sup) {
        int i, j;

        i = inf;
        j = sup;
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

    private void quicksort(Attribute attribute, int inf, int sup) {
        if (sup >= inf) {
            int pos;

            pos = partition((DiscreteAttribute) attribute, inf, sup);

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
