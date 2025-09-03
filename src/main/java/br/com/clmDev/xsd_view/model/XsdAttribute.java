package br.com.clmDev.xsd_view.model;

/**
 * Representa um atributo XSD.
 * Exemplo: <xs:attribute name="id" type="xs:string" use="required"/>
 */
public class XsdAttribute {
    private String name;
    private String type;
    private String use = "optional"; // optional, required, prohibited
    private String defaultValue;
    private String fixedValue;
    private String documentation;
    
    /**
     * Construtor com nome do atributo.
     * @param name Nome do atributo
     */
    public XsdAttribute(String name) {
        this.name = name;
    }
    
    /**
     * Construtor com nome e tipo do atributo.
     * @param name Nome do atributo
     * @param type Tipo do atributo
     */
    public XsdAttribute(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * Retorna o nome do atributo.
     * @return Nome do atributo
     */
    public String getName() {
        return name;
    }
    
    /**
     * Define o nome do atributo.
     * @param name Nome do atributo
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Retorna o tipo do atributo (padrão: string).
     * @return Tipo do atributo
     */
    public String getType() {
        return type != null ? type : "string";
    }
    
    /**
     * Define o tipo do atributo.
     * @param type Tipo do atributo
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Retorna o uso do atributo (optional, required, prohibited).
     * @return Uso do atributo
     */
    public String getUse() {
        return use;
    }
    
    /**
     * Define o uso do atributo.
     * @param use Uso do atributo (optional, required, prohibited)
     */
    public void setUse(String use) {
        this.use = use;
    }
    
    /**
     * Retorna o valor padrão do atributo.
     * @return Valor padrão
     */
    public String getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Define o valor padrão do atributo.
     * @param defaultValue Valor padrão
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Retorna o valor fixo do atributo.
     * @return Valor fixo
     */
    public String getFixedValue() {
        return fixedValue;
    }
    
    /**
     * Define o valor fixo do atributo.
     * @param fixedValue Valor fixo
     */
    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }
    
    /**
     * Retorna a documentação do atributo.
     * @return Documentação
     */
    public String getDocumentation() {
        return documentation;
    }
    
    /**
     * Define a documentação do atributo.
     * @param documentation Documentação
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    /**
     * Verifica se o atributo é obrigatório.
     * @return true se obrigatório
     */
    public boolean isRequired() {
        return "required".equals(use);
    }
    
    /**
     * Verifica se o atributo é opcional.
     * @return true se opcional
     */
    public boolean isOptional() {
        return "optional".equals(use);
    }
    
    /**
     * Verifica se o atributo é proibido.
     * @return true se proibido
     */
    public boolean isProhibited() {
        return "prohibited".equals(use);
    }
    
    /**
     * Retorna informações sobre restrições de valor.
     * @return String com informações de valor
     */
    public String getValueInfo() {
        if (fixedValue != null) {
            return "fixed=" + fixedValue;
        }
        if (defaultValue != null) {
            return "default=" + defaultValue;
        }
        return null;
    }
    
    /**
     * Retorna uma representação completa do atributo para tooltip.
     * @return Informações completas do atributo
     */
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Atributo: ").append(name).append("\n");
        info.append("Tipo: ").append(getType()).append("\n");
        info.append("Uso: ").append(use);
        
        if (isRequired()) {
            info.append(" (obrigatório)");
        }
        
        String valueInfo = getValueInfo();
        if (valueInfo != null) {
            info.append("\n").append(valueInfo);
        }
        
        if (documentation != null) {
            info.append("\nDocumentação: ").append(documentation);
        }
        
        return info.toString();
    }
    
    /**
     * Representação em string do atributo para exibição.
     * Formato: @nome (tipo) [marcadores]
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(name);
        
        if (type != null) {
            sb.append(" (").append(type).append(")");
        }
        
        if (isRequired()) {
            sb.append(" *");
        }
        
        String valueInfo = getValueInfo();
        if (valueInfo != null) {
            sb.append(" [").append(valueInfo).append("]");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        XsdAttribute that = (XsdAttribute) obj;
        return name != null ? name.equals(that.name) : that.name == null;
    }
    
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}