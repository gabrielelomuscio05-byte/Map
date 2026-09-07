package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;

class DiscreteNode extends SplitNode {

    DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        int count = 1;
        Object currentVal = trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Object val = trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!val.equals(currentVal)) {
                count++;
                currentVal = val;
            }
        }

        mapSplit = new SplitInfo[count];

        int child = 0;
        int start = beginExampleIndex;
        currentVal = trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Object val = trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!val.equals(currentVal)) {
                mapSplit[child] = new SplitInfo(currentVal, start, i - 1, child);
                child++;
                start = i;
                currentVal = val;
            }
        }
        mapSplit[child] = new SplitInfo(currentVal, start, endExampleIndex, child);
    }

    @Override
    int testCondition(Object value) {
        for (int i = 0; i < mapSplit.length; i++) {
            if (mapSplit[i].getSplitValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "DISCRETE " + super.toString();
    }
}
