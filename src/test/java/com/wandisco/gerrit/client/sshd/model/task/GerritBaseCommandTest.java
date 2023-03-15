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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GerritBaseCommandTest {

    @Test
    void testConstructor() {
        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand();
        String command = "Command";
        actualGerritBaseCommand.setCommand(command);
        boolean isComplete = true;
        actualGerritBaseCommand.setComplete(isComplete);
        int exitCode = 1;
        actualGerritBaseCommand.setExitCode(exitCode);
        boolean isSuccessful = true;
        actualGerritBaseCommand.setSuccessful(isSuccessful);
        boolean isTimedOut = true;
        actualGerritBaseCommand.setTimedOut(isTimedOut);
        String actualToStringResult = actualGerritBaseCommand.toString();

        assertEquals(1, actualGerritBaseCommand.getExitCode());
        List<String> expectedOutputList = actualGerritBaseCommand.commandOutputLines;
        assertSame(expectedOutputList, actualGerritBaseCommand.getOutputList());
        assertTrue(actualGerritBaseCommand.isComplete());
        assertTrue(actualGerritBaseCommand.isSuccessful());
        assertTrue(actualGerritBaseCommand.isTimedOut());
        assertEquals("Command", actualToStringResult);
    }

    @Test
    void testConstructor2() {

        String command = "Command";

        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand(command);
        String command1 = "Command";
        actualGerritBaseCommand.setCommand(command1);
        boolean isComplete = true;
        actualGerritBaseCommand.setComplete(isComplete);
        int exitCode = 1;
        actualGerritBaseCommand.setExitCode(exitCode);
        boolean isSuccessful = true;
        actualGerritBaseCommand.setSuccessful(isSuccessful);
        boolean isTimedOut = true;
        actualGerritBaseCommand.setTimedOut(isTimedOut);
        String actualToStringResult = actualGerritBaseCommand.toString();

        assertEquals(1, actualGerritBaseCommand.getExitCode());
        List<String> expectedOutputList = actualGerritBaseCommand.commandOutputLines;
        assertSame(expectedOutputList, actualGerritBaseCommand.getOutputList());
        assertTrue(actualGerritBaseCommand.isComplete());
        assertTrue(actualGerritBaseCommand.isSuccessful());
        assertTrue(actualGerritBaseCommand.isTimedOut());
        assertEquals("Command", actualToStringResult);
    }

    @Test
    void testConstructor3() {
        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand();

        assertEquals("", actualGerritBaseCommand.getCommand());
        assertFalse(actualGerritBaseCommand.isTimedOut());
        assertFalse(actualGerritBaseCommand.isSuccessful());
        assertFalse(actualGerritBaseCommand.isComplete());
        assertTrue(actualGerritBaseCommand.getOutputList().isEmpty());
    }

    @Test
    void testConstructor4() {
        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand();
        Duration timeout = Duration.ofSeconds(30);
        actualGerritBaseCommand.setTimeout(timeout);

        assertEquals(actualGerritBaseCommand.getTimeout(), Duration.ofSeconds(30));
    }

    @Test
    void testConstructor5() {
        String command = "Command";
        Duration timeout = Duration.ofSeconds(10);

        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand(command, timeout);

        assertEquals("Command", actualGerritBaseCommand.getCommand());
        assertFalse(actualGerritBaseCommand.isTimedOut());
        assertFalse(actualGerritBaseCommand.isSuccessful());
        assertFalse(actualGerritBaseCommand.isComplete());
        assertEquals(timeout, actualGerritBaseCommand.getDuration());
        assertTrue(actualGerritBaseCommand.getOutputList().isEmpty());
    }

    @Test
    void testConstructor6() {
        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand();
        List<String> expectedOutputList = actualGerritBaseCommand.commandOutputLines;
        assertSame(expectedOutputList, actualGerritBaseCommand.getOutputList());
    }

    @Test
    void testConstructor7() {
        GerritBaseCommand actualGerritBaseCommand = new GerritBaseCommand();
        assertNull(actualGerritBaseCommand.getOutput());
    }

    @Test
    void testStart() {
        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");

        gerritBaseCommand.start();

        assertNull(gerritBaseCommand.getDuration());
    }

    @Test
    void testEnd() {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");

        gerritBaseCommand.end();

        assertTrue(gerritBaseCommand.isComplete());
    }

    @Test
    void testGetDuration() {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");

        Duration actualDuration = gerritBaseCommand.getDuration();

        assertNull(actualDuration);
    }

    @Test
    void testGetCommand() {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");

        String actualCommand = gerritBaseCommand.getCommand();

        assertEquals("Command", actualCommand);
    }

    @Test
    void testGetCommand2() {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");
        gerritBaseCommand.setCommand(null);

        String actualCommand = gerritBaseCommand.getCommand();

        assertEquals("", actualCommand);
    }

    @Test
    void testSetOutput() throws IOException {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");
        String output = "Output";

        gerritBaseCommand.setOutput(output);

        List<String> outputList = gerritBaseCommand.getOutputList();
        assertEquals(1, outputList.size());
        assertEquals("Output", outputList.get(0));
        assertEquals("Output", gerritBaseCommand.getOutput());
    }

    @Test
    void testSetOutput2() throws IOException {
        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");
        gerritBaseCommand.setOutput(null);
        assertTrue(gerritBaseCommand.getOutput().isEmpty());
        assertTrue(gerritBaseCommand.getOutputList().isEmpty());
    }

    @Test
    void testSetOutputLines() {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");
        ArrayList<String> stringList = new ArrayList<>();

        gerritBaseCommand.setOutputLines(stringList);

        assertSame(stringList, gerritBaseCommand.getOutputList());
        assertEquals("", gerritBaseCommand.getOutput());
    }

    @Test
    void testSetOutputLines2() {

        GerritBaseCommand gerritBaseCommand = new GerritBaseCommand("Command");

        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("foo");

        gerritBaseCommand.setOutputLines(stringList);

        assertSame(stringList, gerritBaseCommand.getOutputList());
        assertEquals("foo", gerritBaseCommand.getOutput());
    }
}

