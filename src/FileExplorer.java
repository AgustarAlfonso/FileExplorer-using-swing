
import java.awt.*;
import java.awt.event.*;
import java.io.*;
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
    public static final ImageIcon ICON_BACKBUTTON =
            new ImageIcon("src/com/fileexplorer/icon/backbutton.png");
    public static final ImageIcon ICON_FORWARDBUTTON =
            new ImageIcon("src/com/fileexplorer/icon/forwardbutton.png");



    protected JTree  m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;
    protected JButton backButton;
    protected JButton forwardButton;

    public Fileexplorer()
    {
        super("File explorer");
        setSize(700, 400);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                new IconData(ICON_COMPUTER, null, "Computer"));

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (int k=0; k<roots.length; k++)
        {
            node = new DefaultMutableTreeNode(new IconData(ICON_DISK,
                    null, new FileNode(roots[k])));
            top.add(node);
            node.add(new DefaultMutableTreeNode(Boolean.valueOf(true)));

        }

        m_model = new DefaultTreeModel(top);
        m_tree = new JTree(m_model);

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

        backButton = new JButton(ICON_BACKBUTTON);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle back button click action
                // You can implement navigation logic here
                // For example, you can go back to the parent directory
            }
        });

        // Create forward button with an icon
        forwardButton = new JButton(ICON_FORWARDBUTTON);
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle forward button click action
                // You can implement navigation logic here
                // For example, you can go forward to a previously visited directory
            }
        });

        backButton.setPreferredSize(new Dimension(35, 20));
        forwardButton.setPreferredSize(new Dimension(35, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonPanel.add(backButton);
        buttonPanel.add(forwardButton);

        JScrollPane treeScrollPane = new JScrollPane();
        treeScrollPane.getViewport().add(m_tree);

        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.add(buttonPanel, BorderLayout.NORTH);
        textFieldPanel.add(m_display, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(textFieldPanel, BorderLayout.NORTH);
        getContentPane().add(treeScrollPane, BorderLayout.CENTER);

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

    FileNode getFileNode(DefaultMutableTreeNode node)
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


}