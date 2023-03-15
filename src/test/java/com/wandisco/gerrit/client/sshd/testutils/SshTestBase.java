package com.wandisco.gerrit.client.sshd.testutils;

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

import static com.google.common.io.Resources.getResource;

import com.wandisco.gerrit.client.sshd.model.git.GerritSshdSessionFactory;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSShSessionCreator;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntryResolver;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.git.pack.GitPackCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.UnknownCommandFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.junit.jupiter.api.io.TempDir;

public class SshTestBase {

    protected static final String SSH_USERNAME = "asuer";
    protected static final String SSH_PASS = "apass";

    @TempDir
    protected static Path serverRootDir;

    @TempDir
    protected static Path gitRootDir;

    protected GerritSSHServer currentServer;

    public static SshClient setupTestClient() throws Exception {
        return setupTestClient(SshClient.setUpDefaultClient());
    }

    public static <C extends SshClient> C setupTestClient(C client) throws Exception {
        client.setHostConfigEntryResolver(HostConfigEntryResolver.EMPTY);
        client.setKeyIdentityProvider(LoadKeyPair());
        return client;
    }

    public static SshServer getSshServer() throws Exception {
        Path pubkeyLocation = getFile("gerrit-client-test-key_ecdsa.pub").toPath();
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setCommandFactory(new UnknownCommandFactory());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setKeyboardInteractiveAuthenticator(KeyboardInteractiveAuthenticator.NONE);
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(pubkeyLocation) {
            @Override
            public boolean isValidUsername(String username, ServerSession session) {
                return (GenericUtils.isNotEmpty(username) && username.equals(SSH_USERNAME));
            }
        });
        sshd.setPasswordAuthenticator((username, password, session) -> {
            boolean userGood = (username != null) && username.equals(SSH_USERNAME);
            boolean passGood = (password != null) && password.equals(SSH_PASS);
            return (userGood && passGood);
        });
        return sshd;
    }

    public static SshServer getGitSshServer() throws Exception {
        SshServer sshd = getSshServer();
        sshd.setCommandFactory(new GitPackCommandFactory(GitLocationResolver.constantPath(serverRootDir)));
        return sshd;
    }

    protected static GerritSSHServer getLocalSSHServer(SshServer server) throws Exception {
        String host = server.getHost();
        if (host == null || host.isEmpty()) {
            host = SshdSocketAddress.LOCALHOST_IPV4;
        }
        return new GerritSSHServer(host, server.getPort(), SSH_USERNAME,
            Collections.singletonList(getFile("gerrit-client-test-key_ecdsa")));
    }

    protected Path initNewServerRepo() throws GitAPIException {
        Path serverDir = serverRootDir.resolve(RandomStringUtils.randomAlphanumeric(5) + Constants.DOT_GIT_EXT);
        Git.init().setBare(true).setDirectory(serverDir.toFile()).call();
        return serverDir;
    }

    public static FileKeyPairProvider LoadKeyPair() throws Exception {

        FileKeyPairProvider fileset = new FileKeyPairProvider();
        fileset.setPaths(Collections.singletonList(getFile("gerrit-client-test-key_ecdsa").toPath()));
        return fileset;
    }

    protected static File getFile(String resourceName) throws Exception {
        URL url = getResource(resourceName);
        return new File(url.toURI());
    }

    protected GerritSShSessionCreator getSessionCreator() {
        return new GerritSShSessionCreator(currentServer);
    }

    protected GerritSshdSessionFactory getGerritSshdSessionFactory() {
        return new GerritSshdSessionFactory(getSessionCreator());
    }
}
