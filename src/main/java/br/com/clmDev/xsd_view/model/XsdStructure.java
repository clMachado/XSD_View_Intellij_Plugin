package br.com.clmDev.xsd_view.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XsdStructure {
    private List<XsdElement> rootElements = new ArrayList<>();
    private List<XsdComplexType> complexTypes = new ArrayList<>();
    private Map<String, XsdType> typeMap = new HashMap<>();
    private String targetNamespace;
    
    public void addElement(XsdElement element) {
        rootElements.add(element);
    }
    
    public void addComplexType(XsdComplexType complexType) {
        complexTypes.add(complexType);
        typeMap.put(complexType.getName(), complexType);
    }
    
    public List<XsdElement> getRootElements() {
        return rootElements;
    }
    
    public List<XsdComplexType> getComplexTypes() {
        return complexTypes;
    }
    
    public XsdType getType(String typeName) {
        return typeMap.get(typeName);
    }
    
    public String getTargetNamespace() {
        return targetNamespace;
    }
    
    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }
    
    public boolean isEmpty() {
        return rootElements.isEmpty() && complexTypes.isEmpty();
    }
}