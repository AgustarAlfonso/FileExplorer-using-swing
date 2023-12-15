import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Vector;

class FileNode
{
    protected File m_file;

    public FileNode(File file)
    {
        m_file = file;
    }

    public File getFile()
    {
        return m_file;
    }

    public String toString()
    {
        return m_file.getName().length() > 0 ? m_file.getName() :
                m_file.getPath();
    }

    public boolean expand(DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode flag = (DefaultMutableTreeNode) parent.getFirstChild();
        if (flag == null)    // No flag
            return false;

        Object obj = flag.getUserObject();
        if (!(obj instanceof Boolean))
            return false;      // Already expanded

        parent.removeAllChildren();  // Remove Flag

        File[] files = listFiles();
        if (files == null)
            return true;

        Vector<FileNode> directories = new Vector<>();
        Vector<FileNode> regularFiles = new Vector<>();

        // Separate directories and regular files
        for (File file : files) {
            FileNode newNode = new FileNode(file);
            if (file.isDirectory()) {
                directories.add(newNode);
            } else {
                regularFiles.add(newNode);
            }
        }

        // Sort directories and regular files separately
        directories.sort(FileNode::compareTo);
        regularFiles.sort(FileNode::compareTo);

        // Add directories first, then regular files
        for (FileNode dirNode : directories) {
            IconData idata = new IconData(
                    Fileexplorer.ICON_FOLDER,
                    Fileexplorer.ICON_EXPANDEDFOLDER, dirNode);

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(idata);
            parent.add(node);

            if (dirNode.getFile().isDirectory()) {
                node.add(new DefaultMutableTreeNode(Boolean.TRUE));
            }
        }

        for (FileNode fileNode : regularFiles) {
            IconData idata = new IconData(
                    Fileexplorer.ICON_FILE,
                    Fileexplorer.ICON_EXPANDEDFOLDER, fileNode);

            parent.add(new DefaultMutableTreeNode(idata));
        }

        return true;
    }

    public boolean hasSubDirs()
    {
        File[] files = listFiles();
        if (files == null)
            return false;
        for (int k=0; k<files.length; k++)
        {
            if (files[k].isDirectory())
                return true;
        }
        return false;
    }

    public int compareTo(FileNode toCompare)
    {
        return  m_file.getName().compareToIgnoreCase(
                toCompare.m_file.getName() );
    }

    protected File[] listFiles()
    {
        if (!m_file.isDirectory())
            return null;
        try
        {
            return m_file.listFiles();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null,
                    "Error reading directory "+m_file.getAbsolutePath(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}

