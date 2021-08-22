package kd.lzp.servicetools.util.decompiler.jd.printer;


public class PlainTextMetaPrinter extends PlainTextPrinter {

    @Override
    public void printStringConstant(String constant, String ownerInternalName) {
        this.sb.append(constant);
        this.sb.append("<META-STRING ownerInternalName='");
        this.sb.append(ownerInternalName);
        this.sb.append("'/>");
    }


    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        this.sb.append(name);
        this.sb.append("<META-DECLARATION type='");
        printType(type);
        this.sb.append("' internalName='");
        this.sb.append(internalTypeName);
        this.sb.append("' descriptor='");
        this.sb.append(descriptor);
        this.sb.append("'/>");
    }


    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
        this.sb.append(name);
        this.sb.append("<META-REFERENCE type='");
        printType(type);
        this.sb.append("' internalName='");
        this.sb.append((internalTypeName == null) ? "?" : internalTypeName);
        this.sb.append("' descriptor='");
        this.sb.append(descriptor);
        this.sb.append("' ownerInternalName='");
        this.sb.append(ownerInternalName);
        this.sb.append("'/>");
    }


    @Override
    public void startLine(int lineNumber) {
        printLineNumber(lineNumber);

        for (int i = 0; i < this.indentationCount; i++) {
            this.sb.append("  ");
        }
    }


    @Override
    public void extraLine(int count) {
        this.sb.append("<EXTRALINE>");
        while (count-- > 0) {
            printLineNumber(0);
            this.sb.append("\n");
        }
        this.sb.append("</EXTRALINE>");
    }


    @Override
    public void startMarker(int type) {
        this.sb.append("<MARKER type='");
        printMarker(type);
        this.sb.append("'>");
    }


    @Override
    public void endMarker(int type) {
        this.sb.append("</MARKER type='");
        printMarker(type);
        this.sb.append("'>");
    }

    protected void printType(int type) {
        switch (type) {
            case 1:
                this.sb.append("TYPE");
                break;
            case 2:
                this.sb.append("FIELD");
                break;
            case 3:
                this.sb.append("METHOD");
                break;
            case 4:
                this.sb.append("CONSTRUCTOR");
                break;
            case 5:
                this.sb.append("PACKAGE");
                break;
            case 6:
                this.sb.append("MODULE");
                break;
            default:
                break;
        }
    }

    protected void printMarker(int type) {
        switch (type) {
            case 1:
                this.sb.append("COMMENT");
                break;
            case 2:
                this.sb.append("JAVADOC");
                break;
            case 3:
                this.sb.append("ERROR");
                break;
            case 4:
                this.sb.append("IMPORT_STATEMENTS");
                break;
        }
    }
}


/* Location:              D:\Lzpeng723\Desktop\servicetools.jar!\kd\lzp\servicetool\\util\decompiler\jd\printer\PlainTextMetaPrinter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */