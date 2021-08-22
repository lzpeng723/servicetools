package kd.lzp.servicetools.util.decompiler.jd.printer;

import org.jd.core.v1.api.printer.Printer;


public class PlainTextPrinter implements Printer {
    protected static final String TAB = "  ";
    protected static final String NEWLINE = "\n";
    protected final StringBuilder sb = new StringBuilder();
    protected int indentationCount;
    protected int realLineNumber = 0;

    protected String format;

    protected boolean escapeUnicodeCharacters;

    public PlainTextPrinter() {
        this.escapeUnicodeCharacters = false;
    }


    public PlainTextPrinter(boolean escapeUnicodeCharacters) {
        this.escapeUnicodeCharacters = escapeUnicodeCharacters;
    }


    public void init() {
        this.sb.setLength(0);
        this.realLineNumber = 0;
        this.indentationCount = 0;
    }


    @Override
    public String toString() {
        return this.sb.toString();
    }


    @Override
    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
        this.indentationCount = 0;

        if (maxLineNumber == 0) {
            this.format = "%4d";
        } else {
            int width = 2;

            while (maxLineNumber >= 10) {
                width++;
                maxLineNumber /= 10;
            }

            this.format = "%" + width + "d";
        }
    }


    @Override
    public void end() {
    }


    @Override
    public void printText(String text) {
        if (this.escapeUnicodeCharacters) {
            for (int i = 0, len = text.length(); i < len; i++) {
                char c = text.charAt(i);

                if (c < '') {
                    this.sb.append(c);
                } else {
                    int h = c >> '\f';

                    this.sb.append("\\u");
                    this.sb.append((char) ((h <= 9) ? (h + 48) : (h + 55)));
                    h = c >> '\b' & 0xF;
                    this.sb.append((char) ((h <= 9) ? (h + 48) : (h + 55)));
                    h = c >> '\004' & 0xF;
                    this.sb.append((char) ((h <= 9) ? (h + 48) : (h + 55)));
                    h = c & 0xF;
                    this.sb.append((char) ((h <= 9) ? (h + 48) : (h + 55)));
                }
            }
        } else {
            this.sb.append(text);
        }
    }


    @Override
    public void printNumericConstant(String constant) {
        this.sb.append(constant);
    }


    @Override
    public void printStringConstant(String constant, String ownerInternalName) {
        printText(constant);
    }

    @Override
    public void printKeyword(String keyword) {
        this.sb.append(keyword);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        printText(name);
    }

    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
        printText(name);
    }

    @Override
    public void indent() {
        this.indentationCount++;
    }

    @Override
    public void unindent() {
        if (this.indentationCount > 0) {
            this.indentationCount--;
        }
    }

    @Override
    public void startLine(int lineNumber) {
        printLineNumber(lineNumber);

        for (int i = 0; i < this.indentationCount; i++) {
            this.sb.append("  ");
        }
    }

    @Override
    public void endLine() {
        this.sb.append("\n");
    }

    @Override
    public void extraLine(int count) {
        while (count-- > 0) {
            printLineNumber(0);
            this.sb.append("\n");
        }
    }

    @Override
    public void startMarker(int type) {
    }

    @Override
    public void endMarker(int type) {
    }

    protected void printLineNumber(int lineNumber) {
        if (lineNumber != 0) {
            this.sb.append("/*");
            this.sb.append(String.format(this.format, lineNumber));
            this.sb.append(" */ ");
        } else {
            this.sb.append("/*    */ ");
        }
    }
}