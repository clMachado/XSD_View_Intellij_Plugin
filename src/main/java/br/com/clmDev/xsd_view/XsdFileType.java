package br.com.clmDev.xsd_view;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class XsdFileType implements FileType {
    public static final XsdFileType INSTANCE = new XsdFileType();

    private XsdFileType() {
    }

    @NotNull
    @Override
    public String getName() {
        return "XSD";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "XML Schema Definition files";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "xsd";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // Usar ícone padrão do IntelliJ se disponível
        return null; // O IntelliJ usará o ícone XML padrão
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null; // Usar detecção automática
    }
}