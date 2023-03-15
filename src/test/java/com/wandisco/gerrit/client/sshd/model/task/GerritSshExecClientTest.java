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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSShSessionCreator;
import com.wandisco.gerrit.client.sshd.testutils.DummyCommandRunner;
import com.wandisco.gerrit.client.sshd.testutils.SshTestBase;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.shell.UnknownCommandFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GerritSshExecClientTest extends SshTestBase {

    protected static SshServer sshd;
    protected static int port;

    @BeforeAll
    public static void setupClientAndServer() throws Exception {
        sshd = getSshServer();
        sshd.start();
        port = sshd.getPort();
    }

    @AfterAll
    public static void tearDownClientAndServer() throws Exception {
        if (sshd != null) {
            try {
                sshd.stop(true);
            } finally {
                sshd = null;
            }
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        sshd.setCommandFactory(UnknownCommandFactory.INSTANCE);
        currentServer = getLocalSSHServer(sshd);
    }

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new GerritSshExecClient(currentServer));
    }

    @Test
    void testConstructor2() {
        GerritSShSessionCreator sessionCreator = getSessionCreator();
        assertDoesNotThrow(() -> new GerritSshExecClient(sessionCreator));
    }

    @Test
    void testConstructor3() {
        GerritSShSessionCreator sessionCreator = getSessionCreator();
        GerritSshExecClient client = assertDoesNotThrow(() -> new GerritSshExecClient(sessionCreator, "someID"));
        assertEquals("someID", client.getClientID());
    }

    @Test
    void testEmptyCommand() {
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> execClient.executeCommand(""));
        assertEquals("Command may not be null/empty", thrown.getMessage());
    }

    @Test
    void testUnknownCommand() {
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        GerritBaseCommand command = assertDoesNotThrow(() -> execClient.executeCommand("test"));
        assertAll(() -> assertEquals("test", command.getCommand()), () -> assertEquals("Unknown command: test", command.getOutput()),
            () -> assertFalse(command.isSuccessful()), () -> assertFalse(command.isTimedOut()), () -> assertTrue(command.isComplete()));
    }

    @Test
    void testTimeout() {
        String expectedCommand = "test-command";
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        input.setTimeout(Duration.ofSeconds(5));
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn) {
            @Override
            public boolean handleCommandLine(String command) throws Exception {
                Thread.sleep(Duration.ofSeconds(6).toMillis());
                return true;
            }
        }));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        assertDoesNotThrow(() -> execClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertFalse(input.isSuccessful()),
            () -> assertTrue(input.isTimedOut()), () -> assertFalse(input.isComplete()));
    }

    @Test
    void testGoodExitStringInput() {
        String expectedCommand = "test-command";
        String expectedResponse = "Output of: " + expectedCommand;
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn)));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        GerritBaseCommand command = assertDoesNotThrow(() -> execClient.executeCommand(expectedCommand));
        assertAll(() -> assertEquals(expectedCommand, command.getCommand()), () -> assertEquals(expectedResponse, command.getOutput()),
            () -> assertTrue(command.isSuccessful()), () -> assertFalse(command.isTimedOut()), () -> assertTrue(command.isComplete()));
    }

    @Test
    void testGoodExitObjectInput() {
        String expectedCommand = "test-command";
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        String expectedResponse = "Output of: " + expectedCommand;
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn)));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        assertDoesNotThrow(() -> execClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertEquals(expectedResponse, input.getOutput()),
            () -> assertTrue(input.isSuccessful()), () -> assertFalse(input.isTimedOut()), () -> assertTrue(input.isComplete()));
    }

    @Test
    void testBadExitStringInput() {
        String expectedCommand = "test-command";
        String expectedResponse = "Failed to run: " + expectedCommand + ": Forced failure";
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn, true)));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        GerritBaseCommand command = assertDoesNotThrow(() -> execClient.executeCommand(expectedCommand));
        assertAll(() -> assertEquals(expectedCommand, command.getCommand()), () -> assertEquals(expectedResponse, command.getOutput()),
            () -> assertFalse(command.isSuccessful()), () -> assertFalse(command.isTimedOut()), () -> assertTrue(command.isComplete()));
    }

    @Test
    void testBadExitObjectInput() {
        String expectedCommand = "test-command";
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        String expectedResponse = "Failed to run: " + expectedCommand + ": Forced failure";
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn, true)));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        assertDoesNotThrow(() -> execClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertEquals(expectedResponse, input.getOutput()),
            () -> assertFalse(input.isSuccessful()), () -> assertFalse(input.isTimedOut()), () -> assertTrue(input.isComplete()));
    }

    @Test
    void testMultiLineOutput() {
        String expectedCommand = "test-command";
        String expectedOutput = "Output of: test-command \n" + " Another line \n" + " third line";
        List<String> expectedList = Arrays.asList(expectedOutput.split("\n"));
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        input.setTimeout(Duration.ofSeconds(5));
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn) {
            @Override
            public boolean handleCommandLine(String command) throws Exception {
                OutputStream stdout = getOutputStream();
                String stdOut = "Output of: " + command + " \n Another line \n third line";
                stdout.write(stdOut.getBytes(StandardCharsets.UTF_8));
                stdout.flush();
                return true;
            }
        }));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        assertDoesNotThrow(() -> execClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertEquals(expectedOutput, input.getOutput()),
            () -> assertEquals(3, input.getOutputList().size()), () -> assertIterableEquals(expectedList, input.getOutputList()),
            () -> assertTrue(input.isSuccessful()), () -> assertFalse(input.isTimedOut()), () -> assertTrue(input.isComplete()));
    }

    @Test
    void testMultiLineStdErr() {
        String expectedCommand = "test-command";
        String expectedOutput =
            "Error for command 0\n" + "Error for command 1\n" + "Error for command 2\n" + "Failed to run: test-command: failure";
        List<String> expectedList = Arrays.asList(expectedOutput.split("\n"));
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        input.setTimeout(Duration.ofSeconds(5));
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn) {
            @Override
            public boolean handleCommandLine(String command) throws Exception {
                OutputStream stderr = getErrorStream();
                for (int i = 0; i < 3; i++) {
                    String error = "Error for command " + i + "\n";
                    stderr.write(error.getBytes(StandardCharsets.UTF_8));
                }
                stderr.flush();
                throw new Exception("failure");
            }
        }));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        assertDoesNotThrow(() -> execClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertEquals(expectedOutput, input.getOutput()),
            () -> assertEquals(4, input.getOutputList().size()), () -> assertIterableEquals(expectedList, input.getOutputList()),
            () -> assertFalse(input.isSuccessful()), () -> assertFalse(input.isTimedOut()), () -> assertTrue(input.isComplete()));
    }

    @Test
    void testStdOutAndStdErr() {
        String expectedCommand = "test-command";
        String expectedOutput = "Output of: test-command\n" + "Failed to run: test-command: Forced failure";
        List<String> expectedList = Arrays.asList(expectedOutput.split("\n"));
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        input.setTimeout(Duration.ofSeconds(5));
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn) {
            @Override
            public boolean handleCommandLine(String command) throws Exception {
                OutputStream stdout = getOutputStream();
                String stdOut = "Output of: " + command + "\n";
                stdout.write(stdOut.getBytes(StandardCharsets.UTF_8));
                stdout.flush();
                throw new Exception("Forced failure");
            }
        }));
        GerritSshExecClient execClient = new GerritSshExecClient(currentServer);
        assertDoesNotThrow(() -> execClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertEquals(expectedOutput, input.getOutput()),
            () -> assertEquals(2, input.getOutputList().size()), () -> assertIterableEquals(expectedList, input.getOutputList()),
            () -> assertFalse(input.isSuccessful()), () -> assertFalse(input.isTimedOut()), () -> assertTrue(input.isComplete()));
    }
}
