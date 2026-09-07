package tree;

import data.Data;

abstract class Node {

    private static int idNodeCount = 0;
    private int idNode;
    private int beginExampleIndex;
    private int endExampleIndex;
    private double variance;

    Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        this.idNode = idNodeCount++;
        this.beginExampleIndex = beginExampleIndex;
        this.endExampleIndex = endExampleIndex;

        double sum = 0.0;
        int n = endExampleIndex - beginExampleIndex + 1;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            sum += trainingSet.getClassValue(i);
        }
        double mean = sum / n;

        double sse = 0.0;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            double diff = trainingSet.getClassValue(i) - mean;
            sse += diff * diff;
        }
        this.variance = sse;
    }

    int getIdNode() {
        return idNode;
    }

    int getBeginExampleIndex() {
        return beginExampleIndex;
    }

    int getEndExampleIndex() {
        return endExampleIndex;
    }

    double getVariance() {
        return variance;
    }

    abstract int getNumberOfChildren();

    @Override
    public String toString() {
        return "Nodo: [Examples:" + beginExampleIndex + "-" + endExampleIndex + "] variance:" + variance;
    }
}
