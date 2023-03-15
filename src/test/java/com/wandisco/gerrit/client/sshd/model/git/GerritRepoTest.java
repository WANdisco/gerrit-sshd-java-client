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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.testutils.SshTestBase;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.StreamSupport;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.transport.SshTransport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GerritRepoTest extends SshTestBase {

    private static Path localRootDir;
    private static Path serverDir;
    private static Path localDir;

    protected static SshServer sshd;
    protected static int port;

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
        sshd.setKeyboardInteractiveAuthenticator(KeyboardInteractiveAuthenticator.NONE);
        currentServer = getLocalSSHServer(sshd);
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
    }

    @Test
    void testOpen() throws GitAPIException, IOException {
        Path bareRepo = initNewServerRepo();
        try (GerritRepo gerritRepo = GerritRepo.open(bareRepo.toFile())) {
            assertEquals(bareRepo, gerritRepo.getRepository().getDirectory().toPath());
        }
    }

    @Test
    void testWrap() throws GitAPIException, IOException {
        Path bareRepo = initNewServerRepo();
        try (Git gitObj = Git.open(bareRepo.toFile())) {
            GerritRepo gerritRepo = GerritRepo.wrap(gitObj.getRepository());
            assertEquals(bareRepo, gerritRepo.getRepository().getDirectory().toPath());
            assertEquals(gitObj.getRepository(), gerritRepo.getRepository());
        }
    }

    @Test
    void testConstructor() throws GitAPIException, IOException {
        Path bareRepo = initNewServerRepo();
        try (Git gitObj = Git.open(bareRepo.toFile())) {
            GerritRepo gerritRepo = new GerritRepo(gitObj.getRepository());
            assertEquals(gitObj.getRepository(), gerritRepo.getRepository());
        }
    }

    @Test
    void testConstructor2() throws GitAPIException, IOException {
        Path bareRepo = initNewServerRepo();
        try (Git gitObj = Git.open(bareRepo.toFile())) {
            GerritSshdSessionFactory factory = getGerritSshdSessionFactory();
            GerritRepo gerritRepo = new GerritRepo(gitObj.getRepository(), factory);
            assertEquals(gitObj.getRepository(), gerritRepo.getRepository());
            assertEquals(factory, gerritRepo.getSshFactory());
        }
    }

    @Test
    void testSetSshFactory() throws IOException {
        try (Git gitObj = Git.open(serverDir.toFile())) {
            GerritSshdSessionFactory factory = getGerritSshdSessionFactory();
            GerritRepo gerritRepo = new GerritRepo(gitObj.getRepository());
            gerritRepo.setSshFactory(factory);
            assertEquals(gitObj.getRepository(), gerritRepo.getRepository());
            assertEquals(factory, gerritRepo.getSshFactory());
        }
    }

    @Test
    void testClone() throws Exception {
        CloneCommand cloneCommand = getCloneCommand();
        cloneCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(getGerritSshdSessionFactory());
        });
        GerritRepo git = new GerritRepo(cloneCommand.call().getRepository());
        assertEquals(git.getRepository().getDirectory().getParentFile(), localDir.toFile());
        assertTrue(localDir.toFile().exists());
    }

    @Test
    void testPush() throws GitAPIException {
        try (GerritRepo repo = cloneGerritRepo()) {
            //            assertTrue(repo.getSshFactory().getClient().isStarted());
            assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("master")));
            assertDoesNotThrow(() -> repo.commit().setMessage("First Commit").setCommitter("testPush", "sshd@apache.org").call());
            assertDoesNotThrow(() -> repo.push().call());
            assertAll(() -> assertTrue(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))));
        }
    }

    @Test
    void testPull() throws GitAPIException {
        try (GerritRepo repo = cloneGerritRepo();
            GerritRepo remote = new GerritRepo(getCloneCommandWithGerritTransport().setDirectory(
                    localRootDir.resolve(repo.getRepository().getWorkTree().getName().replace("clone", "second-clone")).toFile()).call()
                .getRepository(), getGerritSshdSessionFactory())) {
            assertAll(() -> assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(remote.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(remote.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))));
            assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master")));

            assertDoesNotThrow(() -> repo.commit().setMessage("First Commit").setCommitter("testPush", "sshd@apache.org").call());
            assertDoesNotThrow(() -> repo.push().call());
            assertAll(() -> assertTrue(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(remote.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertThrows(NoHeadException.class, () -> remote.log().call()));
            assertDoesNotThrow(() -> remote.pull().call());
            assertAll(() -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertTrue(StreamSupport.stream(repo.log().call().spliterator(), false)
                    .anyMatch(revCommit -> revCommit.getShortMessage().equals("First Commit"))));
        }
    }

    @Test
    void testFetch() throws GitAPIException {
        try (GerritRepo repo = cloneGerritRepo();
            GerritRepo remote = new GerritRepo(getCloneCommandWithGerritTransport().setDirectory(
                    localRootDir.resolve(repo.getRepository().getWorkTree().getName().replace("clone", "second-clone")).toFile()).call()
                .getRepository(), getGerritSshdSessionFactory())) {
            assertAll(() -> assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(remote.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(remote.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))));
            assertFalse(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master")));

            assertDoesNotThrow(() -> repo.commit().setMessage("First Commit").setCommitter("testPush", "sshd@apache.org").call());
            assertDoesNotThrow(() -> repo.push().call());
            assertAll(() -> assertTrue(repo.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertFalse(remote.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertThrows(NoHeadException.class, () -> remote.log().call()));
            assertDoesNotThrow(() -> remote.fetch().call());
            assertAll(() -> assertTrue(repo.lsRemote().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))),
                () -> assertThrows(NoHeadException.class, () -> remote.log().call()),
                () -> assertFalse(remote.branchList().call().stream().anyMatch(ref -> ref.getName().equals("refs/heads/master"))));
        }
    }

    private CloneCommand getCloneCommand() {
        return new CloneCommand().setURI(
                "ssh://" + currentServer.getUsername() + "@" + currentServer.getHost() + ":" + port + "/" + serverDir.getFileName())
            .setDirectory(localDir.toFile());
    }

    private CloneCommand getCloneCommandWithGerritTransport() {
        CloneCommand cloneCommand = getCloneCommand();
        cloneCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(getGerritSshdSessionFactory());
        });
        return cloneCommand;
    }

    private GerritRepo cloneGerritRepo() throws GitAPIException {
        CloneCommand cloneCommand = getCloneCommandWithGerritTransport();
        return new GerritRepo(cloneCommand.call().getRepository(), getGerritSshdSessionFactory());
    }
}

