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
import com.wandisco.gerrit.client.sshd.model.exception.SSHConnectionException;
import com.wandisco.gerrit.client.sshd.model.exception.SSHSessionException;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.UnresolvedAddressException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.DefaultConfigFileHostEntryResolver;
import org.apache.sshd.client.config.hosts.KnownHostEntry;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.DefaultKnownHostsServerKeyVerifier;
import org.apache.sshd.client.keyverifier.RejectAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.RuntimeSshException;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerritSShSessionCreator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GerritSSHServer gerritSSHServer;

    private final long connectionTimeoutSeconds;

    /**
     * Constructor that takes the Gerrit SSH server object
     *
     * @param gerritSSHServer {@link GerritSSHServer} Object representation of the server to connect to.
     */
    public GerritSShSessionCreator(GerritSSHServer gerritSSHServer) {
        this.gerritSSHServer = gerritSSHServer;
        this.connectionTimeoutSeconds = 5;
    }

    /**
     * Constructor that takes Gerrit SSH server object and allow custom retry and time out params
     *
     * @param gerritSSHServer {@link GerritSSHServer} Object representation of the server to connect to
     * @param connectionTimeoutSeconds Total time to wait before declaring the connection timed out
     */
    public GerritSShSessionCreator(GerritSSHServer gerritSSHServer, long connectionTimeoutSeconds) {
        this.gerritSSHServer = gerritSSHServer;
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }

    public GerritSSHServer getGerritSSHServer() {
        return gerritSSHServer;
    }

    /**
     * Method to build a client session for the details of the {@link GerritSSHServer} object attached
     * <p>
     * Opens connection then attaches keys list as a key provider to authenticate
     * <p>
     *
     * @return {@link ClientSession} Client session object for the details of the server object
     */
    public ClientSession connect() {
        return connect(createClient());
    }

    public ClientSession connect(SshClient sshClient) {
        ClientSession session;
        try {
            session = createSession(sshClient);
        } catch (SSHConnectionException sshConn) {
            throw new SSHSessionException(String.format(
                "SSH client failed to connect to server '%s:%s'. " + "Check that the host name and port combination is valid.",
                gerritSSHServer.getHost(), gerritSSHServer.getPort()), sshConn);
        }
        try {
            session.setUsername(gerritSSHServer.getUsername());
            session = addSshKeys(session);
            authenticate(session);
        } catch (SSHAuthenticationException | IllegalStateException auth) {
            throw new SSHSessionException(
                String.format("SSH client failed to authenticate on server '%s:%s' as user '%s'. " + "Using supplied SSH keys",
                    gerritSSHServer.getHost(), gerritSSHServer.getPort(), gerritSSHServer.getUsername()), auth);
        }
        return session;
    }

    /**
     * Creates the actual {@link ClientSession} based on the configuration of the attached {@link GerritSSHServer}
     * <p>
     * Based on the settings in the {@link GerritSSHServer} will use local known_hosts, sshconfig or strict host checking
     *
     * @param client {@link SshClient} Ssh client to make session from
     * @return {@link ClientSession} Returns a ClientSession to the sever
     * @throws SSHConnectionException Thrown if the session could not be made
     */
    private ClientSession createSession(SshClient client) throws SSHConnectionException {
        client.start();

        try {
            ConnectFuture sshFuture;
            if (!gerritSSHServer.getLoadSSHConfig()) {
                sshFuture = client.connect(gerritSSHServer.getUsername(), gerritSSHServer.getHost(), gerritSSHServer.getPort());
            } else {
                sshFuture = client.connect(gerritSSHServer.getHostConfigEntry());
            }
            boolean connected = sshFuture.await(connectionTimeoutSeconds * 1000L);
            if (!connected) {
                throw new SSHConnectionException(
                    String.format("Timed out when creating SSH session to '%s:%s', after %s seconds.", gerritSSHServer.getHost(),
                        gerritSSHServer.getPort(), connectionTimeoutSeconds));
            }
            return sshFuture.getSession();
        } catch (IOException | RuntimeSshException | UnresolvedAddressException ex) {
            logger.error("Error thrown during Session creation: {}", ex.getMessage());
            if (ex.getCause() instanceof ConnectException) {
                Throwable cause = ex.getCause();
                throw new SSHConnectionException(
                    String.format("Failed to create SSH session to '%s:%s' - %s ", gerritSSHServer.getHost(), gerritSSHServer.getPort(),
                        cause.getMessage()), cause);
            } else {
                throw new SSHConnectionException(
                    String.format("Failed to create SSH session to '%s:%s' - %s ", gerritSSHServer.getHost(), gerritSSHServer.getPort(),
                        ex.getMessage()), ex);
            }
        }
    }

    public SshClient createClient() {
        SshClient client = SshClient.setUpDefaultClient();

        if (gerritSSHServer.getUseKnownHosts()) {
            DefaultKnownHostsServerKeyVerifier tmp;

            if (gerritSSHServer.getStrictHostCheck()) {
                tmp = new DefaultKnownHostsServerKeyVerifier(RejectAllServerKeyVerifier.INSTANCE, true);
            } else {
                tmp = new DefaultKnownHostsServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE, true);
                tmp.setModifiedServerKeyAcceptor((ClientSession clientSession, SocketAddress remoteAddress, KnownHostEntry entry, PublicKey expected, PublicKey actual) -> true);
            }
            client.setServerKeyVerifier(tmp);
        } else {
            client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        }

        if (gerritSSHServer.getLoadSSHConfig()) {
            client.setHostConfigEntryResolver(DefaultConfigFileHostEntryResolver.INSTANCE);
        }
        return client;
    }

    /**
     * Creates a {@link FileKeyPairProvider} for the list of private keys attached to the {@link GerritSSHServer} this is then attached to
     * the current {@link ClientSession}
     *
     * @param session {@link ClientSession} session to attach the keys to
     * @return {@link ClientSession} Client session modified with the new {@link FileKeyPairProvider}
     */
    private ClientSession addSshKeys(ClientSession session) {
        FileKeyPairProvider fileKeyPairProvider = getCredentialsProvider();
        Iterable<KeyPair> userKeys = fileKeyPairProvider.loadKeys(session);
        userKeys.forEach(session::addPublicKeyIdentity);
        return session;
    }

    public FileKeyPairProvider getCredentialsProvider() {
        return getKeys(gerritSSHServer.getSshKeyFiles());
    }

    /**
     * Attempts to authenticate against the {@link ClientSession} and the Keys saved on the session
     *
     * @param session {@link ClientSession} Client session to attempt authentication on
     * @throws SSHAuthenticationException Thrown if the auth fails for all keys and all reattempts
     */
    private void authenticate(ClientSession session) throws SSHAuthenticationException {

        try {
            AuthFuture auth = session.auth();
            auth.verify(Duration.ofSeconds(connectionTimeoutSeconds));
            } catch (IOException io) {
                logger.error("Error thrown during Session authentication: {}", io.getMessage());
                throw new SSHAuthenticationException(io.getMessage(), io);
            }
        if (session.isClosed()) {
            throw new SSHAuthenticationException(
                String.format("Failed to authenticate at '%s:%s'.", gerritSSHServer.getHost(), gerritSSHServer.getPort()));
        }
    }

    /**
     * Creates a new {@link FileKeyPairProvider} using the list of {@link File} provided
     *
     * @param keyFiles List of {@link File} objects for all ssh private keys to use for Session Authentication
     * @return {@link FileKeyPairProvider} populated with the ssh private keys to use.
     */
    private FileKeyPairProvider getKeys(List<File> keyFiles) {
        if (keyFiles.size() > 0) {
            List<Path> pathList = keyFiles.stream().filter(Objects::nonNull).map(File::toPath).collect(Collectors.toList());
            FileKeyPairProvider fileset = new FileKeyPairProvider();
            fileset.setPaths(pathList);
            return fileset;
        }
        return new FileKeyPairProvider();
    }

    public long getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

}
