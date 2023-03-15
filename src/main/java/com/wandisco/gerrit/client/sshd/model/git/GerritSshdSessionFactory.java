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

import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSShSessionCreator;
import java.io.IOException;
import java.util.Objects;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.session.ClientSessionHolder;
import org.apache.sshd.common.session.SessionHolder;
import org.apache.sshd.common.util.functors.UnaryEquator;
import org.apache.sshd.git.transport.GitSshdSession;
import org.apache.sshd.git.transport.GitSshdSessionFactory;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.transport.ChainingCredentialsProvider;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteSession;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

/**
 * <p>
 * Custom SshSessionFactory to load the Gerrit SSH Client and session for git commits;
 */
public class GerritSshdSessionFactory extends GitSshdSessionFactory implements SessionHolder<ClientSession>, ClientSessionHolder {
    public static final GitSshdSessionFactory INSTANCE = new GitSshdSessionFactory();
    private final GerritSShSessionCreator sessionCreator;

    public GerritSshdSessionFactory(GerritSShSessionCreator sessionCreator) {
        super(null,null);
        this.sessionCreator = sessionCreator;
    }

    public GerritSshdSessionFactory() {
        this((SshClient)null, (ClientSession)null);
    }

    public GerritSshdSessionFactory(SshClient client) {
        this((SshClient) Objects.requireNonNull(client, "No client instance provided"), (ClientSession)null);
    }

    public GerritSshdSessionFactory(ClientSession session) {
        this((SshClient)null, session);
    }

    protected GerritSshdSessionFactory(SshClient client, ClientSession session) {
        super(client, session);
        this.sessionCreator = null;
    }


    @Override
    public RemoteSession getSession(URIish uri, CredentialsProvider credentialsProvider, FS fs, int tms) throws TransportException {
        try {
            if(credentialsProvider == null) {
                //init unused provider if none-provided
                credentialsProvider = new ChainingCredentialsProvider();
            }
            return new GitSshdSession(uri, credentialsProvider, fs, tms) {
                @Override
                protected SshClient createClient() {
                    SshClient thisClient = getClient();
                    if (thisClient != null) {
                        return thisClient;
                    }
                    if (getSessionCreator() != null){
                        return getSessionCreator().createClient();
                    }
                    return super.createClient();
                }

                @Override
                protected ClientSession createClientSession(SshClient clientInstance, String host, String username, int port,
                    String... passwords) throws IOException, InterruptedException {
                    ClientSession thisSession = getClientSession();
                    if (thisSession != null) {
                        return thisSession;
                    }
                    if (getSessionCreator() != null){
                        return getSessionCreator().connect(clientInstance);
                    }
                    return super.createClientSession(clientInstance, host, username, port, passwords);
                }

                @Override
                protected void disconnectSession(ClientSession sessionInstance) {
                    ClientSession thisSession = getClientSession();
                    if (UnaryEquator.isSameReference(thisSession, sessionInstance)) {
                        return; // do not use the session instance we were given
                    }

                    super.disconnectSession(sessionInstance);
                }

                @Override
                protected void disconnectClient(SshClient clientInstance) {
                    SshClient thisClient = getClient();
                    if (UnaryEquator.isSameReference(thisClient, clientInstance)) {
                        return; // do not close the client the user gave us
                    }

                    super.disconnectClient(clientInstance);
                }
            };
        } catch (Exception e) {
            throw new TransportException("Unable to connect", e);
        }
    }

    @Override
    public String getType() {
        return "gerrit-sshd-jgit";
    }

    protected SshClient getClient(){
        return super.getClient();
    }

    public GerritSShSessionCreator getSessionCreator(){return sessionCreator;}

}

