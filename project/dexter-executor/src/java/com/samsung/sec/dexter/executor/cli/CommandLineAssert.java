package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.exception.*;

import org.apache.commons.cli.CommandLine;

public class CommandLineAssert {
    public static void assertExclusiveOptions(final CommandLine cmd, final char firstOption,
            final char secondOption) {
        if (cmd.hasOption(firstOption) && cmd.hasOption(secondOption)) {
            throw new InvalidArgumentRuntimeException(
                    "you cannot use option '-" + firstOption + "' and with '-" + secondOption + "'");
        }
    }

    public static void assertMissingMandatoryOptions(CommandLine cmd, char shouldExistOption, char... mandatoryOptions) {
        if (cmd.hasOption(shouldExistOption) == false)
            return;

        checkMissing(cmd, mandatoryOptions);
    }

    public static void assertExclusiveMissingMandatoryOptions(CommandLine cmd, char shouldNotBeOption,
            char... mandatoryOptions) {
        if (cmd.hasOption(shouldNotBeOption))
            return;

        checkMissing(cmd, mandatoryOptions);
    }

    private static void checkMissing(CommandLine cmd, char... mandatoryOptions) {
        boolean isMissing = false;

        StringBuilder errMessage = new StringBuilder("You missed option(s) : ");
        for (char option : mandatoryOptions) {
            if (cmd.hasOption(option) == false) {
                errMessage.append(" -").append(option);
                isMissing = true;
            }
        }

        if (isMissing)
            throw new InvalidArgumentRuntimeException(errMessage.toString());
    }

}
