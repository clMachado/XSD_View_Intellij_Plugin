package br.com.clmDev.xsd_view;

import br.com.clmDev.xsd_view.model.XsdStructure;
import br.com.clmDev.xsd_view.parser.XsdParser;
import br.com.clmDev.xsd_view.ui.XsdVisualizerPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XsdVisualizerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = e.getProject();

        if (file == null || project == null) {
            return;
        }

        if (!"xsd".equalsIgnoreCase(file.getExtension())) {
            Messages.showWarningDialog(project,
                    "Por favor, selecione um arquivo XSD (.xsd)",
                    "Arquivo Inválido");
            return;
        }

        try {
            // Parse do arquivo XSD
            XsdParser parser = new XsdParser();
            XsdStructure structure = parser.parseXsd(file);

            if (structure.isEmpty()) {
                Messages.showInfoMessage(project,
                        "O arquivo XSD não contém elementos ou tipos complexos para visualizar.",
                        "Arquivo Vazio");
                return;
            }

            // Mostrar visualização
            showVisualization(project, file, structure);

        } catch (Exception ex) {
            Messages.showErrorDialog(project,
                    "Erro ao processar o arquivo XSD:\n" + ex.getMessage(),
                    "Erro no Parser");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(true);
    }

    private void showVisualization(Project project, VirtualFile file, XsdStructure structure) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("XSD View");

        if (toolWindow == null) {
            Messages.showErrorDialog(project,
                    "Tool Window 'XSD View' não encontrada. " +
                            "Verifique se o plugin foi instalado corretamente.",
                    "Erro de Configuração");
            return;
        }

        // Criar painel de visualização
        XsdVisualizerPanel visualizerPanel = new XsdVisualizerPanel(structure);

        // Criar painel principal com toolbar e visualizador
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Criar toolbar com controles aprimorados
        JToolBar toolBar = createToolBar(visualizerPanel);
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Criar scroll pane
        JScrollPane scrollPane = new JScrollPane(visualizerPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Criar painel de status
        JPanel statusPanel = createStatusPanel(structure);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Criar conteúdo para a tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(mainPanel, file.getName(), false);
        content.setCloseable(true);

        // Adicionar conteúdo à tool window
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);

        // Mostrar a tool window
        toolWindow.show();
    }

    private JToolBar createToolBar(XsdVisualizerPanel visualizerPanel) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*
        // Botão Expandir Tudo
        JButton expandAllBtn = new JButton("Expandir Tudo");
        expandAllBtn.setToolTipText("Expande todos os elementos recursivamente");
        expandAllBtn.setIcon(createExpandIcon());
        expandAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizerPanel.expandAll();
                refreshScrollPane(visualizerPanel);
            }
        });

        // Botão Colapsar Tudo
        JButton collapseAllBtn = new JButton("Colapsar Tudo");
        collapseAllBtn.setToolTipText("Colapsa todos os elementos recursivamente");
        collapseAllBtn.setIcon(createCollapseIcon());
        collapseAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizerPanel.collapseAll();
                refreshScrollPane(visualizerPanel);
            }
        });

        // Botão Primeiro Nível
        JButton firstLevelBtn = new JButton("1º Nível");
        firstLevelBtn.setToolTipText("Expande apenas o primeiro nível");
        firstLevelBtn.setIcon(createFirstLevelIcon());
        firstLevelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizerPanel.expandFirstLevel();
                refreshScrollPane(visualizerPanel);
            }
        });

        // Botão Reset View
        JButton resetBtn = new JButton("Reset");
        resetBtn.setToolTipText("Restaura visualização padrão (primeiro nível expandido)");
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizerPanel.expandFirstLevel();
                refreshScrollPane(visualizerPanel);
            }
        });
*/
        // Separador
        toolBar.addSeparator();

        // Label informativo
        JLabel statusLabel = new JLabel("Use os botões +/- nos elementos para expandir/colapsar individualmente");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC, 10f));
        statusLabel.setForeground(Color.GRAY);

        // Adicionar componentes à toolbar
  /*      toolBar.add(expandAllBtn);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(collapseAllBtn);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(firstLevelBtn);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(resetBtn);
  */      toolBar.add(Box.createHorizontalGlue());
        toolBar.add(statusLabel);

        return toolBar;
    }

    private JPanel createStatusPanel(XsdStructure structure) {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Informações do XSD
        JLabel elementsLabel = new JLabel(
            String.format("Elementos raiz: %d | Tipos complexos: %d", 
            structure.getRootElements().size(), 
            structure.getComplexTypes().size())
        );
        elementsLabel.setFont(elementsLabel.getFont().deriveFont(Font.PLAIN, 11f));
        elementsLabel.setForeground(new Color(108, 117, 125));

        // Namespace info
        if (structure.getTargetNamespace() != null && !structure.getTargetNamespace().isEmpty()) {
            JLabel namespaceLabel = new JLabel(" | Namespace: " + structure.getTargetNamespace());
            namespaceLabel.setFont(namespaceLabel.getFont().deriveFont(Font.PLAIN, 10f));
            namespaceLabel.setForeground(new Color(108, 117, 125));
            statusPanel.add(elementsLabel);
            statusPanel.add(namespaceLabel);
        } else {
            statusPanel.add(elementsLabel);
        }

        return statusPanel;
    }

    private void refreshScrollPane(XsdVisualizerPanel visualizerPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Container parent = visualizerPanel.getParent();
                while (parent != null && !(parent instanceof JScrollPane)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JScrollPane) {
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });
    }

    private Icon createExpandIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 120, 0));
                g2.fillRect(x + 2, y + 7, 12, 2);
                g2.fillRect(x + 7, y + 2, 2, 12);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }

    private Icon createCollapseIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(120, 0, 0));
                g2.fillRect(x + 2, y + 7, 12, 2);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }

    private Icon createFirstLevelIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 120));
                // Desenhar símbolo de árvore simples
                g2.drawRect(x + 2, y + 2, 12, 4);
                g2.drawLine(x + 8, y + 6, x + 8, y + 10);
                g2.drawRect(x + 5, y + 10, 6, 4);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }
}