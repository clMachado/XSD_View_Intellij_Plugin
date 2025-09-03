package br.com.clmDev.xsd_view.model;

import java.util.ArrayList;
import java.util.List;

public class XsdElement implements XsdType {
    private String name;
    private String type;
    private int minOccurs = 1;
    private String maxOccurs = "1";
    private List<XsdElement> children = new ArrayList<>();
    private List<XsdAttribute> attributes = new ArrayList<>();
    private boolean isRoot = false;
    private String documentation;
    
    public XsdElement(String name) {
        this.name = name;
    }
    
    public XsdElement(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type != null ? type : "string";
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getMinOccurs() {
        return minOccurs;
    }
    
    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }
    
    public String getMaxOccurs() {
        return maxOccurs;
    }
    
    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }
    
    public List<XsdElement> getChildren() {
        return children;
    }
    
    public void addChild(XsdElement child) {
        children.add(child);
    }
    
    public List<XsdAttribute> getAttributes() {
        return attributes;
    }
    
    public void addAttribute(XsdAttribute attribute) {
        attributes.add(attribute);
    }
    
    public boolean isRoot() {
        return isRoot;
    }
    
    public void setRoot(boolean root) {
        isRoot = root;
    }
    
    public String getDocumentation() {
        return documentation;
    }
    
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }
    
    public String getOccurrenceString() {
        if (minOccurs == 1 && "1".equals(maxOccurs)) {
            return "";
        }
        return String.format("[%d..%s]", minOccurs, maxOccurs);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) %s", name, getType(), getOccurrenceString());
    }
}