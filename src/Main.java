import java.util.ArrayList;
import java.util.List;

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
            throw new IllegalArgumentException("Root node cannot be null.");
        }
        this.root = root;
    }

    public TreeNode getRoot() {
        return root;
    }
}
