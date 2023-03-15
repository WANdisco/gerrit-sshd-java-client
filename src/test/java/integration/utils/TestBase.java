package integration.utils;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.SshKeyInfo;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.wandisco.gerrit.client.sshd.GerritSSHClient;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(TestLogger.class)
public abstract class TestBase {
    protected static final String gerritAdminUsername = "admin";
    protected static final String gerritAdminPassword = "secret";
    protected static final String gerritTestVersion = System.getProperty("gerrit.version", "2.16.28");
    protected static final String jenkinsBuildUrl = System.getenv("BUILD_URL");

    @Container
    public static GenericContainer gerrit =
        new GenericContainer<>(String.format("gerritcodereview/gerrit:%s", gerritTestVersion)).withExposedPorts(8080, 29418)
            .withLabel("jenkins.build.url", jenkinsBuildUrl)
            .withEnv("container", "docker")
            .waitingFor(new HttpWaitStrategy().forPort(8080));

    protected static GerritSSHClient gerritAdminSSHClient;
    protected static GerritApi gerritAdminApiClient;

    protected static Integer gerritSSHPort;
    protected static Integer gerritHttpPort;
    protected static String gerritAddress;
    protected static String publicKey;
    protected static File privateKey;

    /**
     * Prep server called on every extension of TestBase ensures each forked test has own gerrit instance
     * and own clients
     *
     * @throws Exception thrown primarily from connection issues on api client.
     */
    @BeforeAll
    public static void prepServer() throws Exception {
        publicKey = readLineByLine(getFile("gerrit-client-test-key_ecdsa.pub")).trim();
        privateKey = getFile("gerrit-client-test-key_ecdsa");
        gerritSSHPort = gerrit.getMappedPort(29418);
        gerritHttpPort = gerrit.getMappedPort(8080);
        gerritAddress = gerrit.getHost();
        setAdminGerritApiClient();
        assumeFalse(publicKey.isEmpty(), "Skipping tests as public was empty string");

        getAdminGerritApiClient().accounts().self().addSshKey(publicKey);
        SshKeyInfo info = getAdminGerritApiClient().accounts().self().listSshKeys().get(0);
        assertEquals(publicKey, info.sshPublicKey);

        setAdminGerritSSHClient();
    }

    public static String getPublicKey() {
        return publicKey;
    }

    public static Integer getGerritSSHPort() {
        return gerritSSHPort;
    }

    public static Integer getGerritHttpPort() {
        return gerritHttpPort;
    }

    public static String getAdminUsername() {
        return gerritAdminUsername;
    }

    public static String getAdminPassword() {
        return gerritAdminPassword;
    }

    public static String getGerritAddress() {
        return gerritAddress;
    }

    public static String getGerritApiUrl() {
        return String.format("http://%s:%s", getGerritAddress(), getGerritHttpPort());
    }

    public static void setAdminGerritApiClient() {
        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(getGerritApiUrl(), getAdminUsername(), getAdminPassword());
        gerritAdminApiClient = gerritRestApiFactory.create(authData);
    }

    public static GerritApi getAdminGerritApiClient() {
        return gerritAdminApiClient;
    }

    public static void setAdminGerritSSHClient() {
        gerritAdminSSHClient = new GerritSSHClient(
                new GerritSSHServer.Builder(getAdminUsername(), getGerritAddress())
                        .port(getGerritSSHPort())
                        .sshKeyFile(privateKey.getAbsolutePath())
                        .loadSSHConfig(false)
                        .useKnownHosts(false)
                        .strictHostCheck(false)
                        .build());
    }


    public static GerritSSHClient getAdminGerritSSHClient() {
        return gerritAdminSSHClient;
    }

    public static GerritSSHServer getNewGerritSSHServer(String username) {
        return new GerritSSHServer.Builder(username, getGerritAddress())
                .port(getGerritSSHPort())
                .sshKeyFile(privateKey.getAbsolutePath())
                .loadSSHConfig(false)
                .useKnownHosts(false)
                .strictHostCheck(false)
                .build();
    }

    public static GerritApi getNewGerritApiClient(String username, String password) {
        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(getGerritApiUrl(), username, password);
        return gerritRestApiFactory.create(authData);
    }

    public static GerritSSHClient getNewGerritSSHClient(GerritSSHServer server) {
        return new GerritSSHClient(server);
    }

    protected static File getFile(String resourceName) throws Exception {
        URL url = getResource(resourceName);
        return new File(url.toURI());
    }

    private static String readLineByLine(File file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
            return contentBuilder.toString();
        }
    }
}
