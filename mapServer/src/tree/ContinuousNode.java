package tree;

import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;

public class ContinuousNode extends SplitNode {

    private static final long serialVersionUID = 1L;

    public ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex,
            ContinuousAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        double bestInfoVariance = 0;
        List<SplitInfo> bestMapSplit = null;

        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());

            if (value.doubleValue() != currentSplitValue.doubleValue()) {
                double localVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
                double candidateSplitVariance = localVariance;
                localVariance = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
                candidateSplitVariance += localVariance;

                if (bestMapSplit == null) {
                    bestMapSplit = new ArrayList<SplitInfo>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                    bestInfoVariance = candidateSplitVariance;
                } else if (candidateSplitVariance < bestInfoVariance) {
                    bestInfoVariance = candidateSplitVariance;
                    bestMapSplit.set(0,
                            new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.set(1,
                            new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                }

                currentSplitValue = value;
            }
        }

        if (bestMapSplit == null) {
            mapSplit.clear();
            mapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, endExampleIndex, 0, "<="));
            return;
        }

        mapSplit = bestMapSplit;

        // Parte fornita dal docente: elimina uno split non utile quando il secondo
        // sottoinsieme contiene un solo esempio.
        if (mapSplit.size() > 1 && mapSplit.get(1).beginIndex == mapSplit.get(1).getEndIndex()) {
            mapSplit.remove(1);
        }
    }

    @Override
    int testCondition(Object value) {
        if (!(value instanceof Double) || mapSplit.isEmpty())
            return -1;

        double numericValue = ((Double) value).doubleValue();
        double splitValue = ((Double) mapSplit.get(0).getSplitValue()).doubleValue();

        if (numericValue <= splitValue)
            return 0;

        return mapSplit.size() > 1 ? 1 : -1;
    }

    @Override
    public String toString() {
        return "CONTINUOUS " + super.toString();
    }
}
