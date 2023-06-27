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

import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.config.hosts.HostPatternValue;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GerritSSHServerTest {

    private final String username = "auser";
    private final String hostname = "localhost";
    private final int port = 29418;
    private final List<File> files = Collections.singletonList(new File("/a/not/real/path"));

    @Test
    public void testServerConstructor() {
        GerritSSHServer server = new GerritSSHServer(hostname, port, username, files);
        testServerDetails(server);
    }

    @Test
    public void testServerBuilder() {
        GerritSSHServer server =
            new GerritSSHServer.Builder(username, hostname).port(port).sshKeyFile(files.get(0).getAbsolutePath()).build();
        testServerDetails(server);
    }

    private void testServerDetails(GerritSSHServer server) {
        assertEquals(hostname, server.getHost());
        assertEquals(port, server.getPort());
        assertEquals(username, server.getUsername());
        assertEquals(files, server.getSshKeyFiles());
        assertEquals(
            "GerritSSHServer{" + "host='" + hostname + '\'' + ", port=" + port + ", username='" + username + '\'' + '\'' + ", sshKeyPath="
                + Arrays.toString(files.stream().map(File::getPath).toArray()) + '}', server.toString());
    }

    @Test
    void testBuilderBuild() {

        GerritSSHServer.Builder useKnownHostsResult =
            (new GerritSSHServer.Builder("janedoe", "localhost")).loadSSHConfig(true).port(8080).strictHostCheck(true).useKnownHosts(true);

        GerritSSHServer actualBuildResult = useKnownHostsResult.build();

        assertEquals("localhost", actualBuildResult.getHost());
        assertEquals("janedoe", actualBuildResult.getUsername());
        assertTrue(actualBuildResult.getUseKnownHosts());
        assertTrue(actualBuildResult.getStrictHostCheck());
        assertTrue(actualBuildResult.getSshKeyFiles().isEmpty());
        assertEquals(8080, actualBuildResult.getPort());
        assertTrue(actualBuildResult.getLoadSSHConfig());
        HostConfigEntry hostConfigEntry = actualBuildResult.getHostConfigEntry();
        assertEquals("localhost", hostConfigEntry.getHost());
        assertEquals("janedoe", hostConfigEntry.getUsername());
        assertNull(hostConfigEntry.getProxyJump());
        Map<String,String> props = hostConfigEntry.getProperties();
        assertAll(() -> assertEquals(props.get("HostName"),"localhost"),
                () -> assertEquals(props.get("Port"),"8080"),
                () -> assertEquals(props.get("User"),"janedoe"));
        assertEquals(8080, hostConfigEntry.getPort());
        Collection<HostPatternValue> patterns = hostConfigEntry.getPatterns();
        assertEquals(1, patterns.size());
        assertTrue(hostConfigEntry.getIdentities().isEmpty());
        assertEquals("localhost", hostConfigEntry.getHostName());
        HostPatternValue getResult = ((List<HostPatternValue>) patterns).get(0);
        assertFalse(getResult.isNegated());
        assertEquals(0, getResult.getPort());
        Pattern pattern = getResult.getPattern();
        assertEquals("localhost", pattern.pattern());
        assertEquals(2, pattern.flags());
        assertEquals("localhost", pattern.toString());
    }

    @Test
    void testConstructor() {

        String host = "localhost";
        int port = 8080;
        String username = "janedoe";
        ArrayList<File> fileList = new ArrayList<>();

        GerritSSHServer actualGerritSSHServer = new GerritSSHServer(host, port, username, fileList);
        HostConfigEntry hostConfigEntry = new HostConfigEntry();
        actualGerritSSHServer.setHostConfigEntry(hostConfigEntry);
        boolean loadSSHConfig = true;
        actualGerritSSHServer.setLoadSSHConfig(loadSSHConfig);
        boolean strictHostCheck = true;
        actualGerritSSHServer.setStrictHostCheck(strictHostCheck);
        boolean useKnownHosts = true;
        actualGerritSSHServer.setUseKnownHosts(useKnownHosts);
        String actualToStringResult = actualGerritSSHServer.toString();

        assertEquals("localhost", actualGerritSSHServer.getHost());
        assertTrue(actualGerritSSHServer.getLoadSSHConfig());
        assertEquals(8080, actualGerritSSHServer.getPort());
        assertSame(fileList, actualGerritSSHServer.getSshKeyFiles());
        assertTrue(actualGerritSSHServer.getStrictHostCheck());
        assertTrue(actualGerritSSHServer.getUseKnownHosts());
        assertEquals("janedoe", actualGerritSSHServer.getUsername());
        assertEquals("GerritSSHServer{host='localhost', port=8080, username='janedoe'', sshKeyPath=[]}", actualToStringResult);
    }

    @Test
    void testConstructor2() {

        String host = "localhost";
        int port = 8080;
        String username = "janedoe";
        ArrayList<File> fileList = new ArrayList<>();
        boolean useKnownHosts = true;
        boolean strictHostCheck = true;
        boolean loadSSHConfig = true;

        GerritSSHServer actualGerritSSHServer =
            new GerritSSHServer(host, port, username, fileList, useKnownHosts, strictHostCheck, loadSSHConfig);
        HostConfigEntry hostConfigEntry = new HostConfigEntry();
        actualGerritSSHServer.setHostConfigEntry(hostConfigEntry);
        boolean loadSSHConfig1 = true;
        actualGerritSSHServer.setLoadSSHConfig(loadSSHConfig1);
        boolean strictHostCheck1 = true;
        actualGerritSSHServer.setStrictHostCheck(strictHostCheck1);
        boolean useKnownHosts1 = true;
        actualGerritSSHServer.setUseKnownHosts(useKnownHosts1);
        String actualToStringResult = actualGerritSSHServer.toString();

        assertEquals("localhost", actualGerritSSHServer.getHost());
        assertTrue(actualGerritSSHServer.getLoadSSHConfig());
        assertEquals(8080, actualGerritSSHServer.getPort());
        assertSame(fileList, actualGerritSSHServer.getSshKeyFiles());
        assertTrue(actualGerritSSHServer.getStrictHostCheck());
        assertTrue(actualGerritSSHServer.getUseKnownHosts());
        assertEquals("janedoe", actualGerritSSHServer.getUsername());
        assertEquals("GerritSSHServer{host='localhost', port=8080, username='janedoe'', sshKeyPath=[]}", actualToStringResult);
    }

    @Test
    void testGetHostConfigEntry() {
        String host = "localhost";
        int port = 8080;
        String username = "janedoe";
        ArrayList<File> fileList = new ArrayList<>();

        HostConfigEntry actualHostConfigEntry = new HostConfigEntry();
        actualHostConfigEntry.setHostName(host);
        actualHostConfigEntry.setHost(host);
        actualHostConfigEntry.setUsername(username);
        actualHostConfigEntry.setPort(port);


        HostConfigEntry gerritServerHostEntryConfig = new GerritSSHServer(host, port, username, fileList).getHostConfigEntry();
        assertEquals(actualHostConfigEntry.getHost(), gerritServerHostEntryConfig.getHost());
        assertEquals(actualHostConfigEntry.getHostName(), gerritServerHostEntryConfig.getHostName());
        assertEquals(actualHostConfigEntry.getPort(), gerritServerHostEntryConfig.getPort());
        assertEquals(actualHostConfigEntry.getUsername(), gerritServerHostEntryConfig.getUsername());
    }

    @Test
    void testGetHostConfigEntry2() {
        String host = "localhost";
        int port = 8080;
        String username = "janedoe";
        ArrayList<File> fileList = new ArrayList<>();

        HostConfigEntry actualHostConfigEntry = new HostConfigEntry();
        actualHostConfigEntry.setHostName(host);
        actualHostConfigEntry.setHost(host);
        actualHostConfigEntry.setUsername(username);
        actualHostConfigEntry.setPort(port);

        GerritSSHServer gerritSSHServer = new GerritSSHServer(host, port, username, fileList);
        gerritSSHServer.setHostConfigEntry(actualHostConfigEntry);
        HostConfigEntry gerritServerHostEntryConfig = gerritSSHServer.getHostConfigEntry();
        assertEquals(actualHostConfigEntry.getHost(), gerritServerHostEntryConfig.getHost());
        assertEquals(actualHostConfigEntry.getHostName(), gerritServerHostEntryConfig.getHostName());
        assertEquals(actualHostConfigEntry.getPort(), gerritServerHostEntryConfig.getPort());
        assertEquals(actualHostConfigEntry.getUsername(), gerritServerHostEntryConfig.getUsername());
    }
}

