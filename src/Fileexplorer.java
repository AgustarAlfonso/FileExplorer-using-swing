import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

class Fileexplorer
        extends JFrame
{
    public static final ImageIcon ICON_COMPUTER =
            new ImageIcon("src/com/fileexplorer/icon/computer.png");
    public static final ImageIcon ICON_DISK =
            new ImageIcon("src/com/fileexplorer/icon/disk.png");
    public static final ImageIcon ICON_FOLDER =
            new ImageIcon("src/com/fileexplorer/icon/folder.png");
    public static final ImageIcon ICON_EXPANDEDFOLDER =
            new ImageIcon("src/com/fileexplorer/icon/expandedfolder.png");
    public static final ImageIcon ICON_FILE =
            new ImageIcon("src/com/fileexplorer/icon/file.png");
    public static final ImageIcon ICON_SEARCHBUTTON =
            new ImageIcon("src/com/fileexplorer/icon/searchbutton.png");

    private DefaultMutableTreeNode top = new DefaultMutableTreeNode(
            new IconData(ICON_COMPUTER, null, "Computer"));

    protected DefaultTreeModel m_model = new DefaultTreeModel(top);

    protected JTree  m_tree = new JTree(m_model);

    protected JTextField m_display;
    protected JTextField m_search;
    protected JButton searchButton;


    public Fileexplorer()
    {
        super("File explorer");
        setSize(700, 400);


        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (int k=0; k<roots.length; k++)
        {
            node = new DefaultMutableTreeNode(new IconData(ICON_DISK,
                    null, new FileNode(roots[k])));
            top.add(node);
            node.add(new DefaultMutableTreeNode(Boolean.valueOf(true)));

        }

        m_tree.putClientProperty("JTree.lineStyle", "Angled");

        TreeCellRenderer renderer = new
                IconCellRenderer();
        m_tree.setCellRenderer(renderer);

        m_tree.addTreeExpansionListener(new
                DirExpansionListener());

        m_tree.addTreeSelectionListener(new
                DirSelectionListener());

        m_tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        m_tree.setShowsRootHandles(true);
        m_tree.setEditable(false);

        JScrollPane s = new JScrollPane();
        s.getViewport().add(m_tree);
        getContentPane().add(s, BorderLayout.CENTER);

        m_display = new JTextField();
        m_display.setEditable(false);
        getContentPane().add(m_display, BorderLayout.NORTH);

        m_search = new JTextField();
        m_search.setPreferredSize(new Dimension(150, 20));

        searchButton = new JButton(ICON_SEARCHBUTTON);
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = searchNode(m_search.getText());
                if (node != null) {
                    TreeNode[] nodes = m_model.getPathToRoot(node);
                    TreePath path = new TreePath(nodes);
                    m_tree.scrollPathToVisible(path);
                    m_tree.setSelectionPath(path);
                } else {
                    System.out.println("Node with string " + m_search.getText() + " not found");
                }
            }
        });
        searchButton.setPreferredSize(new Dimension(40, 20));
        searchButton.setBackground(Color.WHITE);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        inputPanel.add(m_search);
        inputPanel.add(searchButton);

        getContentPane().add(inputPanel, BorderLayout.SOUTH);


        WindowListener wndCloser = new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        };
        addWindowListener(wndCloser);

        setVisible(true);
    }

    DefaultMutableTreeNode getTreeNode(TreePath path)
    {
        return (DefaultMutableTreeNode)(path.getLastPathComponent());
    }

    static FileNode getFileNode(DefaultMutableTreeNode node)
    {
        if (node == null)
            return null;
        Object obj = node.getUserObject();
        if (obj instanceof IconData)
            obj = ((IconData)obj).getObject();
        if (obj instanceof FileNode)
            return (FileNode)obj;
        else
            return null;
    }

    // Make sure expansion is threaded and updating the tree model
    // only occurs within the event dispatching thread.
    class DirExpansionListener implements TreeExpansionListener
    {
        public void treeExpanded(TreeExpansionEvent event)
        {
            final DefaultMutableTreeNode node = getTreeNode(
                    event.getPath());
            final FileNode fnode = getFileNode(node);

            Thread runner = new Thread()
            {
                public void run()
                {
                    if (fnode != null && fnode.expand(node))
                    {
                        Runnable runnable = new Runnable()
                        {
                            public void run()
                            {
                                m_model.reload(node);
                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            };
            runner.start();
        }

        public void treeCollapsed(TreeExpansionEvent event) {}
    }


    class DirSelectionListener
            implements TreeSelectionListener
    {
        public void valueChanged(TreeSelectionEvent event)
        {
            DefaultMutableTreeNode node = getTreeNode(
                    event.getPath());
            FileNode fnode = getFileNode(node);
            if (fnode != null)
                m_display.setText(fnode.getFile().
                        getAbsolutePath());
            else
                m_display.setText("");
        }
    }

    public DefaultMutableTreeNode searchNode(String nodeStr) {
        DefaultMutableTreeNode lastSelectedNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();

        // Expand the last selected node and update the tree model
        if (lastSelectedNode != null) {
            expandNode(lastSelectedNode);
            m_model.reload(lastSelectedNode);
        }

        Enumeration e = lastSelectedNode.breadthFirstEnumeration();
        DefaultMutableTreeNode node;

        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (nodeStr.equalsIgnoreCase(node.getUserObject().toString())) {
                return node;
            }
        }

        return null;
    }

    // Recursive method to expand a node and its children
    // Recursive method to expand a node and its children
    private void expandNode(DefaultMutableTreeNode node) {
        if (node != null && !node.isLeaf()) {
            FileNode fileNode = getFileNode(node);
            if (fileNode != null) {
                fileNode.expand(node);
            }

            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                expandNode(child);
            }
        }
    }




}