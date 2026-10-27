package seedu.address.logic.parser;

import java.util.List;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions. The prefixes that are typed often also have a shorter alias. */
    public static final Prefix PREFIX_NAME = new Prefix("/name", "/n");
    public static final Prefix PREFIX_CLASS = new Prefix("/class", "/c");
    public static final Prefix PREFIX_EMAIL = new Prefix("/email");
    public static final Prefix PREFIX_GROUP = new Prefix("/group");
    public static final Prefix PREFIX_MEMBERS = new Prefix("/members", "/m");
    public static final Prefix PREFIX_DATE = new Prefix("/date");
    public static final Prefix PREFIX_STATUS = new Prefix("/status", "/st");
    public static final Prefix PREFIX_ASSESSMENT = new Prefix("/assessment", "/as");
    public static final Prefix PREFIX_SCORE = new Prefix("/score", "/sc");

    /** Every prefix that any command accepts. Add a new prefix here as well as above. */
    public static final List<Prefix> ALL_PREFIXES = List.of(PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL,
            PREFIX_GROUP, PREFIX_MEMBERS, PREFIX_DATE, PREFIX_STATUS, PREFIX_ASSESSMENT, PREFIX_SCORE);

}
