import javax.swing.tree.*;
import java.util.Stack;

class HistoryManager {
    private Stack<TreePath> historyBack = new Stack<>();
    private Stack<TreePath> historyForward = new Stack<>();

    public void addToHistory(TreePath path) {
        historyBack.push(path);
        historyForward.clear(); // Clear forward history when a new path is added
    }

    public TreePath navigateBack() {
        if (!historyBack.isEmpty()) {
            TreePath currentPath = historyBack.pop();
            historyForward.push(currentPath);
            return currentPath;
        }
        return null;
    }

    public TreePath navigateForward() {
        if (!historyForward.isEmpty()) {
            TreePath currentPath = historyForward.pop();
            historyBack.push(currentPath);
            return currentPath;
        }
        return null;
    }
}