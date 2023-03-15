package com.wandisco.gerrit.client.sshd;

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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.model.git.GerritRepo;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import com.wandisco.gerrit.client.sshd.model.task.GerritBaseCommand;
import com.wandisco.gerrit.client.sshd.testutils.DummyCommandRunner;
import com.wandisco.gerrit.client.sshd.testutils.SshTestBase;
import java.io.File;
import java.nio.file.Path;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.git.pack.GitPackCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.apache.sshd.server.shell.UnknownCommandFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GerritSSHClientTest extends SshTestBase {

    private static SshServer sshd;
    private static Path localRootDir;
    private static Path serverDir;
    private static Path localDir;

    private GerritSSHServer currentServer;

    public GerritSSHClientTest() {
        super();
    }

    @BeforeAll
    public static void setupClientAndServer() throws Exception {
        sshd = getGitSshServer();
        sshd.start();
        localRootDir = gitRootDir.resolve("local");
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
        sshd.setKeyboardInteractiveAuthenticator(KeyboardInteractiveAuthenticator.NONE);
        sshd.setCommandFactory(new UnknownCommandFactory());
        currentServer = getLocalSSHServer(sshd);
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
    }

    @Test
    void testConstructor() {
        GerritSSHClient actualGerritSSHClient = assertDoesNotThrow(() -> new GerritSSHClient(currentServer));
        assertEquals(currentServer, actualGerritSSHClient.getServer());

    }

    @Test
    void testExecuteCommand() {
        String expectedCommand = "test-command";
        String expectedResponse = "Output of: " + expectedCommand;
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn)));
        GerritSSHClient actualGerritSSHClient = assertDoesNotThrow(() -> new GerritSSHClient(currentServer));
        GerritBaseCommand command = assertDoesNotThrow(() -> actualGerritSSHClient.executeCommand(expectedCommand));
        assertAll(() -> assertEquals(expectedCommand, command.getCommand()), () -> assertEquals(expectedResponse, command.getOutput()),
            () -> assertTrue(command.isSuccessful()), () -> assertFalse(command.isTimedOut()), () -> assertTrue(command.isComplete()));
    }

    @Test
    void testExecuteCommandObj() {
        String expectedCommand = "test-command";
        GerritBaseCommand input = new GerritBaseCommand(expectedCommand);
        String expectedResponse = "Output of: " + expectedCommand;
        sshd.setCommandFactory(((channelSession, commandIn) -> new DummyCommandRunner(commandIn)));
        GerritSSHClient actualGerritSSHClient = assertDoesNotThrow(() -> new GerritSSHClient(currentServer));
        assertDoesNotThrow(() -> actualGerritSSHClient.executeCommand(input));
        assertAll(() -> assertEquals(expectedCommand, input.getCommand()), () -> assertEquals(expectedResponse, input.getOutput()),
            () -> assertTrue(input.isSuccessful()), () -> assertFalse(input.isTimedOut()), () -> assertTrue(input.isComplete()));
    }

    @Test
    void testCloneRepoTarget() throws GitAPIException {
        sshd.setCommandFactory(new GitPackCommandFactory(GitLocationResolver.constantPath(serverRootDir)));
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
        GerritSSHClient actualGerritSSHClient = assertDoesNotThrow(() -> new GerritSSHClient(currentServer));
        try (Git repo = actualGerritSSHClient.cloneRepo(serverDir.getFileName().toString(), localDir.toFile())) {
            assertAll(() -> assertInstanceOf(GerritRepo.class, repo),
                () -> assertEquals(localDir.toFile(), repo.getRepository().getDirectory().getParentFile()),
                () -> assertTrue(localDir.toFile().exists()));
        }
    }

    @Test
    void testPush() throws GitAPIException {
        sshd.setCommandFactory(new GitPackCommandFactory(GitLocationResolver.constantPath(serverRootDir)));
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
        GerritSSHClient actualGerritSSHClient = assertDoesNotThrow(() -> new GerritSSHClient(currentServer));
        try (Git repo = actualGerritSSHClient.cloneRepo(serverDir.getFileName().toString(), localDir.toFile())) {
            assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("master")));
            assertDoesNotThrow(() -> repo.commit().setMessage("First Commit").setCommitter("testPush", "sshd@apache.org").call());
            assertDoesNotThrow(() -> repo.push().call());
            assertAll(() -> assertTrue(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))));
        }
    }

    @Test
    @Disabled
    void testGetCommitMsgHook() throws GitAPIException {
        sshd.setCommandFactory(new GitPackCommandFactory(GitLocationResolver.constantPath(serverRootDir)));
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
        GerritSSHClient actualGerritSSHClient = assertDoesNotThrow(() -> new GerritSSHClient(currentServer));
        try (Git repo = actualGerritSSHClient.cloneRepo(serverDir.getFileName().toString(), localDir.toFile())) {
            File hook = new File(repo.getRepository().getDirectory(), "hooks/commit-msg");
            if (hook.exists()) {
                assertTrue(hook.delete());
            }
            assertDoesNotThrow(() -> actualGerritSSHClient.getCommitMsgHook(repo));
            assertTrue(hook.exists());
        }
    }
}

