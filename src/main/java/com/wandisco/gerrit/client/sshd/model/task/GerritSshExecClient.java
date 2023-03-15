package com.wandisco.gerrit.client.sshd.model.task;

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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerritSshExecClient {

    final char[][] pairs = {{'a', 'z'}, {'0', '9'}};
    private final String clientID;
    private final GerritSShSessionCreator clientSSHSessionCreator;
    private final RandomStringGenerator idGenerator = new RandomStringGenerator.Builder().withinRange(pairs).build();
    private final SshClient currentClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GerritSshExecClient(GerritSSHServer server) {
        this.clientID = idGenerator.generate(10);
        this.clientSSHSessionCreator = new GerritSShSessionCreator(server);
        this.currentClient = clientSSHSessionCreator.createClient();
    }

    public GerritSshExecClient(GerritSShSessionCreator clientSSHSessionCreator) {
        this.clientID = idGenerator.generate(10);
        this.clientSSHSessionCreator = clientSSHSessionCreator;
        this.currentClient = clientSSHSessionCreator.createClient();
    }

    public GerritSshExecClient(GerritSShSessionCreator clientSSHSessionCreator, String clientID) {
        this.clientSSHSessionCreator = clientSSHSessionCreator;
        this.clientID = clientID;
        this.currentClient = clientSSHSessionCreator.createClient();
    }

    /**
     * Will open channel to gerrit server and execute the string provided
     *
     * @param command {@link String} String command to be sent to Gerrit instance
     * @return {@link GerritBaseCommand} returns a command object with exit code duration etc. set on the object
     * @throws IOException thrown primarily from issues connecting to server
     */
    public GerritBaseCommand executeCommand(String command) throws IOException {
        return executeCommand(new GerritBaseCommand(command));
    }

    /**
     * Will open channel to gerrit server and execute the string provided
     *
     * @param command {@link GerritBaseCommand} String command to be sent to Gerrit instance
     * @return {@link GerritBaseCommand} returns the command object with exit code, duration output if any etc. set on the object
     * @throws IOException thrown primarily from issues connecting to server
     */
    public GerritBaseCommand executeCommand(GerritBaseCommand command) throws IOException {
        List<String> commandOutputLines = new LinkedList<>();
        String commandID = idGenerator.generate(10);
        String clientCommandID = String.format("%s-%s", clientID, commandID);
        try (ClientSession clientSession = clientSSHSessionCreator.connect(currentClient);
             ChannelExec channel = clientSession.createExecChannel(command.getCommand())) {
            //execute start to start get command start times
            command.start();
            logger.info("[{}] : Executing command: {}", clientCommandID, command);
            //open exec channel to actually execute the command
            channel.open().await();
            //wait for either data or an exit code from the command
            Collection<ClientChannelEvent> waitMask =
                channel.waitFor(Stream.of(ClientChannelEvent.CLOSED).collect(Collectors.toSet()), command.getTimeout().toMillis());
            if (waitMask.contains(ClientChannelEvent.TIMEOUT)) {
                command.setTimedOut(true);
                command.setSuccessful(false);
            } else {
                int exitCode = channel.getExitStatus();
                command.setExitCode(exitCode);
                command.setSuccessful(exitCode == 0);
            }

            //get streams of the stdout and stdErr

            // Some gerrit commands appear to have exit status of 0 but print out to stderr,
            // this combines the two streams printing stdout then stderr, usually so far one is always empty.
            try (BufferedInputStream stdout = new BufferedInputStream(channel.getInvertedOut());
                BufferedInputStream stderr = new BufferedInputStream(channel.getInvertedErr());
                SequenceInputStream joinedStream = new SequenceInputStream(stdout, stderr);
                InputStreamReader inReader = new InputStreamReader(joinedStream);
                BufferedReader bReader = new BufferedReader(inReader)) {
                //read streams line by line to build up list of output strings
                String str;
                while ((str = bReader.readLine()) != null) {
                    commandOutputLines.add(str);
                }
            } finally {
                channel.close(true);
                clientSession.close(true);
            }
            //attach list of outputs to the command object
            command.setOutputLines(commandOutputLines);
            //end to set stop time
            command.end(!command.isTimedOut());
            logger.info("[{}] : Command exited after duration:{}ms with status:{} ", clientCommandID, command.getDuration(),
                command.getExitCode());
            logger.debug("[{}] : Command out output for command:{} \n output:{}", clientCommandID, command.getCommand(),
                command.getOutput());

            return command;
        }
    }

    public String getClientID() {
        return clientID;
    }
}
