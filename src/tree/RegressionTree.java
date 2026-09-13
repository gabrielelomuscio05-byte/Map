package tree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import utility.Keyboard;

public class RegressionTree implements Serializable {

    private static final long serialVersionUID = 1L;

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
            Attribute attribute = trainingSet.getExplanatoryAttribute(i);
            SplitNode currentNode = null;

            if (attribute instanceof DiscreteAttribute) {
                currentNode = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attribute);
            } else if (attribute instanceof ContinuousAttribute) {
                currentNode = new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) attribute);
            }

            if (currentNode != null)
                ts.add(currentNode);
        }

        SplitNode bestSplit = ts.isEmpty() ? null : ts.first();

        if (bestSplit != null)
            trainingSet.sort(bestSplit.getAttribute(), begin, end);

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

        int risp;
        System.out.println(((SplitNode) root).formulateQuery());
        risp = Keyboard.readInt();

        if (risp < 0 || risp >= root.getNumberOfChildren())
    throw new UnknownValueException(
        "The answer should be an integer between 0 and "
        + (root.getNumberOfChildren() - 1) + "!"
    );
        return childTree[risp].predictClass();
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
            for (int i = 0; i < childTree.length; i++)
                tree += childTree[i];
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

    public void salva(String nomeFile) throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
            out.writeObject(this);
        }
    }

    public static RegressionTree carica(String nomeFile)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeFile))) {
            return (RegressionTree) in.readObject();
        }
    }
}
