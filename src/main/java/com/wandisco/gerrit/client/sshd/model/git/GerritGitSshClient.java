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

import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSShSessionCreator;
import java.io.File;
import org.apache.commons.text.RandomStringGenerator;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerritGitSshClient {

    final char[][] pairs = {{'a', 'z'}, {'0', '9'}};
    private final GerritSSHServer server;
    private final GerritSShSessionCreator clientSSHSessionCreator;
    private final String clientID;
    private final GerritSshdSessionFactory sshFactory;
    private final RandomStringGenerator idGenerator = new RandomStringGenerator.Builder().withinRange(pairs).build();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GerritGitSshClient(GerritSSHServer server) {
        this.server = server;
        this.clientID = idGenerator.generate(10);
        this.clientSSHSessionCreator = new GerritSShSessionCreator(server);
        this.sshFactory = new GerritSshdSessionFactory(clientSSHSessionCreator);
    }

    public GerritGitSshClient(GerritSShSessionCreator clientSSHSessionCreator) {
        this.clientID = idGenerator.generate(10);
        this.clientSSHSessionCreator = clientSSHSessionCreator;
        this.server = clientSSHSessionCreator.getGerritSSHServer();
        this.sshFactory = new GerritSshdSessionFactory(clientSSHSessionCreator);
    }

    public GerritGitSshClient(GerritSShSessionCreator clientSSHSessionCreator, String clientID) {
        this.clientSSHSessionCreator = clientSSHSessionCreator;
        this.server = clientSSHSessionCreator.getGerritSSHServer();
        this.clientID = clientID;
        this.sshFactory = new GerritSshdSessionFactory(clientSSHSessionCreator);
    }

    public CloneCommand generateCloneCommand(String projectName, File target) {
        return generateCloneCommand(projectName).setDirectory(target);
    }

    /**
     * Generates a Clone command based on the current clients details for provided project
     *
     * @param projectName String of the project to clone
     * @return {@link CloneCommand} for project from current {@link GerritSSHServer} instance.
     */
    public CloneCommand generateCloneCommand(String projectName) {
        String cloneURI = String.format("ssh://%s:%s/%s", server.getHost(), server.getPort(), projectName);
        return new CloneCommand().setURI(cloneURI).setTransportConfigCallback(transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshFactory);
        });
    }

    public Git cloneRepo(String projectName) throws GitAPIException {
        return cloneRepo(generateCloneCommand(projectName));
    }

    public Git cloneRepo(String projectName, File target) throws GitAPIException {
        return cloneRepo(generateCloneCommand(projectName, target));
    }

    /**
     * Method uses the current GerritServer object to build a Git session for this gerrit instance
     * <p>
     * Updates the sshFactory for Jgit call and executes the {@link CloneCommand#call()} on provided command
     *
     * @param cloneCommand {@link CloneCommand} Clone command to be executed can be generated from {@link #generateCloneCommand(String)}
     * @return returns {@link Git} object for the Git repository clone
     * @throws GitAPIException Exceptions thrown on Git failures
     */
    public Git cloneRepo(CloneCommand cloneCommand) throws GitAPIException {
        String commandID = idGenerator.generate(10);
        String clientCommandID = String.format("%s-%s", clientID, commandID);
        //build gerrit url without credentials as these are in teh client already
        // carry out git clone and return a Git object of the clone via ssh rest of calls on this object will use client
        logger.info("[{}] Cloning repository from : {} ", clientCommandID, server.getHost());
        cloneCommand.setTransportConfigCallback(transport -> {
            //replace the session factory with own version
            //org.eclipse.jgit.transport.SshSessionFactory.setInstance(sshFactory);
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshFactory);
        });
        //Return our overridden Git class to ensure the ssh session is always for this client.
        // as CloneCommand is static easier to just re-init our gerritRepo with the resulting clone.
        return new GerritRepo(cloneCommand.call().getRepository(), sshFactory);
    }

    protected GerritSSHServer getServer() {
        return server;
    }

    protected String getClientID() {
        return clientID;
    }
}
