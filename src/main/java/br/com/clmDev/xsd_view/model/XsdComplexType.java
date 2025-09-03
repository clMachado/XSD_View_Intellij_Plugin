package br.com.clmDev.xsd_view.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um tipo complexo XSD.
 * Exemplo: <xs:complexType name="PersonType">...</xs:complexType>
 */
public class XsdComplexType implements XsdType {
    private String name;
    private List<XsdElement> elements = new ArrayList<>();
    private List<XsdAttribute> attributes = new ArrayList<>();
    private String documentation;
    private String baseType; // Para extensões
    private boolean isAbstract = false;
    private boolean mixed = false; // Para conteúdo misto
    
    /**
     * Construtor com nome do tipo complexo.
     * @param name Nome do tipo complexo
     */
    public XsdComplexType(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Define o nome do tipo complexo.
     * @param name Nome do tipo
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Retorna a lista de elementos do tipo complexo.
     * @return Lista de elementos
     */
    public List<XsdElement> getElements() {
        return elements;
    }
    
    /**
     * Adiciona um elemento ao tipo complexo.
     * @param element Elemento a ser adicionado
     */
    public void addElement(XsdElement element) {
        elements.add(element);
    }
    
    /**
     * Remove um elemento do tipo complexo.
     * @param element Elemento a ser removido
     * @return true se o elemento foi removido
     */
    public boolean removeElement(XsdElement element) {
        return elements.remove(element);
    }
    
    /**
     * Retorna a lista de atributos do tipo complexo.
     * @return Lista de atributos
     */
    public List<XsdAttribute> getAttributes() {
        return attributes;
    }
    
    /**
     * Adiciona um atributo ao tipo complexo.
     * @param attribute Atributo a ser adicionado
     */
    public void addAttribute(XsdAttribute attribute) {
        attributes.add(attribute);
    }
    
    /**
     * Remove um atributo do tipo complexo.
     * @param attribute Atributo a ser removido
     * @return true se o atributo foi removido
     */
    public boolean removeAttribute(XsdAttribute attribute) {
        return attributes.remove(attribute);
    }
    
    /**
     * Retorna a documentação do tipo complexo.
     * @return Documentação
     */
    public String getDocumentation() {
        return documentation;
    }
    
    /**
     * Define a documentação do tipo complexo.
     * @param documentation Documentação
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    /**
     * Retorna o tipo base (para extensões).
     * @return Tipo base
     */
    public String getBaseType() {
        return baseType;
    }
    
    /**
     * Define o tipo base (para extensões).
     * @param baseType Tipo base
     */
    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }
    
    /**
     * Verifica se o tipo é abstrato.
     * @return true se abstrato
     */
    public boolean isAbstract() {
        return isAbstract;
    }
    
    /**
     * Define se o tipo é abstrato.
     * @param isAbstract true se abstrato
     */
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
    
    /**
     * Verifica se o tipo permite conteúdo misto.
     * @return true se permite conteúdo misto
     */
    public boolean isMixed() {
        return mixed;
    }
    
    /**
     * Define se o tipo permite conteúdo misto.
     * @param mixed true se permite conteúdo misto
     */
    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }
    
    /**
     * Verifica se o tipo complexo tem elementos.
     * @return true se tem elementos
     */
    public boolean hasElements() {
        return !elements.isEmpty();
    }
    
    /**
     * Verifica se o tipo complexo tem atributos.
     * @return true se tem atributos
     */
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }
    
    /**
     * Verifica se o tipo complexo está vazio (sem elementos nem atributos).
     * @return true se vazio
     */
    public boolean isEmpty() {
        return elements.isEmpty() && attributes.isEmpty();
    }
    
    /**
     * Retorna o número total de elementos.
     * @return Número de elementos
     */
    public int getElementCount() {
        return elements.size();
    }
    
    /**
     * Retorna o número total de atributos.
     * @return Número de atributos
     */
    public int getAttributeCount() {
        return attributes.size();
    }
    
    /**
     * Busca um elemento por nome.
     * @param elementName Nome do elemento
     * @return Elemento encontrado ou null
     */
    public XsdElement findElement(String elementName) {
        return elements.stream()
            .filter(element -> elementName.equals(element.getName()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Busca um atributo por nome.
     * @param attributeName Nome do atributo
     * @return Atributo encontrado ou null
     */
    public XsdAttribute findAttribute(String attributeName) {
        return attributes.stream()
            .filter(attribute -> attributeName.equals(attribute.getName()))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public String getDisplayName() {
        StringBuilder display = new StringBuilder();
        display.append("<<").append(name).append(">>");
        
        if (isAbstract) {
            display.append(" (abstract)");
        }
        
        if (baseType != null) {
            display.append(" extends ").append(baseType);
        }
        
        return display.toString();
    }
    
    /**
     * Retorna informações detalhadas do tipo complexo.
     * @return String com informações detalhadas
     */
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Tipo Complexo: ").append(name).append("\n");
        
        if (baseType != null) {
            info.append("Estende: ").append(baseType).append("\n");
        }
        
        if (isAbstract) {
            info.append("Tipo: Abstrato\n");
        }
        
        if (mixed) {
            info.append("Conteúdo: Misto\n");
        }
        
        info.append("Elementos: ").append(elements.size()).append("\n");
        info.append("Atributos: ").append(attributes.size()).append("\n");
        
        if (documentation != null) {
            info.append("\nDocumentação:\n").append(documentation);
        }
        
        return info.toString();
    }
    
    @Override
    public String toString() {
        return getDisplayName() + " (" + elements.size() + " elementos, " + attributes.size() + " atributos)";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        XsdComplexType that = (XsdComplexType) obj;
        return name != null ? name.equals(that.name) : that.name == null;
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}