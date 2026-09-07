public class LeafNode extends Node {

    private Double predictedClassValue;

    LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        double sum = 0.0;
        int n = endExampleIndex - beginExampleIndex + 1;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            sum += trainingSet.getClassValue(i);
        }
        this.predictedClassValue = sum / n;
    }

    Double getPredictedClassValue() {
        return predictedClassValue;
    }

    @Override
    int getNumberOfChildren() {
        return 0;
    }

    @Override
    public String toString() {
        return "LEAF class=" + predictedClassValue + " " + super.toString();
    }
}