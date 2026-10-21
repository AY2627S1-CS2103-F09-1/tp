package seedu.address.logic.parser;

import java.util.List;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("/name");
    public static final Prefix PREFIX_CLASS = new Prefix("/class");
    public static final Prefix PREFIX_EMAIL = new Prefix("/email");
    public static final Prefix PREFIX_TAG = new Prefix("/tag");
    public static final Prefix PREFIX_GROUP = new Prefix("/group");
    public static final Prefix PREFIX_MEMBERS = new Prefix("/members");
    public static final Prefix PREFIX_DATE = new Prefix("/date");
    public static final Prefix PREFIX_STATUS = new Prefix("/status");
    public static final Prefix PREFIX_ASSESSMENT = new Prefix("/assessment");
    public static final Prefix PREFIX_SCORE = new Prefix("/score");

    /** Every prefix that any command accepts. Add a new prefix here as well as above. */
    public static final List<Prefix> ALL_PREFIXES = List.of(PREFIX_NAME, PREFIX_CLASS, PREFIX_EMAIL, PREFIX_TAG,
            PREFIX_GROUP, PREFIX_MEMBERS, PREFIX_DATE, PREFIX_STATUS, PREFIX_ASSESSMENT, PREFIX_SCORE);

}
