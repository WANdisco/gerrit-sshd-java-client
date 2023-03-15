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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.wandisco.gerrit.client.sshd.GerritSSHClient;
import com.wandisco.gerrit.client.sshd.commands.GerritCommand;
import com.wandisco.gerrit.client.sshd.commands.changes.ReviewCommand.ReviewCommandBuilder;
import com.wandisco.gerrit.client.sshd.commands.projects.CreateProjectCommand;
import com.wandisco.gerrit.client.sshd.commands.projects.ProjectSubmitTypes;
import com.wandisco.gerrit.client.sshd.model.exception.SCPDownloadException;
import integration.utils.TestBase;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


public class GitIT extends TestBase {

    @TempDir
    public File folder;

    @Test
    public void testClone() throws GitAPIException, IOException {
        String projectName = RandomStringUtils.randomAlphanumeric(10);

        CreateProjectCommand createProjectCommand =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();

        //Create New project
        getAdminGerritSSHClient().executeCommand(createProjectCommand);

        // clone the repo to a target directory
        File target = new File(folder, projectName);
        Git repo = getAdminGerritSSHClient().cloneRepo(projectName, target);
        repo.close();
        // Check repo is now on disc
        assertTrue(target.exists());
    }

    @Test
    public void testMulitpleClones() throws IOException, GitAPIException {
        GerritSSHClient client1 = getAdminGerritSSHClient();
        GerritSSHClient client2 = getNewGerritSSHClient(client1.getServer());

        String projectName = RandomStringUtils.randomAlphanumeric(10);
        String projectName2 = RandomStringUtils.randomAlphanumeric(10);

        CreateProjectCommand createProjectCommand =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();

        //Create New project
        getAdminGerritSSHClient().executeCommand(createProjectCommand);

        CreateProjectCommand createProjectCommand2 =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName2).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();
        getAdminGerritSSHClient().executeCommand(createProjectCommand2);
        File target2 = new File(folder, projectName2);
        File target = new File(folder, projectName);
        try (Git repo2 = client2.cloneRepo(projectName2, target2); Git repo = getAdminGerritSSHClient().cloneRepo(projectName, target)) {
            // Check clones are now on disc
            assertAll(() -> assertTrue(target.exists()), () -> assertTrue(target2.exists()));
            repo.branchCreate().setName("Test").call();
            repo.push().add("Test").call();
            //check that the remote branch has our new branch
            boolean found = false;
            List<Ref> remoteRefs = repo.branchList().setListMode(ListMode.REMOTE).call();
            for (Ref current : remoteRefs) {
                if (current.getName().contains("Test")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);

            repo2.branchCreate().setName("Test2").call();
            repo2.push().add("Test2").call();
            //check that the remote branch has our new branch
            boolean found2 = false;
            List<Ref> remoteRefs2 = repo2.branchList().setListMode(ListMode.REMOTE).call();
            for (Ref current : remoteRefs2) {
                if (current.getName().contains("Test")) {
                    found2 = true;
                    break;
                }
            }
            assertTrue(found2);
        }
    }

    @Test
    public void testPushBranch() throws GitAPIException, IOException {
        String projectName = RandomStringUtils.randomAlphanumeric(10);

        CreateProjectCommand createProjectCommand =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();

        //Create New project
        getAdminGerritSSHClient().executeCommand(createProjectCommand);

        // clone the repo to a target directory
        File target = new File(folder, projectName);
        try (Git repo = getAdminGerritSSHClient().cloneRepo(projectName, target)) {
            // Check repo is now on disc
            assertTrue(target.exists());
            // push a new branch to remote
            repo.branchCreate().setName("Test").call();
            repo.push().add("Test").call();
            //check that the remote branch has our new branch
            boolean found = false;
            List<Ref> remoteRefs = repo.branchList().setListMode(ListMode.REMOTE).call();
            for (Ref current : remoteRefs) {
                if (current.getName().contains("Test")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void testCloneWithHook() throws GitAPIException, IOException, SCPDownloadException {
        String projectName = RandomStringUtils.randomAlphanumeric(10);

        CreateProjectCommand createProjectCommand = new CreateProjectCommand
                .CreateProjectCommandBuilder(projectName)
                .submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").branch("notMaster").build();

        //Create New project
        getAdminGerritSSHClient().executeCommand(createProjectCommand);

        // clone the repo to a target directory
        File target = new File(folder, projectName);
        try (Git repo = getAdminGerritSSHClient().cloneRepo(projectName, target)) {
            // Check repo is now on disc
            assertTrue(target.exists());
            getAdminGerritSSHClient().getCommitMsgHook(repo);
            assertTrue(new File(target, ".git/hooks/commit-msg").exists());
        }
    }

    @Test
    public void testGetChangeID() throws IOException, GitAPIException, RestApiException {
        String projectName = RandomStringUtils.randomAlphanumeric(10);

        CreateProjectCommand createProjectCommand =
            new CreateProjectCommand.CreateProjectCommandBuilder(projectName).submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit().owner("Administrators").description("A Brand New Project").build();

        //Create New project
        getAdminGerritSSHClient().executeCommand(createProjectCommand);

        // clone the repo to a target directory
        File target = new File(folder, projectName);
        try (Git repo = getAdminGerritSSHClient().cloneRepo(projectName, target)) {
            File testFile = File.createTempFile("junit", "pushfile", target);
            repo.add().addFilepattern(testFile.getName()).call();
            repo.commit().setInsertChangeId(true).setMessage("testing").call();
            String changeId = "";
            String committedSha = "";
            for (RevCommit commit : repo.log().setMaxCount(1).call()) {
                for (String out : commit.getFooterLines("Change-Id")) {
                    changeId = out;
                }
                committedSha = commit.getId().abbreviate(7).name();
            }
            //        RefSpec spec1 = new RefSpec(Constants.HEAD + ":" + "master");
            Iterable<PushResult> resultList = repo.push().setRemote("origin").add("master").call();
            for (PushResult result : resultList) {
                assertFalse(result.getMessages().contains("Rejected"));
            }
            RefSpec spec2 = new RefSpec(Constants.HEAD + ":" + "refs/for/master%merged");
            repo.push().setRefSpecs(spec2).call();
            GerritCommand output =
                (GerritCommand) getAdminGerritSSHClient().executeCommand(new ReviewCommandBuilder(committedSha).codeReview("+2").build());
            assertTrue(output.isSuccessful(), "Failed to apply code review to merged review");
            ChangeInfo change = gerritAdminApiClient.changes().id(projectName, "master", changeId).get();
            assertEquals(2, (int) change.labels.get("Code-Review").all.get(0).value);
        }
    }
}
