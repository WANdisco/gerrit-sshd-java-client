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

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.sshd.client.config.hosts.HostConfigEntry;

public class GerritSSHServer {

    private final String host;
    private final int port;
    private final String username;
    private final List<File> sshKeyFiles;
    private boolean useKnownHosts = false;
    private boolean strictHostCheck = true;
    private boolean loadSSHConfig = false;
    private HostConfigEntry hostConfigEntry;

    /**
     * Object representing the Gerrit server
     *
     * @param host String of the host name to connect to
     * @param port String current Gerrit SSH port
     * @param username String user to connect as
     * @param sshKeyFileList List of {@link File} type of private keys to send.
     */
    public GerritSSHServer(String host, int port, String username, List<File> sshKeyFileList) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.sshKeyFiles = sshKeyFileList;
    }

    /**
     * Object representing the Gerrit server
     *
     * @param host String of the host name to connect to
     * @param port String current Gerrit SSH port
     * @param username String user to connect as
     * @param sshKeyFileList List of {@link File} type of private keys to send.
     * @param useKnownHosts boolean flag of whether to use host machines known_hosts file
     * @param strictHostCheck boolean flag of whether to use Strict host checking
     * @param loadSSHConfig boolean flag of whether to use host machines ssh config
     */
    public GerritSSHServer(String host, int port, String username, List<File> sshKeyFileList, boolean useKnownHosts,
        boolean strictHostCheck, boolean loadSSHConfig) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.sshKeyFiles = sshKeyFileList;
        this.useKnownHosts = useKnownHosts;
        this.strictHostCheck = strictHostCheck;
        this.loadSSHConfig = loadSSHConfig;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public List<File> getSshKeyFiles() {
        return sshKeyFiles;
    }

    public boolean getUseKnownHosts() {
        return useKnownHosts;
    }

    public void setUseKnownHosts(boolean useKnownHosts) {
        this.useKnownHosts = useKnownHosts;
    }

    public HostConfigEntry getHostConfigEntry() {
        if (hostConfigEntry == null) {
            hostConfigEntry = new HostConfigEntry();
            hostConfigEntry.setHostName(host);
            hostConfigEntry.setHost(host);
            hostConfigEntry.setUsername(username);
            hostConfigEntry.setPort(port);
        }
        return hostConfigEntry;
    }

    public void setHostConfigEntry(HostConfigEntry hostConfigEntry) {
        this.hostConfigEntry = hostConfigEntry;
    }

    public boolean getStrictHostCheck() {
        return strictHostCheck;
    }

    public void setStrictHostCheck(boolean strictHostCheck) {
        this.strictHostCheck = strictHostCheck;
    }

    public boolean getLoadSSHConfig() {
        return loadSSHConfig;
    }

    public void setLoadSSHConfig(boolean loadSSHConfig) {
        this.loadSSHConfig = loadSSHConfig;
    }

    @Override
    public String toString() {
        return "GerritSSHServer{" + "host='" + host + '\'' + ", port=" + port + ", username='" + username + '\'' + '\'' + ", sshKeyPath="
            + Arrays.toString(sshKeyFiles.stream().map(File::getPath).toArray()) + '}';
    }

    public static class Builder {
        private final String host;
        private final String username;
        private final List<File> sshKeyFiles = new LinkedList<>();
        private boolean useKnownHosts = false;
        private boolean strictHostCheck = true;
        private boolean loadSSHConfig = false;
        private int port = 29418;

        public Builder(String username, String host) {
            this.username = username;
            this.host = host;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder sshKeyFile(String sshKeyPath) {
            sshKeyFiles.add(new File(sshKeyPath));
            return this;
        }

        public Builder useKnownHosts(boolean useKnownHosts) {
            this.useKnownHosts = useKnownHosts;
            return this;
        }

        public Builder loadSSHConfig(boolean loadSSHConfig) {
            this.loadSSHConfig = loadSSHConfig;
            return this;
        }

        public Builder strictHostCheck(boolean strictHostCheck) {
            this.strictHostCheck = strictHostCheck;
            return this;
        }

        public GerritSSHServer build() {
            return new GerritSSHServer(host, port, username, sshKeyFiles, useKnownHosts, strictHostCheck, loadSSHConfig);
        }
    }
}
