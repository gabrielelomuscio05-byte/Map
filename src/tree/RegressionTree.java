package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import java.util.TreeSet;

import utility.Keyboard;

public class RegressionTree {

    private Node root;
    private RegressionTree[] childTree;

    private RegressionTree() {
    }

    public RegressionTree(Data trainingSet) {
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1,
                trainingSet.getNumberOfExamples() * 10 / 100);
    }

    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        TreeSet<SplitNode> ts = new TreeSet<SplitNode>();

        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            if (attr instanceof DiscreteAttribute) {
                ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr));
            }
        }

        SplitNode bestSplit = ts.isEmpty() ? null : ts.first();

        if (bestSplit != null) {
            trainingSet.sort(bestSplit.getAttribute(), begin, end);
        }

        return bestSplit;
    }

    private void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
        } else {
            root = determineBestSplitNode(trainingSet, begin, end);

            if (root != null && root.getNumberOfChildren() > 1) {
                childTree = new RegressionTree[root.getNumberOfChildren()];
                for (int i = 0; i < root.getNumberOfChildren(); i++) {
                    childTree[i] = new RegressionTree();
                    childTree[i].learnTree(trainingSet,
                            ((SplitNode) root).getSplitInfo(i).beginIndex,
                            ((SplitNode) root).getSplitInfo(i).endIndex,
                            numberOfExamplesPerLeaf);
                }
            } else {
                root = new LeafNode(trainingSet, begin, end);
            }
        }
    }

    public Double predictClass() throws UnknownValueException {
        if (root instanceof LeafNode)
            return ((LeafNode) root).getPredictedClassValue();
        else {
            int risp;
            System.out.println(((SplitNode) root).formulateQuery());
            risp = Keyboard.readInt();
            if (risp == -1 || risp >= root.getNumberOfChildren())
                throw new UnknownValueException("The answer should be an integer between 0 and "
                        + (root.getNumberOfChildren() - 1) + "!");
            else
                return childTree[risp].predictClass();
        }
    }

    public void printTree() {
        System.out.println("********* TREE **********");
        System.out.println(toString());
        System.out.println("*************************");
    }

    @Override
    public String toString() {
        String tree = root.toString() + "\n";

        if (!(root instanceof LeafNode)) {
            for (int i = 0; i < childTree.length; i++) {
                tree += childTree[i];
            }
        }
        return tree;
    }

    public void printRules() {
        System.out.println("********* RULES **********");
        printRules("");
        System.out.println("*************************");
    }

    private void printRules(String current) {
        if (root instanceof LeafNode) {
            System.out.println(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue());
        } else if (root instanceof SplitNode) {
            SplitNode splitNode = (SplitNode) root;
            for (int i = 0; i < childTree.length; i++) {
                SplitNode.SplitInfo info = splitNode.getSplitInfo(i);
                String currentCondition = splitNode.getAttribute().getName()
                        + info.getComparator() + info.getSplitValue();
                String next = current.isEmpty() ? currentCondition : current + " AND " + currentCondition;
                childTree[i].printRules(next);
            }
        }
    }
}
