package com.wandisco.gerrit.client.sshd.model.git;

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

import com.wandisco.gerrit.client.sshd.testutils.SshTestBase;
import java.nio.file.Path;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.sshd.server.SshServer;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GerritGitSshClientTest extends SshTestBase {

    protected static SshServer sshd;
    protected static int port;

    private static Path localRootDir;
    private static Path serverDir;
    private static Path localDir;

    @BeforeAll
    public static void setupClientAndServer() throws Exception {
        sshd = getGitSshServer();
        sshd.start();
        port = sshd.getPort();
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
        currentServer = getLocalSSHServer(sshd);
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
    }

    @Test
    void testConstructor() {
        GerritGitSshClient client = new GerritGitSshClient(currentServer);
        assertAll(() -> assertEquals(currentServer, client.getServer()));
    }

    @Test
    void testConstructor2() {
        GerritGitSshClient client = new GerritGitSshClient(getSessionCreator());
        assertAll(() -> assertEquals(currentServer, client.getServer()));
    }

    @Test
    void testConstructor3() {
        String id = RandomStringUtils.randomAlphanumeric(3);
        GerritGitSshClient client = new GerritGitSshClient(getSessionCreator(), id);
        assertAll(() -> assertEquals(currentServer, client.getServer()), () -> assertEquals(id, client.getClientID()));
    }

    @Test
    @Disabled("Clones to working Dir")
    void testGenerateCloneCommand() throws Exception {
        GerritGitSshClient client = new GerritGitSshClient(currentServer);
        CloneCommand cloneCommand = client.generateCloneCommand(serverDir.getFileName().toString());
        GerritRepo git = new GerritRepo(cloneCommand.setDirectory(localDir.toFile()).call().getRepository());
        assertEquals(localDir.toFile(), git.getRepository().getDirectory().getParentFile());
        assertTrue(localDir.toFile().exists());
    }

    @Test
    void testGenerateCloneCommandWithTarget() throws Exception {
        GerritGitSshClient client = new GerritGitSshClient(currentServer);
        CloneCommand cloneCommand = client.generateCloneCommand(serverDir.getFileName().toString(), localDir.toFile());
        GerritRepo git = new GerritRepo(cloneCommand.call().getRepository());
        assertEquals(localDir.toFile(), git.getRepository().getDirectory().getParentFile());
        assertTrue(localDir.toFile().exists());
    }

    @Test
    @Disabled("Clones to working Dir")
    void testCloneRepo() throws GitAPIException {
        GerritGitSshClient client = new GerritGitSshClient(currentServer);
        try (Git repo = client.cloneRepo(serverDir.getFileName().toString())) {
            assertInstanceOf(GerritRepo.class, repo);
        }
    }

    @Test
    void testCloneRepoTarget() throws GitAPIException {
        GerritGitSshClient client = new GerritGitSshClient(currentServer);
        try (Git repo = client.cloneRepo(serverDir.getFileName().toString(), localDir.toFile())) {
            assertAll(() -> assertInstanceOf(GerritRepo.class, repo),
                () -> assertEquals(localDir.toFile(), repo.getRepository().getDirectory().getParentFile()),
                () -> assertTrue(localDir.toFile().exists()));
        }
    }

    @Test
    void testPush() throws GitAPIException {
        GerritGitSshClient client = new GerritGitSshClient(currentServer);
        try (Git repo = client.cloneRepo(serverDir.getFileName().toString(), localDir.toFile())) {
            assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("master")));
            assertDoesNotThrow(() -> repo.commit().setMessage("First Commit").setCommitter("testPush", "sshd@apache.org").call());
            assertDoesNotThrow(() -> repo.push().call());
            assertAll(() -> assertTrue(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))));
        }
    }
}

