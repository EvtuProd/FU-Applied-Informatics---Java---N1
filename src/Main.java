import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class TreeNode {
    private int id;
    private TreeNode parent;
    private List<TreeNode> children;

    public TreeNode(int id) {
        this.id = id;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }
}

class Tree {
    private int treeId; // Идентификатор дерева
    private TreeNode root;

    public Tree(int treeId, TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Корневой узел не должен быть нулевым.");
        }
        this.treeId = treeId;
        this.root = root;
    }

    public TreeNode getRoot() {
        return root;
    }

    public int getTreeId() {
        return treeId;
    }
}


public class Main {
    private static int treeIdCounter = 1;

    private static int generateTreeId() {
        return treeIdCounter++;
    }

    public static void main(String[] args) {
        List<Tree> trees = readTreesFromFile("input.csv");
        int totalTrees = trees.size();
        int totalLeaves = countLeavesInAllTrees(trees);
        int maxBranchLength = getMaxBranchLength(trees);
        int maxBranchId = getMaxBranchId(trees);

        // Вывод пути самой длинной ветки
        System.out.println("Самая длинная ветка: " + getLongestBranchPath(trees));

        // Запись результатов в файл result.txt
        writeResultToFile(totalTrees, totalLeaves, maxBranchId, maxBranchLength);
    }

    public static List<Tree> readTreesFromFile(String filename) {
        List<Tree> trees = new ArrayList<>();
        Map<Integer, TreeNode> nodesMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int nodeId = Integer.parseInt(parts[0]);
                int parentId = Integer.parseInt(parts[1]);

                TreeNode node = nodesMap.getOrDefault(nodeId, new TreeNode(nodeId));
                TreeNode parent = nodesMap.getOrDefault(parentId, new TreeNode(parentId));

                if (node.getParent() != null) {
                    // Если узел уже имеет родителя, это может быть ошибка в данных
                    throw new IllegalArgumentException("Узел " + nodeId + " имеет несколько родителей.");
                }

                node.setParent(parent);
                parent.getChildren().add(node);

                nodesMap.put(nodeId, node);
                nodesMap.put(parentId, parent);
            }

            // Поиск корневых узлов и создание деревьев
            for (TreeNode node : nodesMap.values()) {
                if (node.isRoot()) {
                    // Генерация идентификатора для дерева
                    int treeId = generateTreeId(); // Ваш способ генерации id дерева
                    trees.add(new Tree(treeId, node)); // Передача идентификатора и корня дерева
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return trees;
    }

    public static int countLeavesInAllTrees(List<Tree> trees) {
        int totalLeaves = 0;
        for (Tree tree : trees) {
            totalLeaves += countLeaves(tree.getRoot());
        }
        return totalLeaves;
    }

    private static int countLeaves(TreeNode node) {
        if (node.isLeaf()) {
            return 1;
        }
        int count = 0;
        for (TreeNode child : node.getChildren()) {
            count += countLeaves(child);
        }
        return count;
    }

    public static int getMaxBranchLength(List<Tree> trees) {
        int maxBranchLength = 0;
        for (Tree tree : trees) {
            int branchLength = calculateMaxBranchLength(tree.getRoot());
            maxBranchLength = Math.max(maxBranchLength, branchLength);
        }
        return maxBranchLength;
    }

    private static int calculateMaxBranchLength(TreeNode node) {
        System.out.println("Calculating branch length for node " + node.getId());
        if (node.isLeaf()) {
            System.out.println("Node " + node.getId() + " is a leaf, length is 1");
            return 1;
        }
        int maxChildBranchLength = 0;
        for (TreeNode child : node.getChildren()) {
            int childBranchLength = calculateMaxBranchLength(child);
            System.out.println("Child branch length for node " + child.getId() + ": " + childBranchLength);
            maxChildBranchLength = Math.max(maxChildBranchLength, childBranchLength);
        }
        System.out.println("Max branch length for node " + node.getId() + ": " + (maxChildBranchLength + 1));
        return maxChildBranchLength + 1;
    }

    public static int getMaxBranchId(List<Tree> trees) {
        int maxBranchId = 0;
        int maxBranchLength = 0;

        for (Tree tree : trees) {
            TreeNode root = tree.getRoot();
            int branchLength = calculateMaxBranchLength(root);
            if (branchLength > maxBranchLength) {
                maxBranchId = tree.getTreeId();
                maxBranchLength = branchLength;
            }
        }

        return maxBranchId;
    }

    private static TreeNode getLeafNode(TreeNode node) {
        if (node.isLeaf()) {
            return node;
        }
        TreeNode maxChildNode = node.getChildren().get(0);
        int maxChildBranchLength = calculateMaxBranchLength(maxChildNode);
        for (TreeNode child : node.getChildren()) {
            int childBranchLength = calculateMaxBranchLength(child);
            if (childBranchLength > maxChildBranchLength) {
                maxChildNode = child;
                maxChildBranchLength = childBranchLength;
            }
        }
        return getLeafNode(maxChildNode);
    }


    public static String getLongestBranchPath(List<Tree> trees) {
        StringBuilder longestBranchPath = new StringBuilder();
        int maxLength = 0;

        for (Tree tree : trees) {
            List<String> paths = new ArrayList<>();
            getPath(tree.getRoot(), "", paths);
            for (String path : paths) {
                if (path.length() > longestBranchPath.length()) {
                    longestBranchPath = new StringBuilder(path);
                }
            }
        }

        return longestBranchPath.toString();
    }

    private static void getPath(TreeNode node, String currentPath, List<String> paths) {
        if (node.isLeaf()) {
            currentPath += node.getId();
            paths.add(currentPath);
            return;
        }
        currentPath += node.getId() + "->";
        for (TreeNode child : node.getChildren()) {
            getPath(child, currentPath, paths);
        }
    }

    public static void writeResultToFile(int totalTrees, int totalLeaves, int maxBranchId, int maxBranchLength) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
            writer.write(totalTrees + " " + totalLeaves + " "+ maxBranchId + " " + (maxBranchLength - 2));
            System.out.println(totalTrees + " " + totalLeaves + " "+ maxBranchId + " " + (maxBranchLength - 2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
