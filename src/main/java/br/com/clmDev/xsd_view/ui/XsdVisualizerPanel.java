package br.com.clmDev.xsd_view.ui;

import br.com.clmDev.xsd_view.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XsdVisualizerPanel extends JPanel {
    private XsdStructure structure;
    private Graphics2D g2d;
    private FontMetrics fontMetrics;
    private List<ElementBox> elementBoxes = new ArrayList<>();
    private Map<XsdElement, Boolean> expandedState = new HashMap<>();

    // Cores para diferentes tipos
    private static final Color ELEMENT_COLOR = new Color(173, 216, 230);
    private static final Color COMPLEX_TYPE_COLOR = new Color(255, 218, 185);
    private static final Color ATTRIBUTE_COLOR = new Color(144, 238, 144);
    private static final Color ROOT_ELEMENT_COLOR = new Color(255, 182, 193);
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color CONNECTION_COLOR = new Color(100, 100, 100);
    private static final Color EXPAND_BUTTON_COLOR = new Color(220, 220, 220);

    // Configurações de layout
    private static final int ELEMENT_WIDTH = 140;
    private static final int ELEMENT_HEIGHT = 60;
    private static final int HORIZONTAL_SPACING = 180;
    private static final int VERTICAL_SPACING = 80;
    private static final int MARGIN = 50;
    private static final int EXPAND_BUTTON_SIZE = 16;

    public XsdVisualizerPanel(XsdStructure structure) {
        this.structure = structure;
        setBackground(Color.WHITE);
        initializeExpandedState();
        calculatePanelSize();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    private void initializeExpandedState() {
        // Inicializar apenas elementos raiz como expandidos (primeiro nível)
        for (XsdElement element : structure.getRootElements()) {
            expandedState.put(element, true);
            // Inicializar filhos como colapsados por padrão
            initializeChildrenAsCollapsed(element);
        }
    }

    private void initializeChildrenAsCollapsed(XsdElement element) {
        for (XsdElement child : element.getChildren()) {
            expandedState.put(child, false);
            initializeChildrenAsCollapsed(child);
        }
    }

    private void calculatePanelSize() {
        if (structure.isEmpty()) {
            setPreferredSize(new Dimension(400, 200));
            return;
        }

        int maxDepth = calculateMaxDepth();
        int totalHeight = calculateTotalHeight();

        int width = Math.max(800, (maxDepth + 1) * HORIZONTAL_SPACING + 2 * MARGIN);
        int height = Math.max(600, totalHeight + 2 * MARGIN);

        setPreferredSize(new Dimension(width, height));
        revalidate();
    }

    private int calculateTotalHeight() {
        int totalHeight = 0;
        for (XsdElement element : structure.getRootElements()) {
            totalHeight += calculateElementHeight(element, 0);
            totalHeight += VERTICAL_SPACING;
        }

        for (XsdComplexType complexType : structure.getComplexTypes()) {
            totalHeight += calculateComplexTypeHeight(complexType);
            totalHeight += VERTICAL_SPACING;
        }

        return totalHeight;
    }

    private int calculateElementHeight(XsdElement element, int level) {
        int height = ELEMENT_HEIGHT;

        if (expandedState.getOrDefault(element, false) && !element.getChildren().isEmpty()) {
            int childrenHeight = 0;
            for (XsdElement child : element.getChildren()) {
                childrenHeight += calculateElementHeight(child, level + 1);
                childrenHeight += VERTICAL_SPACING / 2;
            }
            height = Math.max(height, childrenHeight);
        }

        return height;
    }

    private int calculateComplexTypeHeight(XsdComplexType complexType) {
        int height = ELEMENT_HEIGHT;
        int childrenHeight = 0;

        for (XsdElement element : complexType.getElements()) {
            childrenHeight += calculateElementHeight(element, 1);
            childrenHeight += VERTICAL_SPACING / 2;
        }

        return Math.max(height, childrenHeight);
    }

    private int calculateMaxDepth() {
        int maxDepth = 0;
        for (XsdElement element : structure.getRootElements()) {
            maxDepth = Math.max(maxDepth, calculateElementDepth(element, 0));
        }
        return maxDepth;
    }

    private int calculateElementDepth(XsdElement element, int currentDepth) {
        if (!expandedState.getOrDefault(element, false)) {
            return currentDepth;
        }

        int maxChildDepth = currentDepth;
        for (XsdElement child : element.getChildren()) {
            maxChildDepth = Math.max(maxChildDepth, calculateElementDepth(child, currentDepth + 1));
        }
        return maxChildDepth;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        fontMetrics = g2d.getFontMetrics();
        elementBoxes.clear();

        if (structure.isEmpty()) {
            drawEmptyMessage();
            return;
        }

        drawSchema();
    }

    private void drawEmptyMessage() {
        String message = "Nenhum elemento encontrado no arquivo XSD";
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = g2d.getFont().getStringBounds(message, frc);

        int x = (getWidth() - (int) bounds.getWidth()) / 2;
        int y = (getHeight() - (int) bounds.getHeight()) / 2;

        g2d.setColor(Color.GRAY);
        g2d.drawString(message, x, y);
    }

    private void drawSchema() {
        int startY = MARGIN;

        // Desenhar elementos raiz
        for (XsdElement element : structure.getRootElements()) {
            startY += drawElement(element, MARGIN, startY, 0);
            startY += VERTICAL_SPACING;
        }

        // Desenhar tipos complexos independentes
        for (XsdComplexType complexType : structure.getComplexTypes()) {
            startY += drawComplexType(complexType, MARGIN, startY);
            startY += VERTICAL_SPACING;
        }
    }

    private int drawElement(XsdElement element, int x, int y, int level) {
        Color elementColor = element.isRoot() ? ROOT_ELEMENT_COLOR : ELEMENT_COLOR;

        // Desenhar caixa do elemento
        ElementBox box = new ElementBox(x, y, ELEMENT_WIDTH, ELEMENT_HEIGHT, element);
        elementBoxes.add(box);

        g2d.setColor(elementColor);
        g2d.fillRect(x, y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(x, y, ELEMENT_WIDTH, ELEMENT_HEIGHT);

        // Desenhar botão de expandir/colapsar se há filhos
        if (!element.getChildren().isEmpty()) {
            drawExpandCollapseButton(x, y, element);
        }

        // Desenhar texto do elemento
        g2d.setColor(TEXT_COLOR);
        Font boldFont = g2d.getFont().deriveFont(Font.BOLD);
        g2d.setFont(boldFont);

        String name = truncateText(element.getName(), ELEMENT_WIDTH - 30); // Reservar espaço para botão
        g2d.drawString(name, x + 25, y + 16);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        String type = truncateText("(" + element.getType() + ")", ELEMENT_WIDTH - 30);
        g2d.drawString(type, x + 25, y + 32);

        String occurrence = element.getOccurrenceString();
        if (!occurrence.isEmpty()) {
            g2d.setColor(Color.BLUE);
            g2d.drawString(occurrence, x + 25, y + 48);
        }

        // Desenhar atributos como pequenos círculos
        int attrX = x + ELEMENT_WIDTH + 5;
        int attrY = y + 10;
        for (XsdAttribute attr : element.getAttributes()) {
            g2d.setColor(ATTRIBUTE_COLOR);
            g2d.fillOval(attrX, attrY, 15, 15);
            g2d.setColor(BORDER_COLOR);
            g2d.drawOval(attrX, attrY, 15, 15);

            attrY += 15;
        }

        // Desenhar filhos recursivamente apenas se expandido
        int totalChildHeight = 0;
        if (expandedState.getOrDefault(element, false) && !element.getChildren().isEmpty()) {
            int childX = x + HORIZONTAL_SPACING;
            int childY = y;

            for (XsdElement child : element.getChildren()) {
                // Desenhar linha de conexão
                g2d.setColor(CONNECTION_COLOR);
                g2d.drawLine(x + ELEMENT_WIDTH, y + ELEMENT_HEIGHT / 2, childX, childY + ELEMENT_HEIGHT / 2);

                int childHeight = drawElement(child, childX, childY, level + 1);
                childY += childHeight + VERTICAL_SPACING / 2;
                totalChildHeight += childHeight + VERTICAL_SPACING / 2;
            }
        }

        return Math.max(ELEMENT_HEIGHT, totalChildHeight);
    }

    private void drawExpandCollapseButton(int x, int y, XsdElement element) {
        boolean isExpanded = expandedState.getOrDefault(element, false);

        // Desenhar botão
        g2d.setColor(EXPAND_BUTTON_COLOR);
        g2d.fillRect(x + 5, y + 5, EXPAND_BUTTON_SIZE, EXPAND_BUTTON_SIZE);
        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(x + 5, y + 5, EXPAND_BUTTON_SIZE, EXPAND_BUTTON_SIZE);

        // Desenhar símbolo + ou -
        g2d.setColor(TEXT_COLOR);
        int centerX = x + 5 + EXPAND_BUTTON_SIZE / 2;
        int centerY = y + 5 + EXPAND_BUTTON_SIZE / 2;

        // Linha horizontal sempre presente
        g2d.drawLine(centerX - 4, centerY, centerX + 4, centerY);

        // Linha vertical apenas quando colapsado
        if (!isExpanded) {
            g2d.drawLine(centerX, centerY - 4, centerX, centerY + 4);
        }
    }

    private int drawComplexType(XsdComplexType complexType, int x, int y) {
        // Desenhar caixa do tipo complexo
        g2d.setColor(COMPLEX_TYPE_COLOR);
        g2d.fillRect(x, y, ELEMENT_WIDTH, ELEMENT_HEIGHT);
        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(x, y, ELEMENT_WIDTH, ELEMENT_HEIGHT);

        // Desenhar texto
        g2d.setColor(TEXT_COLOR);
        Font boldFont = g2d.getFont().deriveFont(Font.BOLD);
        g2d.setFont(boldFont);

        String name = truncateText("<<" + complexType.getName() + ">>", ELEMENT_WIDTH - 10);
        g2d.drawString(name, x + 5, y + 20);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
        g2d.drawString("ComplexType", x + 5, y + 40);

        // Desenhar elementos do tipo complexo
        int childX = x + HORIZONTAL_SPACING;
        int childY = y;
        int totalHeight = ELEMENT_HEIGHT;

        for (XsdElement element : complexType.getElements()) {
            // Linha de conexão
            g2d.setColor(CONNECTION_COLOR);
            g2d.drawLine(x + ELEMENT_WIDTH, y + ELEMENT_HEIGHT / 2, childX, childY + ELEMENT_HEIGHT / 2);

            int elementHeight = drawElement(element, childX, childY, 1);
            childY += elementHeight + VERTICAL_SPACING / 2;
            totalHeight = Math.max(totalHeight, elementHeight);
        }

        return totalHeight;
    }

    private String truncateText(String text, int maxWidth) {
        if (text == null) return "";

        if (fontMetrics.stringWidth(text) <= maxWidth) {
            return text;
        }

        String truncated = text;
        while (fontMetrics.stringWidth(truncated + "...") > maxWidth && truncated.length() > 1) {
            truncated = truncated.substring(0, truncated.length() - 1);
        }
        return truncated + "...";
    }

    private void handleMouseClick(int x, int y) {
        // Verificar clique no botão de expandir/colapsar primeiro
        for (ElementBox box : elementBoxes) {
            if (box.element != null && !box.element.getChildren().isEmpty()) {
                // Verificar se clicou no botão de expandir/colapsar
                int buttonX = box.x + 5;
                int buttonY = box.y + 5;

                if (x >= buttonX && x <= buttonX + EXPAND_BUTTON_SIZE &&
                        y >= buttonY && y <= buttonY + EXPAND_BUTTON_SIZE) {

                    // Alternar estado de expansão apenas deste elemento
                    boolean currentState = expandedState.getOrDefault(box.element, false);
                    expandedState.put(box.element, !currentState);

                    // Recalcular tamanho e redesenhar
                    calculatePanelSize();
                    repaint();
                    return;
                }
            }
        }

        // Verificar clique normal no elemento para mostrar detalhes
        for (ElementBox box : elementBoxes) {
            if (box.contains(x, y)) {
                showElementDetails(box.element);
                break;
            }
        }
    }

    private void showElementDetails(XsdElement element) {
        StringBuilder details = new StringBuilder();
        details.append("Elemento: ").append(element.getName()).append("\n");
        details.append("Tipo: ").append(element.getType()).append("\n");
        details.append("Ocorrências: ").append(element.getMinOccurs()).append("..").append(element.getMaxOccurs()).append("\n");

        if (!element.getAttributes().isEmpty()) {
            details.append("\nAtributos:\n");
            for (XsdAttribute attr : element.getAttributes()) {
                details.append("  ").append(attr.toString()).append("\n");
            }
        }



        if (element.getDocumentation() != null) {
            details.append("\nDocumentação:\n").append(element.getDocumentation());
        }

        JOptionPane.showMessageDialog(this, details.toString(),
                "Detalhes do Elemento", JOptionPane.INFORMATION_MESSAGE);
    }

    // Métodos públicos para controle externo - CORRIGIDOS
    public void expandAll() {
        // Expandir todos os elementos recursivamente
        for (XsdElement element : structure.getRootElements()) {
            setElementExpandedRecursively(element, true);
        }
        calculatePanelSize();
        repaint();
    }

    public void collapseAll() {
        // Colapsar todos os elementos recursivamente
        for (XsdElement element : structure.getRootElements()) {
            setElementExpandedRecursively(element, false);
        }
        calculatePanelSize();
        repaint();
    }

    public void expandFirstLevel() {
        // Expandir apenas o primeiro nível
        for (XsdElement element : structure.getRootElements()) {
            expandedState.put(element, true);
            // Colapsar todos os filhos
            setElementExpandedRecursively(element.getChildren(), false);
        }
        calculatePanelSize();
        repaint();
    }

    public void collapseFirstLevel() {
        // Colapsar apenas o primeiro nível
        for (XsdElement element : structure.getRootElements()) {
            expandedState.put(element, false);
        }
        calculatePanelSize();
        repaint();
    }

    private void setElementExpandedRecursively(XsdElement element, boolean expanded) {
        expandedState.put(element, expanded);
        for (XsdElement child : element.getChildren()) {
            setElementExpandedRecursively(child, expanded);
        }
    }

    private void setElementExpandedRecursively(List<XsdElement> elements, boolean expanded) {
        for (XsdElement element : elements) {
            setElementExpandedRecursively(element, expanded);
        }
    }

    // Classe auxiliar para detectar cliques
    private static class ElementBox {
        final int x, y, width, height;
        final XsdElement element;

        ElementBox(int x, int y, int width, int height, XsdElement element) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.element = element;
        }

        boolean contains(int px, int py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }
}