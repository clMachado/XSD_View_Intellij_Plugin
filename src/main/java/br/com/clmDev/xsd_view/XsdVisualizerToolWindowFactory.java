package br.com.clmDev.xsd_view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class XsdVisualizerToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Criar painel inicial vazio
        JPanel emptyPanel = createEmptyPanel();

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(emptyPanel, "Inicial", false);
        content.setCloseable(false);

        toolWindow.getContentManager().addContent(content);
    }

    private JPanel createEmptyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel(
                /*"<html><div style='text-align: center; padding: 20px; font-family: sans-serif;'>" +
                        "<h2 style='color: #2E3440; margin-bottom: 20px;'>🔍 XSD View</h2>" +
                        "<p style='margin-bottom: 15px;'><strong>Para visualizar um arquivo XSD:</strong></p>" +
                        "<ol style='text-align: left; display: inline-block; margin-bottom: 20px;'>" +
                        "<li>Abra ou selecione um arquivo .xsd no projeto</li>" +
                        "<li>Clique com o botão direito no arquivo</li>" +
                        "<li>Selecione 'Visualizar XSD' no menu contextual</li>" +
                        "</ol>" +

                        "<div style='background-color: #F0F8FF; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                        "<p style='margin: 0; color: #4A5568;'><strong>💡 Funcionalidades disponíveis:</strong></p>" +
                        "<ul style='text-align: left; display: inline-block; margin: 10px 0 0 0;'>" +
                        "<li><strong>Scroll:</strong> Use as barras de rolagem para navegar</li>" +
                        "<li><strong>Expandir/Colapsar:</strong> Clique nos botões +/- dos elementos</li>" +
                        "<li><strong>Toolbar:</strong> Botões para expandir/colapsar tudo</li>" +
                        "<li><strong>Detalhes:</strong> Clique no elemento para ver informações</li>" +
                        "</ul>" +
                        "</div>" +

                        "<p style='font-size: 11px; color: #888;'>A representação gráfica aparecerá nesta janela</p>" +
                        "</div></html>"*/
                        "<html><div style='text-align: center; padding: 20px; font-family: sans-serif;'>" +
                        "<h2 style='color: #2E3440; margin-bottom: 20px;'>🔍 XSD View</h2>" +
                        "<div style='background-color: #3d743f; padding: 15px; border-radius: 5px; margin: 20px 0;'></div>" +
                        "   <p style='background-color: #86b0d4;'><strong>Para visualizar um arquivo XSD:</strong></p>" +
                        "   <ol style='background-color: #86b0d4; text-align: left; display: inline-block'>" +
                        "      <li>Abra ou selecione um arquivo .xsd no projeto</li>" +
                        "      <li>Clique com o botão direito no arquivo</li>" +
                        "      <li>Selecione 'Visualizar XSD' no menu contextual</li>" +
                        "   </ol>" +
                        "</div>" +
                        "<div style='background-color: #86b0d4; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                        "   <p style='margin: 0; color: #4A5568;'><strong>💡 Funcionalidades disponíveis:</strong></p>" +
                        "   <ul style='text-align: left; display: inline-block; margin: 10px 0 0 0;'>" +
                        "      <li><strong>Scroll:</strong> Use as barras de rolagem para navegar</li>" +
                        "      <li><strong>Expandir/Colapsar:</strong> Clique nos botões +/- dos elementos</li>" +
                        "      <li><strong>Detalhes:</strong> Clique no elemento para ver informações</li>" +
                        "   </ul>" +
                        "</div>" +
                        "</div></html>"

        );

        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(instructionLabel, BorderLayout.CENTER);

        // Adicionar um painel de rodapé com dicas rápidas
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(248, 249, 250));

        JLabel tipLabel = new JLabel("💡 Dica: Elementos grandes podem ser colapsados para melhor navegação");
        tipLabel.setFont(tipLabel.getFont().deriveFont(Font.ITALIC, 10f));
        tipLabel.setForeground(new Color(108, 117, 125));

        footerPanel.add(tipLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }
}