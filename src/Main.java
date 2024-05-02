import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
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
    private TreeNode root;

    public Tree(TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Корневой узел не должен быть нулевым.");
        }
        this.root = root;
    }

    public TreeNode getRoot() {
        return root;
    }
}

public class Main {
    public static void main(String[] args) {
        List<Tree> trees = readTreesFromFile("input.csv");
        int totalTrees = trees.size();
        int totalLeaves = countLeavesInAllTrees(trees);
        int maxBranchLength = getMaxBranchLength(trees);
        int treesWithMaxBranch = countTreesWithMaxBranch(trees, maxBranchLength);

        // Запись результатов в файл result.txt
        writeResultToFile(totalTrees, totalLeaves, maxBranchLength, treesWithMaxBranch);
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
                    trees.add(new Tree(node));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return trees;
    }
    public static void writeResultToFile(int totalTrees, int totalLeaves, int maxBranchLength, int treesWithMaxBranch) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
            writer.write(totalTrees + " " + totalLeaves + " ");
            if (treesWithMaxBranch > 1 || maxBranchLength == 0) {
                writer.write("0 0");
            } else {
                writer.write("1 " + maxBranchLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
