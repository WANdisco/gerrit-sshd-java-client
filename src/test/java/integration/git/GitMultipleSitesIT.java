package integration.git;

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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.projects.BranchInfo;
import com.google.gerrit.extensions.common.SshKeyInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.wandisco.gerrit.client.sshd.GerritSSHClient;
import com.wandisco.gerrit.client.sshd.commands.projects.CreateProjectCommand;
import com.wandisco.gerrit.client.sshd.commands.projects.ProjectSubmitTypes;
import com.wandisco.gerrit.client.sshd.model.sshSession.GerritSSHServer;
import integration.utils.TestBase;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class GitMultipleSitesIT extends TestBase {


    protected  static GerritSSHClient gerrit2AdminSSHClient;
    protected  static GerritApi gerrit2AdminApiClient;

    private  static Integer gerrit2SSHPort;
    private static Integer gerrit2HttpPort;
    private  static String gerrit2Address;

    @Container
    public static final GenericContainer<?> gerrit2 =
        new GenericContainer<>(String.format("gerritcodereview/gerrit:%s", gerritTestVersion)).withExposedPorts(8080, 29418)
            .withLabel("jenkins.build.url", jenkinsBuildUrl).withEnv("container", "docker")
            .waitingFor(new HttpWaitStrategy().forPort(8080));
    @TempDir
    public File folder;

    @BeforeAll
    public static void initClients() throws RestApiException {
        gerrit2SSHPort = gerrit2.getMappedPort(29418);
        gerrit2HttpPort = gerrit2.getMappedPort(8080);
        gerrit2Address = gerrit2.getHost();
        setAdminGerrit2ApiClient(String.format("http://%s:%s", gerrit2Address, gerrit2HttpPort));
        gerrit2AdminApiClient.accounts().self().addSshKey(publicKey);
        SshKeyInfo info = gerrit2AdminApiClient.accounts().self().listSshKeys().get(0);
        assertEquals(publicKey, info.sshPublicKey);
        setAdminGerrit2SSHClient(gerrit2Address,gerrit2SSHPort);
    }

    @Test
    public void testMultipleClones() throws IOException, GitAPIException, RestApiException {
        GerritSSHClient client1 = getAdminGerritSSHClient();
        GerritSSHClient client2 = gerrit2AdminSSHClient;

        String projectName = RandomStringUtils.randomAlphanumeric(10);
        String projectName2 = RandomStringUtils.randomAlphanumeric(10);

        //Create New project on first site
        CreateProjectCommand createProjectCommand =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();
        client1.executeCommand(createProjectCommand);

        //Create New project on second site
        createProjectCommand =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName2).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();
        client2.executeCommand(createProjectCommand);

        // clone the repo from site1 to a target directory
        File target = new File(folder, projectName);
        File target2 = new File(folder, projectName2);

        try (Git repo1 = client1.cloneRepo(projectName, target); Git repo2 = client2.cloneRepo(projectName2, target2);) {
            assertAll(() -> assertTrue(target.exists()), () -> assertTrue(target2.exists()));
            String branch1 = pushBranchAndCheck(repo1);
            String branch2 = pushBranchAndCheck(repo2);
            checkBranchOnAPI(getAdminGerritApiClient(), projectName, branch1);
            checkBranchOnAPI(gerrit2AdminApiClient, projectName2, branch2);
        }

    }

    private String pushBranchAndCheck(Git repo) throws GitAPIException {
        String branchName = RandomStringUtils.randomAlphanumeric(10);
        repo.branchCreate().setName(branchName).call();
        repo.push().add(branchName).call();
        //check that the remote branch has our new branch
        boolean found = false;
        List<Ref> remoteRefs = repo.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        for (Ref current : remoteRefs) {
            if (current.getName().contains(branchName)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Failed to find branch in list" + branchName);
        return branchName;
    }

    private void checkBranchOnAPI(GerritApi client, String project, String branchToFind) throws RestApiException {
        boolean found = false;
        for (BranchInfo branch : client.projects().name(project).branches().get()){
            System.out.println(branch.ref);
            if (branch.ref.equals("refs/heads/"+branchToFind)){
                found = true;
                break;
            }
        }
        assertTrue(found, "did not find branch Test on node: " + client);
    }

    private static void setAdminGerrit2ApiClient(String apiUrl) {
        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic(apiUrl, getAdminUsername(), getAdminPassword());
        gerrit2AdminApiClient = gerritRestApiFactory.create(authData);
    }

    private static void setAdminGerrit2SSHClient(String address, int port) {
        gerrit2AdminSSHClient = new GerritSSHClient(
                new GerritSSHServer.Builder(getAdminUsername(), address)
                        .port(port)
                        .sshKeyFile(privateKey.getAbsolutePath())
                        .loadSSHConfig(false)
                        .useKnownHosts(false)
                        .strictHostCheck(false)
                        .build());
    }
}
