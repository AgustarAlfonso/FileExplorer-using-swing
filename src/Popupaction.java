import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;


public class Popupaction {
    private JTree m_tree;
    private DefaultTreeModel m_model;
    private JTextField m_display;

    public Popupaction(JTree tree, DefaultTreeModel model, JTextField display) {
        this.m_tree = tree;
        this.m_model = model;
        this.m_display = display;
    }

    Action openAction = new AbstractAction("Open") {
        @Override
        public void actionPerformed(ActionEvent e) {
            openSelectedFile();
        }
    };

    Action deleteAction = new AbstractAction("Delete") {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteSelectedFile();
        }
    };

    Action renameAction = new AbstractAction("Rename") {
        @Override
        public void actionPerformed(ActionEvent e) {
            renameSelectedFile();
        }
    };

    private void openSelectedFile() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            FileNode fileNode = Fileexplorer.getFileNode(selectedNode);
            if (fileNode != null) {
                File file = fileNode.getFile();
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Error tidak dapat membuka file tersebut: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    public void deleteSelectedFile() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            FileNode fileNode = Fileexplorer.getFileNode(selectedNode);
            if (fileNode != null) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete " + fileNode.getFile().getName() + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    boolean success = deleteFileOrDirectory(fileNode.getFile());
                    if (success) {
                        JOptionPane.showMessageDialog(null, "File berhasil dihapus");
                        refreshTree();
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal menghapus file.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public void renameSelectedFile() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            FileNode fileNode = Fileexplorer.getFileNode(selectedNode);
            if (fileNode != null) {
                String newName = JOptionPane.showInputDialog(null,
                        "Enter new name for " + fileNode.getFile().getName() + ":");
                if (newName != null && !newName.trim().isEmpty()) {
                    File newFile = new File(fileNode.getFile().getParentFile(), newName);
                    boolean success = fileNode.getFile().renameTo(newFile);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Berhasil mengganti nama file.");
                        refreshTree();
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal mengganti nama file.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private boolean deleteFileOrDirectory(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    deleteFileOrDirectory(f);
                }
            }
        }
        return file.delete();
    }

    private void refreshTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) m_model.getRoot();
        root.removeAllChildren();
        File[] roots = File.listRoots();
        for (File rootFile : roots) {
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new IconData(Fileexplorer.ICON_DISK, null, new FileNode(rootFile)));
            root.add(rootNode);
            rootNode.add(new DefaultMutableTreeNode(Boolean.valueOf(true)));
        }
        m_model.reload();
    }
}
