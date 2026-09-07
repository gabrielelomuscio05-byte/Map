public class RegressionTree {

    Node root;
    RegressionTree[] childTree;

    RegressionTree() {
    }

    public RegressionTree(Data trainingSet) {
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, trainingSet.getNumberOfExamples() * 10 / 100);
    }

    boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        return (end - begin + 1) <= numberOfExamplesPerLeaf;
    }

    SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        SplitNode bestSplit = null;
        double minVariance = Double.MAX_VALUE;

        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            if (attr instanceof DiscreteAttribute) {
                DiscreteNode currentNode = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attr);
                if (bestSplit == null || currentNode.getVariance() < minVariance) {
                    minVariance = currentNode.getVariance();
                    bestSplit = currentNode;
                }
            }
        }

        // Riordina definitivamente il dataset rispetto all'attributo vincente
        if (bestSplit != null) {
            trainingSet.sort(bestSplit.getAttribute(), begin, end);
        }

        return bestSplit;
    }

    void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
        } else {
            root = determineBestSplitNode(trainingSet, begin, end);

            if (root.getNumberOfChildren() > 1) {
                childTree = new RegressionTree[root.getNumberOfChildren()];
                for (int i = 0; i < root.getNumberOfChildren(); i++) {
                    childTree[i] = new RegressionTree();
                    childTree[i].learnTree(trainingSet, ((SplitNode) root).getSplitInfo(i).beginIndex, ((SplitNode) root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
                }
            } else {
                root = new LeafNode(trainingSet, begin, end);
            }
        }
    }

    void printTree() {
        System.out.println("********* TREE **********\n");
        System.out.println(toString());
        System.out.println("*************************\n");
    }

    @Override
    public String toString() {
        String tree = root.toString() + "\n";

        if (root instanceof LeafNode) {
            // Nessun ramo da esplorare
        } else {
            for (int i = 0; i < childTree.length; i++) {
                tree += childTree[i];
            }
        }
        return tree;
    }

    void printRules() {
        System.out.println("********* RULES **********");
        printRules("");
        System.out.println("*************************\n");
    }

    void printRules(String current) {
        if (root instanceof LeafNode) {
            System.out.println(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue());
        } else if (root instanceof SplitNode) {
            SplitNode splitNode = (SplitNode) root;
            for (int i = 0; i < childTree.length; i++) {
                SplitNode.SplitInfo info = splitNode.getSplitInfo(i);
                String currentCondition = splitNode.getAttribute().getName() + info.getComparator() + info.getSplitValue();
                String next = current.isEmpty() ? currentCondition : current + " AND " + currentCondition;
                childTree[i].printRules(next);
            }
        }
    }
}