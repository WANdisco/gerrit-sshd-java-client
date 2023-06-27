package com.wandisco.gerrit.client.sshd.model.sshSession;

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

import com.wandisco.gerrit.client.sshd.model.exception.SSHAuthenticationException;
import com.wandisco.gerrit.client.sshd.model.exception.SSHSessionException;
import com.wandisco.gerrit.client.sshd.testutils.SshTestBase;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.UnknownCommandFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GerritSShSessionCreatorTest extends SshTestBase {

    protected static SshServer sshd;
    protected static int port;

    private static Path pubkeyLocation;

    @BeforeAll
    public static void setupClientAndServer() throws Exception {
        pubkeyLocation = getFile("gerrit-client-test-key_ecdsa.pub").toPath();
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
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(pubkeyLocation) {
            @Override
            public boolean isValidUsername(String username, ServerSession session) {
                return (GenericUtils.isNotEmpty(username) && username.equals(SSH_USERNAME));
            }
        });
        currentServer = getLocalSSHServer(sshd);
    }

    @AfterEach
    void clear(){
        System.clearProperty("org.apache.sshd.config.auth-timeout");
    }

    @Test
    public void testConstructor() {
        GerritSShSessionCreator creator = new GerritSShSessionCreator(currentServer);
        assertDoesNotThrow(() -> creator.connect());
    }

    @Test
    public void testConstructorWithTimeouts() {
        GerritSShSessionCreator creator = new GerritSShSessionCreator(currentServer, 30);
        assertAll(
            () -> assertDoesNotThrow(() -> creator.connect()),
            () -> assertEquals(30, creator.getConnectionTimeoutSeconds())
        );
    }

    @Test
    public void testGetGerritServer() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        GerritSShSessionCreator creator = new GerritSShSessionCreator(actualGerritSSHServer, 30);
        GerritSSHServer returnedServer = creator.getGerritSSHServer();
        assertAll("Gerrit server value checks",
            () -> assertEquals(actualGerritSSHServer.getHost(), returnedServer.getHost()),
            () -> assertEquals(actualGerritSSHServer.getPort(), returnedServer.getPort()),
            () -> assertEquals(actualGerritSSHServer.getUsername(), returnedServer.getUsername()),
            () -> assertEquals(actualGerritSSHServer.getUseKnownHosts(), returnedServer.getUseKnownHosts()),
            () -> assertEquals(actualGerritSSHServer.getLoadSSHConfig(), returnedServer.getLoadSSHConfig()),
            () -> assertEquals(actualGerritSSHServer.toString(), returnedServer.toString()),
            () -> assertIterableEquals(actualGerritSSHServer.getSshKeyFiles(), returnedServer.getSshKeyFiles()));
    }

    @Test
    public void testConnectionBadHost() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        int port = actualGerritSSHServer.getPort();
        GerritSSHServer badServer =
            new GerritSSHServer("badhost.bad.com", port, actualGerritSSHServer.getUsername(), actualGerritSSHServer.getSshKeyFiles());
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals("SSH client failed to connect to server 'badhost.bad.com:" + port
            + "'. Check that the host name and port combination is valid.", thrown.getMessage());
    }

    @Test
    public void testConnectionBadPort() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        String host = actualGerritSSHServer.getHost();
        int badport = 11111;
        GerritSSHServer badServer = new GerritSSHServer(actualGerritSSHServer.getHost(), badport, actualGerritSSHServer.getUsername(),
            actualGerritSSHServer.getSshKeyFiles());
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals("SSH client failed to connect to server '" + host + ":" + badport
            + "'. Check that the host name and port combination is valid.", thrown.getMessage());
    }

    @Test
    public void testConnectionWrongUser() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        String badUser = "wew";
        GerritSSHServer badServer = new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), badUser,
            actualGerritSSHServer.getSshKeyFiles());
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals(
            "SSH client failed to authenticate on server '" + actualGerritSSHServer.getHost() + ":" + actualGerritSSHServer.getPort()
                + "' as user '" + badUser + "'. Using supplied SSH keys", thrown.getMessage());
    }

    @Test
    public void testConnectionNoUser() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        String badUser = "";
        GerritSSHServer badServer = new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), badUser,
            actualGerritSSHServer.getSshKeyFiles());
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals(
            "SSH client failed to authenticate on server '" + actualGerritSSHServer.getHost() + ":" + actualGerritSSHServer.getPort()
                + "' as user '" + badUser + "'. Using supplied SSH keys", thrown.getMessage());
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "BUILD_LOCATION", matches = "github", disabledReason = "Requires local known host file configuration")
    public void testUseKnownHosts() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        GerritSSHServer testServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                actualGerritSSHServer.getSshKeyFiles());
        testServer.setUseKnownHosts(true);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(testServer);
        assertDoesNotThrow(() -> creator.connect());

    }

    @Test
    @DisabledIfEnvironmentVariable(named = "BUILD_LOCATION", matches = "github", disabledReason = "Requires local known host file configuration")
    public void testUseKnowHostsAndStrict() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        GerritSSHServer testServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                actualGerritSSHServer.getSshKeyFiles());
        testServer.setUseKnownHosts(true);
        testServer.setStrictHostCheck(true);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(testServer);
        assertDoesNotThrow(() -> creator.connect());

    }

    @Test
    public void testUseKnowHostsAndNotStrict() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        GerritSSHServer testServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                actualGerritSSHServer.getSshKeyFiles());
        testServer.setUseKnownHosts(true);
        testServer.setStrictHostCheck(false);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(testServer);
        assertDoesNotThrow(() -> creator.connect());
    }

    @Test
    public void testSetLoadSSHConfig() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        GerritSSHServer testServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                actualGerritSSHServer.getSshKeyFiles());
        testServer.setLoadSSHConfig(true);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(testServer);
        assertDoesNotThrow(() -> creator.connect());
    }

    @Test
    public void testConnectionNokeys() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        List<File> keys = actualGerritSSHServer.getSshKeyFiles();
        List<File> badkeys = Collections.emptyList();
        GerritSSHServer badServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                badkeys);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals(
            "SSH client failed to authenticate on server '" + actualGerritSSHServer.getHost() + ":" + actualGerritSSHServer.getPort()
                + "' as user '" + actualGerritSSHServer.getUsername() + "'. Using supplied SSH keys", thrown.getMessage());
    }

    @Test
    public void testConnectionBadkeys() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        List<File> badkeys = Collections.singletonList(new File("/some/bad/path"));
        GerritSSHServer badServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                badkeys);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals(
            "SSH client failed to authenticate on server '" + actualGerritSSHServer.getHost() + ":" + actualGerritSSHServer.getPort()
                + "' as user '" + actualGerritSSHServer.getUsername() + "'. Using supplied SSH keys", thrown.getMessage());
    }

    @Test
    public void testConnectionNullKeys() {
        GerritSSHServer actualGerritSSHServer = currentServer;
        List<File> keys = actualGerritSSHServer.getSshKeyFiles();
        List<File> badkeys = Collections.singletonList(null);
        GerritSSHServer badServer =
            new GerritSSHServer(actualGerritSSHServer.getHost(), actualGerritSSHServer.getPort(), actualGerritSSHServer.getUsername(),
                badkeys);
        GerritSShSessionCreator creator = new GerritSShSessionCreator(badServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, creator::connect);
        assertEquals(
            "SSH client failed to authenticate on server '" + actualGerritSSHServer.getHost() + ":" + actualGerritSSHServer.getPort()
                + "' as user '" + actualGerritSSHServer.getUsername() + "'. Using supplied SSH keys", thrown.getMessage());
    }

    @Test
    @Disabled("Flaky on Github")
    public void testAuthTimeout() throws Exception {
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(pubkeyLocation) {
            @Override
            public boolean isValidUsername(String username, ServerSession session) {
                try {
                    Thread.sleep(Duration.ofSeconds(10).toMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return (GenericUtils.isNotEmpty(username) && username.equals(SSH_USERNAME));
            }
        });
        System.setProperty("org.apache.sshd.config.auth-timeout", "5000");
        GerritSShSessionCreator creator = new GerritSShSessionCreator(currentServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, () -> creator.connect());
        assertInstanceOf(SSHAuthenticationException.class,thrown.getCause());
        System.out.println(thrown.getCause().getMessage());
        Pattern timeoutMatch = Pattern.compile("(^Detected AuthTimeout after \\d+/5000 ms\\.$)");
        assertTrue(timeoutMatch.matcher(thrown.getCause().getMessage()).matches());
    }

    @Test
    public void testAuthTimeoutUpdatedPass() throws Exception {
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(pubkeyLocation) {
            @Override
            public boolean isValidUsername(String username, ServerSession session) {
                try {
                    Thread.sleep(Duration.ofSeconds(30).toMillis());
                } catch (InterruptedException e) {
                    log.info("Call interrupted as auth cancellation on Timeout");
                }
                return (GenericUtils.isNotEmpty(username) && username.equals(SSH_USERNAME));
            }
        });
        GerritSShSessionCreator creator = new GerritSShSessionCreator(currentServer);
        assertDoesNotThrow( () -> creator.connect());
    }

    @Test
    public void testSessionClosed() throws Exception {
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(pubkeyLocation) {
            @Override
            public boolean isValidUsername(String username, ServerSession session) {
                try {
                    session.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return (GenericUtils.isNotEmpty(username) && username.equals(SSH_USERNAME));
            }
        });
        GerritSShSessionCreator creator = new GerritSShSessionCreator(currentServer);
        SSHSessionException thrown = assertThrows(SSHSessionException.class, () -> creator.connect());
        assertInstanceOf(SSHAuthenticationException.class,thrown.getCause());
        assertEquals("Session is being closed",thrown.getCause().getMessage());

    }

}
