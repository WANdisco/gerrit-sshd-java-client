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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSShSessionCreator;
import com.wandisco.gerrit.client.sshd.testutils.SshTestBase;
import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.git.transport.GitSshdSessionFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GerritSshdSessionFactoryTest extends SshTestBase {
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
    }

    @Test
    void testConstructor() {
        GerritSshdSessionFactory sessionFactory = assertDoesNotThrow(() -> new GerritSshdSessionFactory());
        assertAll(
            () -> assertNull(sessionFactory.getSessionCreator()),
            () -> assertNull(sessionFactory.getSession()),
            () -> assertNull(sessionFactory.getClientSession()),
            () -> assertNull(sessionFactory.getClient())
        );
    }

    @Test
    void testConstructor2() {
        GerritSShSessionCreator sessionCreator = getSessionCreator();
        GerritSshdSessionFactory sessionFactory = assertDoesNotThrow(() -> new GerritSshdSessionFactory(sessionCreator));
        assertAll(
            () -> assertEquals(sessionCreator,sessionFactory.getSessionCreator()),
            () -> assertNull(sessionFactory.getSession()),
            () -> assertNull(sessionFactory.getClientSession()),
            () -> assertNull(sessionFactory.getClient())
        );
    }

    @Test
    void testConstructor3() {
        SshClient client = null;
        Throwable failure = assertThrows(NullPointerException.class,() -> new GerritSshdSessionFactory(client));
        assertEquals("No client instance provided",failure.getMessage());
    }

    @Test
    void testConstructor4() throws Exception {
        SshClient client = setupTestClient();
        GerritSshdSessionFactory sessionFactory = assertDoesNotThrow(() -> new GerritSshdSessionFactory(client));
        assertAll(
            () -> assertNull(sessionFactory.getSessionCreator()),
            () -> assertNull(sessionFactory.getSession()),
            () -> assertNull(sessionFactory.getClientSession()),
            () -> assertEquals(client,sessionFactory.getClient())
        );
    }

    @Test
    void testConstructor5() {
        ClientSession session = null;

        GerritSshdSessionFactory sessionFactory = assertDoesNotThrow(() -> new GerritSshdSessionFactory(session));
        assertAll(
            () -> assertNull(sessionFactory.getSessionCreator()),
            () -> assertNull(sessionFactory.getSession()),
            () -> assertNull(sessionFactory.getClientSession()),
            () -> assertNull(sessionFactory.getClient())
        );
    }

    @Test
    void testConstructor6() {
        ClientSession session = null;
        SshClient client = null;

        GerritSshdSessionFactory sessionFactory = assertDoesNotThrow(() -> new GerritSshdSessionFactory(client,session));
        assertAll(
            () -> assertNull(sessionFactory.getSessionCreator()),
            () -> assertNull(sessionFactory.getSession()),
            () -> assertNull(sessionFactory.getClientSession()),
            () -> assertNull(sessionFactory.getClient())
        );
    }

    @Test
    void testTransportException() throws GitAPIException {
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));
        GerritSSHServer actualGerritSSHServer = currentServer;
        List<File> keys = actualGerritSSHServer.getSshKeyFiles();
        List<File> badkeys = Collections.emptyList();
        GerritSSHServer badServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                badkeys);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        GitSshdSessionFactory sshdFactory = new GerritSshdSessionFactory(creator);
        CloneCommand cloneCommand = getCloneCommandWithGerritTransport();
        cloneCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshdFactory);
        });
        Throwable thrown = assertThrows(TransportException.class, cloneCommand::call);
        assertEquals("Unable to connect", thrown.getMessage());
        assertFalse(localDir.toFile().exists());
    }

    @Test
    void testNonGerritUsage() throws Exception {
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));

        SshClient passClient = setupTestClient();
        passClient.start();
        GitSshdSessionFactory sshdFactory = new GerritSshdSessionFactory(passClient);  // re-use the same client for all SSH sessions
        CloneCommand cloneCommand =
            new CloneCommand().setURI("ssh://" + currentServer.getHost() + ":" + port + "/" + serverDir.getFileName())
                .setDirectory(localDir.toFile());
        cloneCommand.setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshdFactory);
            sshTransport.setCredentialsProvider(new UsernamePasswordCredentialsProvider(SSH_USERNAME, SSH_PASS));
        });
        Git repo = assertDoesNotThrow(cloneCommand::call);
        assertEquals(repo.getRepository().getDirectory().getParentFile(), localDir.toFile());
        assertTrue(localDir.toFile().exists());
    }

    @Test
    void testNonGerritUsageSession() throws Exception {
        serverDir = initNewServerRepo();
        localDir = localRootDir.resolve(serverDir.getFileName().toString().replace(".git", "-clone"));

        try( SshClient passClient = SshClient.setUpDefaultClient()){
            passClient.start();
            ConnectFuture sshFuture = passClient.connect(SSH_USERNAME,currentServer.getHost(),currentServer.getPort());
            sshFuture.await(Duration.ofSeconds(20));
            ClientSession clientSession = sshFuture.getClientSession();
            clientSession.addPasswordIdentity(SSH_PASS);
            AuthFuture auth = clientSession.auth();
            auth.verify(Duration.ofSeconds(20));
            GitSshdSessionFactory sshdFactory = new GerritSshdSessionFactory(clientSession);  // re-use the same client for all SSH sessions
            CloneCommand cloneCommand =
                new CloneCommand().setURI("ssh://" + currentServer.getHost() + ":" + port + "/" + serverDir.getFileName()).setDirectory(localDir.toFile());
            cloneCommand.setTransportConfigCallback(transport -> {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshdFactory);
                sshTransport.setCredentialsProvider(new UsernamePasswordCredentialsProvider(SSH_USERNAME, SSH_PASS));
            });
            Git repo = assertDoesNotThrow(cloneCommand::call);
            assertEquals(repo.getRepository().getDirectory().getParentFile(), localDir.toFile());
            assertTrue(localDir.toFile().exists());
        }
    }

    @Test
    void testGetType() {
        GerritSshdSessionFactory sessionFactory = assertDoesNotThrow(() -> new GerritSshdSessionFactory(getSessionCreator()));
        assertEquals("gerrit-sshd-jgit", sessionFactory.getType());
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

