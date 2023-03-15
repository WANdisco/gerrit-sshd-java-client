package com.wandisco.gerrit.client.sshd.model.task;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for wrapping a command executed, providing some helper methods to check progress, completion and time a task started/ended.
 */
public class GerritBaseCommand {

    //TODO: add getters and setters for stdout and stderr separately

    protected final StringBuilder commandBuilder = new StringBuilder();
    protected List<String> commandOutputLines = new LinkedList<>();
    private String command;
    private Instant timeStarted;
    private Instant timeEnded;
    private Duration duration;
    private boolean isComplete = false;
    private boolean isSuccessful = false;
    private boolean isTimedOut = false;
    private int exitCode;

    private Duration timeout = Duration.ofMinutes(3);

    private String output;

    public GerritBaseCommand() {
    }

    /**
     * Basic constructor takes a String to be executed on the SSH Session
     *
     * @param command Command to be executed on the gerrit ssh channel, should not include connection details
     */
    public GerritBaseCommand(String command) {
        this.command = command;
    }

    public GerritBaseCommand(String command, Duration timeout) {
        this.command = command;
        this.duration = timeout;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    /**
     * Method called to store start time of the command
     */
    public void start() {
        this.timeStarted = Instant.now();
    }

    /**
     * Method called to set the end time of the command and set that the command is complete
     */
    public void end() {
        this.timeEnded = Instant.now();
        setComplete(true);
    }

    /**
     * Method called to set the end time of the command and set if the command is complete
     *
     * @param completed boolean for the {@link #isComplete}
     */
    public void end(boolean completed) {
        this.timeEnded = Instant.now();
        setComplete(completed);
    }

    /**
     * Return an {@link Instant} of time command start was called
     *
     * @return {@link Instant} of time command start was called
     */
    public Instant getTimeStarted() {
        return timeStarted;
    }

    /**
     * Return an {@link Instant} of time command end was called
     *
     * @return {@link Instant} of time command end was called
     */
    public Instant getTimeEnded() {
        return timeEnded;
    }

    /**
     * If both {@link #timeStarted} and {@link #timeEnded} are set will return the duration
     *
     * @return {@link Duration} of the time between {@link #timeStarted} and {@link #timeEnded}
     */
    public Duration getDuration() {
        if (timeStarted != null && timeEnded != null) {
            duration = Duration.between(timeStarted, timeEnded);
        }
        return duration;
    }

    /**
     * Returns current value of {@link #command} or if it is not set current content of {@link #commandBuilder}
     *
     * @return String of the command to be executed
     */
    public String getCommand() {
        if (command != null) {
            return command;
        } else {
            return commandBuilder.toString();
        }
    }

    /**
     * Set or override the command to be executed
     *
     * @param command String to be used as part of command execution
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Get current Output of the command
     *
     * @return String of the command output, if multiple lines will contain line ends
     */
    public String getOutput() {
        return output;
    }

    /**
     * Set the output of the command from string if multiple lines will also update the {@link #commandOutputLines} list
     *
     * @param commandOutput String output of the command
     * @throws IOException Thrown due to issues reading lines to list.
     */
    public void setOutput(String commandOutput) throws IOException {
        output = commandOutput;
        if (output == null) {
            output = "";
        }
        BufferedReader reader = new BufferedReader(new StringReader(output));
        String current;
        while ((current = reader.readLine()) != null) {
            commandOutputLines.add(current);
        }
        reader.close();
    }

    /**
     * getter for the value of {@link #isComplete}
     *
     * @return returns current value of is complete
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Setter for value {@link #isComplete}
     *
     * @param isComplete boolean of if the command is complete or not
     */
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * Getter for the value of {@link #isSuccessful}
     *
     * @return boolean to indicate command was successfully executed
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * setter for the value of {@link #isSuccessful}
     *
     * @param isSuccessful set to indicate command was successfully executed
     */
    public void setSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    /**
     * Getter for the value of {@link #isTimedOut}
     *
     * @return boolean to indicate command Timed out during execution
     */
    public boolean isTimedOut() {
        return isTimedOut;
    }

    /**
     * Setter for the value of {@link #isTimedOut}
     *
     * @param isTimedOut set to indicate command Timed out during execution
     */
    public void setTimedOut(boolean isTimedOut) {
        this.isTimedOut = isTimedOut;
    }

    /**
     * Getter for the value of {@link #exitCode}
     *
     * @return int of command's exit code
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Setter for the value of {@link #exitCode}
     *
     * @param exitCode int of command's exit code
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Setter for the value of {@link #commandOutputLines} Set to store output as an iterable list of strings
     *
     * @param outputLines list of output lines from commands execution
     */
    public void setOutputLines(List<String> outputLines) {
        this.commandOutputLines = outputLines;
        this.output = join(outputLines);
    }

    /**
     * Getter for current content of {@link #commandOutputLines}
     *
     * @return iterable list of strings for the commands output
     */
    public List<String> getOutputList() {
        return commandOutputLines;
    }

    /**
     * private method to convert the List of {@link #commandOutputLines} into a single string
     *
     * @param listOfStrings list of strings to be joined together
     * @return Single string of all entries of listOfStrings
     */
    private String join(List<String> listOfStrings) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = listOfStrings.iterator();

        while (i.hasNext()) {
            sb.append(i.next());

            if (i.hasNext()) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * toString override to print the completed command
     */
    @Override
    public String toString() {
        return getCommand();
    }
}
