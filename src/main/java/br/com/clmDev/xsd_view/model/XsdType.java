package br.com.clmDev.xsd_view.model;

/**
 * Interface base para todos os tipos XSD.
 * Implementada por XsdElement, XsdComplexType, XsdSimpleType, etc.
 */
public interface XsdType {
    
    /**
     * Retorna o nome do tipo XSD.
     * @return Nome do tipo
     */
    String getName();
    
    /**
     * Retorna uma representação em string do tipo.
     * @return String representation
     */
    default String getDisplayName() {
        return getName();
    }
}