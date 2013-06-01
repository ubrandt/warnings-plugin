package hudson.plugins.warnings.parser;

import java.util.regex.Matcher;

import hudson.Extension;

import hudson.plugins.analysis.util.model.Priority;

/**
 * FIXME: Document type PerlCriticParser.
 *
 * @author Mihail Menev, menev@hm.edu
 */
@Extension
public class PerlCriticParser extends RegexpLineParser {

    private static final String PERLCRITIC_WARNING_PATTERN = "(?:(^/.*?):)?(.*)\\s+at\\s+line\\s+(\\d+),\\s+column\\s+(\\d+)\\.\\s*(?:See page[s]?\\s+)?(.*)\\.\\s*\\(?Severity:\\s*(\\d)\\)?";

    /** {@inheritDoc} */
    @Override
    protected Warning createWarning(final Matcher matcher) {
        int offset = 0;
        String filename = "-";

        if (matcher.groupCount() == 6) {
            offset = 1;
            filename = matcher.group(offset);
        }

        String message = matcher.group(1 + offset);
        String category = matcher.group(4 + offset);
        int line = Integer.parseInt(matcher.group(2 + offset));
        int column = Integer.parseInt(matcher.group(3 + offset));
        Priority priority = checkPriority(Integer.parseInt(matcher.group(5 + offset)));

        Warning warning = createWarning(filename, line, category, message, priority);
        warning.setColumnPosition(column, column);
        return warning;
    }

    /**
     * Checks the severity level, parsed from the warning and return the priority level.
     *
     * @param priority
     *            the severity level of the warning.
     * @return the priority level.
     */
    private Priority checkPriority(final int priority) {
        if (priority < 2) {
            return Priority.LOW;
        }
        else if (priority < 4) {
            return Priority.NORMAL;
        }
        else {
            return Priority.HIGH;
        }
    }

    /**
     * Creates a new instance of {@link PerlCriticParser}.
     */
    public PerlCriticParser() {
        super(Messages._Warnings_PerlCritic_ParserName(), Messages._Warnings_PerlCritic_LinkName(), Messages
                ._Warnings_PerlCritic_TrendName(), PERLCRITIC_WARNING_PATTERN, true);
    }

}
