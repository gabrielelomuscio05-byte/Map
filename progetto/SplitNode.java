abstract class SplitNode extends Node {

    class SplitInfo {
        Object splitValue;
        int beginIndex;
        int endIndex;
        int numberChild;
        String comparator = "=";

        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }

        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }

        int getBeginindex() {
            return beginIndex;
        }

        int getBeginIndex() {
            return beginIndex;
        }

        int getEndIndex() {
            return endIndex;
        }

        Object getSplitValue() {
            return splitValue;
        }

        @Override
        public String toString() {
            return "child " + numberChild + " split value" + comparator + splitValue + "[Examples:" + beginIndex + "-" + endIndex + "]";
        }

        String getComparator() {
            return comparator;
        }
    }

    Attribute attribute;
    SplitInfo mapSplit[];
    double splitVariance;

    abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);
    abstract int testCondition(Object value);

    SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

        // Calcolo varianza complessiva dello split
        splitVariance = 0;
        for (int i = 0; i < mapSplit.length; i++) {
            double localVariance = new LeafNode(trainingSet, mapSplit[i].getBeginindex(), mapSplit[i].getEndIndex()).getVariance();
            splitVariance += localVariance;
        }
    }

    Attribute getAttribute() {
        return attribute;
    }

    @Override
    double getVariance() {
        return splitVariance;
    }

    @Override
    int getNumberOfChildren() {
        return mapSplit.length;
    }

    SplitInfo getSplitInfo(int child) {
        return mapSplit[child];
    }

    String formulateQuery() {
        String query = "";
        for (int i = 0; i < mapSplit.length; i++) {
            query += (i + ":" + attribute + mapSplit[i].getComparator() + mapSplit[i].getSplitValue()) + "\n";
        }
        return query;
    }

    @Override
    public String toString() {
        String v = "SPLIT : attribute=" + attribute + " " + super.toString() + " Split Variance: " + getVariance() + "\n";
        for (int i = 0; i < mapSplit.length; i++) {
            v += "\t" + mapSplit[i] + "\n";
        }
        return v;
    }
}