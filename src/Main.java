import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Deque;
import java.util.ArrayDeque;

// Класс, представляющий узел дерева
class TreeNode {
    private int id; // Идентификатор узла
    private TreeNode parent; // Родительский узел
    private List<TreeNode> children; // Список дочерних узлов

    // Конструктор узла
    public TreeNode(int id) {
        this.id = id;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    // Геттер для идентификатора узла
    public int getId() {
        return id;
    }

    // Геттер для родительского узла
    public TreeNode getParent() {
        return parent;
    }

    // Сеттер для родительского узла
    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    // Геттер для списка дочерних узлов
    public List<TreeNode> getChildren() {
        return children;
    }

    // Проверка, является ли узел листом
    public boolean isLeaf() {
        return children.isEmpty();
    }

    // Проверка, является ли узел корневым
    public boolean isRoot() {
        return parent == null;
    }
}

// Класс, представляющий дерево
class Tree {
    private int treeId; // Идентификатор дерева
    private TreeNode root; // Корневой узел дерева

    // Конструктор дерева
    public Tree(int treeId, TreeNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Корневой узел не должен быть нулевым.");
        }
        this.treeId = treeId;
        this.root = root;
    }

    // Геттер для корневого узла
    public TreeNode getRoot() {
        return root;
    }

    // Геттер для идентификатора дерева
    public int getTreeId() {
        return treeId;
    }
}

// Основной класс программы
public class Main {
    private static int treeIdCounter = 1; // Счетчик для генерации идентификаторов деревьев

    // Метод для генерации идентификатора дерева
    private static int generateTreeId() {
        return treeIdCounter++;
    }

    // Основной метод программы
    public static void main(String[] args) {
        List<Tree> trees = readTreesFromFile("input.csv"); // Чтение деревьев из файла
        int totalTrees = trees.size(); // Общее количество деревьев
        int totalLeaves = countLeavesInAllTrees(trees); // Общее количество листьев во всех деревьях
        int maxBranchLength = getMaxBranchLength(trees); // Длина самой длинной ветки среди всех деревьев
        int maxBranchId = getMaxBranchId(trees); // Идентификатор дерева с самой длинной веткой

        // Вывод пути самой длинной ветки для каждого дерева
        for (Tree tree : trees) {
            System.out.println("Самая длинная ветка для дерева с корнем " + tree.getRoot().getId() + ": " + getLongestBranchPath(tree.getRoot()));
        }

        // Запись результатов в файл result.txt
        writeResultToFile(totalTrees, totalLeaves, maxBranchId, maxBranchLength);
    }

    // Метод для чтения деревьев из файла
    public static List<Tree> readTreesFromFile(String filename) {
        List<Tree> trees = new ArrayList<>(); // Создание списка для хранения деревьев
        Map<Integer, TreeNode> nodesMap = new HashMap<>(); // Создание карты для отображения идентификаторов узлов на сами узлы

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int nodeId = Integer.parseInt(parts[0]); // Идентификатор узла
                int parentId = Integer.parseInt(parts[1]); // Идентификатор родительского узла

                // Получение узла из карты, если он существует, или создание нового узла
                TreeNode node = nodesMap.getOrDefault(nodeId, new TreeNode(nodeId));
                TreeNode parent = nodesMap.getOrDefault(parentId, new TreeNode(parentId));

                // Проверка наличия родителя у узла
                if (node.getParent() != null) {
                    // Если узел уже имеет родителя, это может быть ошибка в данных
                    throw new IllegalArgumentException("Узел " + nodeId + " имеет несколько родителей.");
                }

                // Установка родителя узлу и добавление узла в список дочерних у родителя
                node.setParent(parent);
                parent.getChildren().add(node);

                // Обновление карты узлов
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

        return trees; // Возвращение списка деревьев
    }

    // Метод для подсчета количества листьев во всех деревьях
    public static int countLeavesInAllTrees(List<Tree> trees) {
        int totalLeaves = 0;
        for (Tree tree : trees) {
            totalLeaves += countLeaves(tree.getRoot());
        }
        return totalLeaves;
    }

    // Рекурсивный метод для подсчета количества листьев в дереве
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

    // Метод для получения длины самой длинной ветки среди всех деревьев
    public static int getMaxBranchLength(List<Tree> trees) {
        int maxBranchLength = 0;
        for (Tree tree : trees) {
            int branchLength = calculateMaxBranchLength(tree.getRoot());
            maxBranchLength = Math.max(maxBranchLength, branchLength);
        }
        return maxBranchLength;
    }

    // Метод для расчета длины самой длинной ветки в дереве (без использования рекурсии)
    private static int calculateMaxBranchLength(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int maxBranchLength = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                for (TreeNode child : node.getChildren()) {
                    queue.offer(child);
                }
            }
            maxBranchLength++;
        }

        return maxBranchLength;
    }


    // Метод для получения идентификатора дерева с самой длинной веткой
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

    // Метод для получения самой длинной ветки дерева
    private static String getLongestBranchPath(TreeNode root) {
        if (root == null) {
            return "";
        }

        Map<TreeNode, String> pathMap = new HashMap<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        pathMap.put(root, String.valueOf(root.getId()));

        String longestPath = "";

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            String currentPath = pathMap.get(node);

            if (node.isLeaf() && currentPath.length() > longestPath.length()) {
                longestPath = currentPath;
            }

            for (TreeNode child : node.getChildren()) {
                stack.push(child);
                pathMap.put(child, currentPath + "->" + child.getId());
            }
        }

        return longestPath;
    }

    // Метод для записи результатов в файл
    public static void writeResultToFile(int totalTrees, int totalLeaves, int maxBranchId, int maxBranchLength) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
            writer.write(totalTrees + " " + totalLeaves + " "+ maxBranchId + " " + (maxBranchLength - 2));
            System.out.println(totalTrees + " " + totalLeaves + " "+ maxBranchId + " " + (maxBranchLength - 2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
