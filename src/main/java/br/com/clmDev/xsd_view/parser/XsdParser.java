package br.com.clmDev.xsd_view.parser;

import br.com.clmDev.xsd_view.model.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class XsdParser {
    private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private Set<Element> processedElements = new HashSet<>();

    public XsdStructure parseXsd(VirtualFile file) {
        InputStream inputStream = null;
        try {
            // Reset processed elements for each parse
            processedElements.clear();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = file.getInputStream();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            return buildStructure(root);

        } catch (javax.xml.parsers.FactoryConfigurationError e) {
            return parseWithFallback(file);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer parse do XSD: " + e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // Ignorar erro ao fechar
                }
            }
        }
    }

    private XsdStructure parseWithFallback(VirtualFile file) {
        try {
            processedElements.clear();
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                    "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            DocumentBuilder builder = factory.newDocumentBuilder();

            try (InputStream inputStream = file.getInputStream()) {
                Document doc = builder.parse(inputStream);
                Element root = doc.getDocumentElement();
                return buildStructure(root);
            }

        } catch (Exception fallbackException) {
            return parseSimple(file);
        }
    }

    private XsdStructure parseSimple(VirtualFile file) {
        try {
            processedElements.clear();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            try (InputStream inputStream = file.getInputStream()) {
                Document doc = builder.parse(inputStream);
                Element root = doc.getDocumentElement();
                return buildStructure(root);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro crítico ao fazer parse do XSD. " +
                    "Verifique se o arquivo XSD está bem formado: " + e.getMessage(), e);
        }
    }

    private XsdStructure buildStructure(Element root) {
        XsdStructure structure = new XsdStructure();

        String targetNamespace = root.getAttribute("targetNamespace");
        structure.setTargetNamespace(targetNamespace);

        // Processar apenas elementos que são filhos diretos do root
        processDirectChildElements(root, structure);

        // Processar apenas tipos complexos que são filhos diretos do root
        processDirectChildComplexTypes(root, structure);

        return structure;
    }

    private void processDirectChildElements(Element root, XsdStructure structure) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                if (XSD_NAMESPACE.equals(element.getNamespaceURI()) && 
                    "element".equals(element.getLocalName()) &&
                    !processedElements.contains(element)) {
                    
                    processedElements.add(element);
                    XsdElement xsdElement = parseElement(element);
                    xsdElement.setRoot(true);
                    structure.addElement(xsdElement);
                }
            }
        }
    }

    private void processDirectChildComplexTypes(Element root, XsdStructure structure) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                if (XSD_NAMESPACE.equals(element.getNamespaceURI()) && 
                    "complexType".equals(element.getLocalName()) &&
                    !processedElements.contains(element)) {
                    
                    processedElements.add(element);
                    XsdComplexType xsdComplexType = parseComplexType(element);
                    structure.addComplexType(xsdComplexType);
                }
            }
        }
    }

    private XsdElement parseElement(Element element) {
        String name = element.getAttribute("name");
        String type = element.getAttribute("type");
        String minOccurs = element.getAttribute("minOccurs");
        String maxOccurs = element.getAttribute("maxOccurs");

        XsdElement xsdElement = new XsdElement(name);

        if (!type.isEmpty()) {
            xsdElement.setType(type);
        }

        if (!minOccurs.isEmpty()) {
            try {
                xsdElement.setMinOccurs(Integer.parseInt(minOccurs));
            } catch (NumberFormatException e) {
                xsdElement.setMinOccurs(1);
            }
        }

        if (!maxOccurs.isEmpty()) {
            xsdElement.setMaxOccurs(maxOccurs);
        }

        // Processar documentação
        processDocumentation(element, xsdElement);

        // Processar tipo complexo inline - apenas filhos diretos
        processInlineComplexType(element, xsdElement);

        return xsdElement;
    }

    private void processDocumentation(Element element, XsdElement xsdElement) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "annotation".equals(childElement.getLocalName())) {
                    
                    NodeList docChildren = childElement.getChildNodes();
                    for (int j = 0; j < docChildren.getLength(); j++) {
                        Node docChild = docChildren.item(j);
                        if (docChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element docElement = (Element) docChild;
                            if (XSD_NAMESPACE.equals(docElement.getNamespaceURI()) && 
                                "documentation".equals(docElement.getLocalName())) {
                                String docText = docElement.getTextContent();
                                if (docText != null) {
                                    xsdElement.setDocumentation(docText.trim());
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void processInlineComplexType(Element element, XsdElement xsdElement) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "complexType".equals(childElement.getLocalName()) &&
                    !processedElements.contains(childElement)) {
                    
                    processedElements.add(childElement);
                    parseComplexTypeContent(childElement, xsdElement);
                    break;
                }
            }
        }
    }

    private XsdComplexType parseComplexType(Element complexTypeElement) {
        String name = complexTypeElement.getAttribute("name");
        XsdComplexType complexType = new XsdComplexType(name);

        parseComplexTypeContent(complexTypeElement, complexType);

        return complexType;
    }

    private void parseComplexTypeContent(Element complexTypeElement, XsdComplexType complexType) {
        processSequences(complexTypeElement, complexType);
        processChoices(complexTypeElement, complexType);
        processAttributes(complexTypeElement, complexType);
    }

    private void parseComplexTypeContent(Element complexTypeElement, XsdElement parentElement) {
        processSequences(complexTypeElement, parentElement);
        processChoices(complexTypeElement, parentElement);
        processAttributes(complexTypeElement, parentElement);
    }

    private void processSequences(Element parent, XsdComplexType complexType) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "sequence".equals(childElement.getLocalName())) {
                    parseSequence(childElement, complexType);
                }
            }
        }
    }

    private void processSequences(Element parent, XsdElement parentElement) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "sequence".equals(childElement.getLocalName())) {
                    parseSequence(childElement, parentElement);
                }
            }
        }
    }

    private void processChoices(Element parent, XsdComplexType complexType) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "choice".equals(childElement.getLocalName())) {
                    parseChoice(childElement, complexType);
                }
            }
        }
    }

    private void processChoices(Element parent, XsdElement parentElement) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "choice".equals(childElement.getLocalName())) {
                    parseChoice(childElement, parentElement);
                }
            }
        }
    }

    private void processAttributes(Element parent, XsdComplexType complexType) {
        processDirectAttributes(parent, complexType);
        processAttributesInExtensions(parent, complexType);
    }

    private void processAttributes(Element parent, XsdElement parentElement) {
        processDirectAttributes(parent, parentElement);
        processAttributesInExtensions(parent, parentElement);
    }

    private void processDirectAttributes(Element parent, XsdComplexType complexType) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "attribute".equals(childElement.getLocalName())) {
                    XsdAttribute xsdAttr = parseAttribute(childElement);
                    complexType.addAttribute(xsdAttr);
                }
            }
        }
    }

    private void processDirectAttributes(Element parent, XsdElement parentElement) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "attribute".equals(childElement.getLocalName())) {
                    XsdAttribute xsdAttr = parseAttribute(childElement);
                    parentElement.addAttribute(xsdAttr);
                }
            }
        }
    }

    private void processAttributesInExtensions(Element parent, XsdComplexType complexType) {
        // Processar atributos em complexContent/extension, simpleContent/extension, etc.
        processAttributesInPath(parent, complexType, "complexContent", "extension");
        processAttributesInPath(parent, complexType, "simpleContent", "extension");
        processAttributesInPath(parent, complexType, "complexContent", "restriction");
        processAttributesInPath(parent, complexType, "simpleContent", "restriction");
    }

    private void processAttributesInExtensions(Element parent, XsdElement parentElement) {
        // Processar atributos em complexContent/extension, simpleContent/extension, etc.
        processAttributesInPath(parent, parentElement, "complexContent", "extension");
        processAttributesInPath(parent, parentElement, "simpleContent", "extension");
        processAttributesInPath(parent, parentElement, "complexContent", "restriction");
        processAttributesInPath(parent, parentElement, "simpleContent", "restriction");
    }

    private void processAttributesInPath(Element parent, XsdComplexType complexType, String contentType, String derivationType) {
        NodeList contentNodes = parent.getChildNodes();
        for (int i = 0; i < contentNodes.getLength(); i++) {
            Node contentNode = contentNodes.item(i);
            if (contentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element contentElement = (Element) contentNode;
                if (XSD_NAMESPACE.equals(contentElement.getNamespaceURI()) && 
                    contentType.equals(contentElement.getLocalName())) {
                    
                    NodeList derivationNodes = contentElement.getChildNodes();
                    for (int j = 0; j < derivationNodes.getLength(); j++) {
                        Node derivationNode = derivationNodes.item(j);
                        if (derivationNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element derivationElement = (Element) derivationNode;
                            if (XSD_NAMESPACE.equals(derivationElement.getNamespaceURI()) && 
                                derivationType.equals(derivationElement.getLocalName())) {
                                
                                processDirectAttributes(derivationElement, complexType);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processAttributesInPath(Element parent, XsdElement parentElement, String contentType, String derivationType) {
        NodeList contentNodes = parent.getChildNodes();
        for (int i = 0; i < contentNodes.getLength(); i++) {
            Node contentNode = contentNodes.item(i);
            if (contentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element contentElement = (Element) contentNode;
                if (XSD_NAMESPACE.equals(contentElement.getNamespaceURI()) && 
                    contentType.equals(contentElement.getLocalName())) {
                    
                    NodeList derivationNodes = contentElement.getChildNodes();
                    for (int j = 0; j < derivationNodes.getLength(); j++) {
                        Node derivationNode = derivationNodes.item(j);
                        if (derivationNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element derivationElement = (Element) derivationNode;
                            if (XSD_NAMESPACE.equals(derivationElement.getNamespaceURI()) && 
                                derivationType.equals(derivationElement.getLocalName())) {
                                
                                processDirectAttributes(derivationElement, parentElement);
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseSequence(Element sequence, XsdComplexType complexType) {
        NodeList children = sequence.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) &&
                    "element".equals(childElement.getLocalName()) &&
                    !processedElements.contains(childElement)) {
                    
                    processedElements.add(childElement);
                    XsdElement element = parseElement(childElement);
                    complexType.addElement(element);
                }
            }
        }
    }

    private void parseSequence(Element sequence, XsdElement parentElement) {
        NodeList children = sequence.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) &&
                    "element".equals(childElement.getLocalName()) &&
                    !processedElements.contains(childElement)) {
                    
                    processedElements.add(childElement);
                    XsdElement element = parseElement(childElement);
                    parentElement.addChild(element);
                }
            }
        }
    }

    private void parseChoice(Element choice, XsdComplexType complexType) {
        parseSequence(choice, complexType);
    }

    private void parseChoice(Element choice, XsdElement parentElement) {
        parseSequence(choice, parentElement);
    }

    private XsdAttribute parseAttribute(Element attrElement) {
        String name = attrElement.getAttribute("name");
        String type = attrElement.getAttribute("type");
        String use = attrElement.getAttribute("use");
        String defaultValue = attrElement.getAttribute("default");
        String fixedValue = attrElement.getAttribute("fixed");

        XsdAttribute attribute = new XsdAttribute(name);

        if (!type.isEmpty()) {
            attribute.setType(type);
        }

        if (!use.isEmpty()) {
            attribute.setUse(use);
        } else {
            attribute.setUse("optional");
        }

        if (!defaultValue.isEmpty()) {
            attribute.setDefaultValue(defaultValue);
        }

        if (!fixedValue.isEmpty()) {
            attribute.setFixedValue(fixedValue);
        }

        // Processar documentação do atributo
        processAttributeDocumentation(attrElement, attribute);

        return attribute;
    }

    private void processAttributeDocumentation(Element attrElement, XsdAttribute attribute) {
        NodeList children = attrElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (XSD_NAMESPACE.equals(childElement.getNamespaceURI()) && 
                    "annotation".equals(childElement.getLocalName())) {
                    
                    NodeList docChildren = childElement.getChildNodes();
                    for (int j = 0; j < docChildren.getLength(); j++) {
                        Node docChild = docChildren.item(j);
                        if (docChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element docElement = (Element) docChild;
                            if (XSD_NAMESPACE.equals(docElement.getNamespaceURI()) && 
                                "documentation".equals(docElement.getLocalName())) {
                                String docText = docElement.getTextContent();
                                if (docText != null) {
                                    attribute.setDocumentation(docText.trim());
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}