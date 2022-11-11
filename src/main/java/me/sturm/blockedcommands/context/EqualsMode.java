package me.sturm.blockedcommands.context;

public enum EqualsMode {

    EQUALS("="),
    WEAK_EQUALS("=="),
    NON_EQUALS("!="),
    MORE(">"),
    LESS("<"),
    NO_MORE_THAN("<="),
    NO_LESS_THAN(">=");

    private final String stringValue;

    EqualsMode(String v) {
        this.stringValue = v;
    }

    public String getStringValue() {
        return stringValue;
    }

}
