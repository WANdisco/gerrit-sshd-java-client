package com.wandisco.gerrit.client.sshd.commands;

/*-
 * #%L
 * gerrit-sshd-java-client
 * %%
 * Copyright (C) 2021 - 2023 WANdisco
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.wandisco.gerrit.client.sshd.model.task.CommandOption;
import com.wandisco.gerrit.client.sshd.model.task.GerritBaseCommand;

/**
 * <p>
 * Parent class for all Gerrit Commands prefix of gerrit added to all commands Methods to ensure last param of a command is always at the
 * end common methods for adding new parameters to the command.
 */
public abstract class GerritCommand extends GerritBaseCommand {

    private static final String GERRIT_COMMAND_BASE = "gerrit";
    private static final String PARAM_FORMAT_STRING = "%s %s ";
    private final String commandMethod;
    private String commandLastParam;

    /**
     * Constructor for commands that do not take parameters.
     * <p>
     * Method will construct the complete command by prefixing gerrit to the start of it
     *
     * @param commandBeginning Start of the command after gerrit
     */
    public GerritCommand(String commandBeginning) {
        commandMethod = commandBeginning;
        commandBuilder.append(String.format(PARAM_FORMAT_STRING, GERRIT_COMMAND_BASE, commandMethod));
    }

    /**
     * Constructor for commands that have a parameter that must always be last
     * <p>
     * such as a username or project name Method will construct the complete command by prefixing gerrit to the start of it and ensure the
     * last param is always the one provided.
     *
     * @param commandBeginning Start of the command after gerrit
     * @param commandEnd Parameter that is required as last option on the command
     */
    public GerritCommand(String commandBeginning, String commandEnd) {
        commandMethod = commandBeginning;
        commandLastParam = commandEnd;
        commandBuilder.append(String.format(PARAM_FORMAT_STRING, GERRIT_COMMAND_BASE, commandMethod));
    }

    /**
     * Overrides {@link GerritBaseCommand#getCommand()}  to ensure that the last variable of the command is always on the end
     *
     * @return returns a string of the command to be executed
     */
    @Override
    public String getCommand() {
        if (!commandBuilder.toString().endsWith(getCommandEnd())) {
            commandBuilder.append(getCommandEnd());
        }
        return commandBuilder.toString();
    }

    /**
     * Getter for the command that is to be run for example ls-projects
     *
     * @return Value set as the Command to be executed on gerrit
     */
    protected String getCommandMethod() {
        return commandMethod;
    }

    /**
     * Getter for the last parameter of the command if present for example the project name of create project
     *
     * @return Value set as the last parameter of the command
     */
    protected String getCommandEnd() {
        return commandLastParam;
    }

    /**
     * Method for appending a new flag to the current command
     *
     * @param option extension of {@link CommandOption} to get the actual commandline option from
     */
    protected void addOption(CommandOption option) {
        commandBuilder.append(String.format("%s ", option.getFlag()));
    }

    /**
     * Method for appending a new flag with a parameter to the current command
     *
     * @param option extension of {@link CommandOption} to get the actual command line option from
     * @param param String parameter to be added with the command line option
     */
    protected void addOption(CommandOption option, String param) {
        commandBuilder.append(String.format(PARAM_FORMAT_STRING, option.getFlag(), param));
    }

    /**
     * Method for appending a new flag with a parameter that must be wrapped in single quotes for example ssh-keys, users full names etc.
     *
     * @param option extension of {@link CommandOption} to get the actual command line option from
     * @param param String to be added to the command line option and will be wrapped in single quotes
     */
    protected void addDescriptionFlag(CommandOption option, String param) {
        commandBuilder.append(String.format(PARAM_FORMAT_STRING, option.getFlag(), String.format("'%s'", param)));
    }
}
