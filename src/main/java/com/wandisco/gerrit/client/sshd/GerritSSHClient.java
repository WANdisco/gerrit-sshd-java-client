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

import com.wandisco.gerrit.client.sshd.model.exception.SCPDownloadException;
import com.wandisco.gerrit.client.sshd.model.git.GerritGitSshClient;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSShSessionCreator;
import com.wandisco.gerrit.client.sshd.model.task.GerritBaseCommand;
import com.wandisco.gerrit.client.sshd.model.task.GerritSshExecClient;
import java.io.File;
import java.io.IOException;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Object to act as client to the gerrit SSH daemon
 **/
public class GerritSSHClient {
    final char[][] pairs = {{'a', 'z'}, {'0', '9'}};
    private final GerritSSHServer server;
    private final GerritGitSshClient gitClient;
    private final GerritSshExecClient execClient;

    /**
     * Constructor takes an object representing the gerrit server and connection details.
     *
     * @param server {@link GerritSSHServer}  object representation of the gerrit server
     */
    public GerritSSHClient(GerritSSHServer server) {
        this(server,30);
    }

    public GerritSSHClient(GerritSSHServer server, long connectionTimeout) {
        RandomStringGenerator idGenerator = new RandomStringGenerator.Builder().withinRange(pairs).build();
        String clientID = idGenerator.generate(10);
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("[{}] Created new client with id:{} for server:{}", clientID, clientID, server);
        this.server = server;
        GerritSShSessionCreator clientSSHSessionCreator = new GerritSShSessionCreator(server,connectionTimeout);
        this.gitClient = new GerritGitSshClient(clientSSHSessionCreator, clientID);
        this.execClient = new GerritSshExecClient(clientSSHSessionCreator, clientID);
    }

    public GerritSSHServer getServer() {
        return server;
    }

    /**
     * Class to get the Exec Client for this gerrit server
     *
     * @return {@link GerritSshExecClient} returns exec Client with servers details
     */
    public GerritSshExecClient exec() {
        return execClient;
    }

    /**
     * Class to get the git Client for this gerrit server
     *
     * @return {@link GerritGitSshClient} returns git Client with servers details
     */
    public GerritGitSshClient git() {
        return gitClient;
    }

    /**
     * Executes the String command using this instance of {@link GerritSshExecClient}
     *
     * @param command Command to be sent to gerrit ssh port
     * @return {@link GerritBaseCommand} populated with command result
     * @throws IOException Thrown from issues parsing or executing the command
     */
    public GerritBaseCommand executeCommand(String command) throws IOException {
        return exec().executeCommand(command);
    }

    /**
     * Executes the {@link GerritBaseCommand} object using this instance of {@link GerritSshExecClient}
     *
     * @param command {@link GerritBaseCommand} to be send to gerrit ssh port
     * @return {@link GerritBaseCommand} provided populated with command result
     * @throws IOException Thrown from issues parsing or executing the command
     */
    public GerritBaseCommand executeCommand(GerritBaseCommand command) throws IOException {
        return exec().executeCommand(command);
    }

    /**
     * Executes a git clone against gerrit server with the project name provided to the target directory
     *
     * @param projectName Name of the project on gerrit
     * @param target {@link File} of target directory for the git clone
     * @return {@link Git} object for the newly cloned repository
     * @throws GitAPIException Exception thrown for failures during git clone
     */
    public Git cloneRepo(String projectName, File target) throws GitAPIException {
        return git().cloneRepo(projectName, target);
    }

    /**
     * Executes the {@link CloneCommand#call()} of the provided Clonecommand object Can optionally download the commit-msg hook These can be
     * generated for the gerrit instance using {@link GerritGitSshClient#generateCloneCommand(String)} to allow additional flags to be
     * supplied.
     *
     * @param cloneCommand Name of the project on gerrit
     * @return {@link Git} object for the newly cloned repository
     * @throws GitAPIException Exception thrown for failures during git clone
     */
    public Git cloneRepo(CloneCommand cloneCommand) throws GitAPIException {
        return git().cloneRepo(cloneCommand);
    }

    /**
     * Downloads the Commit-msg hook into the target projects hook dir over scp
     *
     * @param project {@link Git} object for the project directory
     * @throws SCPDownloadException Exception thrown from failure to download hook script
     */
    public void getCommitMsgHook(Git project) throws SCPDownloadException {
        ScpClient scpClient = ScpClientCreator.instance().createScpClient(new GerritSShSessionCreator(getServer()).connect());
        try {
            scpClient.download("hooks/commit-msg", new File(project.getRepository().getDirectory(), "hooks/commit-msg").toPath());
        } catch (IOException e) {
            throw new SCPDownloadException("Failed to download the commit-msg script", e);
        }
    }

}
