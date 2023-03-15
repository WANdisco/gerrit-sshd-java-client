# gerrit-sshd-java-client

![Coverage](.github/badges/jacoco.svg) ![Branches](.github/badges/branches.svg)


A Java SSH client for use with [Gerrit Code Review Tool](https://www.gerritcodereview.com/)

Uses Apache Mina to create an execute channel to Gerrit

Contains command objects and builders for construction and execution of commands against Gerrits SSH daemon.

### Dependency entry:

```xml
  <depenencies>
    <dependency>
      <groupId>com.wandisco.gerrit.client.sshd</groupId>
      <artifactId>gerrit-sshd-java-client</artifactId>
      <version>0.0.5-SNAPSHOT</version>
    </dependency>
  </depenencies>
```

## Usage guide:

Create a GerritSSHServer object either in line or with builder.
```java
    return  new GerritSSHServer.Builder("admin", localhost)
                .port(29418)
                .sshKeyFile(/some/path/to/userkey)
                .build();
```

This is then used to initialise a GerritSSHClient for that user and server
```java
    GerritSSHClient adminClient = new GerritSSHClient(getAdminGerritSSHServer())
```

This can then be used to execute command string to the gerrit sshd
```java
    Command outputCommand = adminClient.executeCommand("gerrit");
    assertTrue(outputCommand.isSuccessful());
    System.out.println(outputCommand.getOutput());
```
Or by passing in a GerritCommand Object

```java
    CreateProjectCommand createProjectCommand = new CreateProjectCommand("newProject1");

    createProjectCommand.setSubmitType(ProjectSubmitTypes.MERGE_IF_NECESSARY);
    createProjectCommand.setEmptyCommit();
    createProjectCommand.setOwner("Administrators");
    createProjectCommand.setDescription("A Brand new Project");
    createProjectCommand.setBranch("notMaster");

    createProjectCommand = (CreateProjectCommand) getAdminGerritSSHClient().executeCommand(createProjectCommand);
    assertTrue(createProjectCommand.isSuccessful());
    createProjectCommand.getDuration;
```

Most GerritCommand Objects will have a builder class for optional command flags
```java
    CreateProjectCommand createProjectCommand = new CreateProjectCommand
                .CreateProjectCommandBuilder("newProject2")
                .submitType(ProjectSubmitTypes.MERGE_IF_NECESSARY)
                .emptyCommit()
                .owner("Administrators")
                .description("A Brand New Project")
                .branch("notMaster")
                .build();

    createProjectCommand = (CreateProjectCommand) getAdminGerritSSHClient().executeCommand(createProjectCommand);
    assertTrue(createProjectCommand.isSuccessful());
    createProjectCommand.getDuration;
```

Can also be used to clone repositories from gerrit over ssh as below
```java
    File target = new File(projectName);
        getAdminGerritSSHClient().executeCommand(createProjectCommand);
        Git repo = getAdminGerritSSHClient()
                .cloneRepo(projectName,target);

        assertTrue(target.exists());

        repo.log().call().forEach(revCommit -> System.out.println(revCommit.getShortMessage()));
        repo.branchCreate().setName("Test").call();
        repo.push().add("Test").call();
        repo.branchList().setListMode(ListMode.REMOTE).call().forEach(ref -> System.out.println(ref.getName()));
```

## Building

Builds using maven with unit tests and integration test run separately

```shell script
  ./mvnw clean install
```

Integration tests under [src/test/java/integration](./src/test/java/integration) are run using
testContainers to host a local docker image of gerrit to run commands against

As such will need to have a local docker configuration such as docker engine,  
docker for mac or docker machine.

The following property in the pom dictates which gerrit version to use during testing

```xml

<gerrit.version>2.16.28</gerrit.version>
```

and can be overridden at run time with `-Dgerrit.version` as an example

```shell script
  ./mvnw clean install -Dgerrit.version=latest
```

Test's currently default to target Gerrit [v2.16.28](https://www.gerritcodereview.com/2.16.html) but has been confirmed against
Gerrit 3.7.1

Integration tests can be skipped by adding `-DskipITs` to command line execution.

```shell script
  ./mvnw clean install -DskipITs
```

# Dependencies
------------
Uses the following [Apache MINA SSHD](https://github.com/apache/mina-sshd) assets:
[sshd-git](https://github.com/apache/mina-sshd/tree/master/sshd-git)
[sshd-scp](https://github.com/apache/mina-sshd/tree/master/sshd-scp)

Uses the gerrit-rest-java-client for testing found here:
[gerrit-rest-java-client](https://github.com/uwolfer/gerrit-rest-java-client)

Testcontainers also used for integration testing:
[testcontainers](https://www.testcontainers.org/)

# TODO
------------
List of current Features to be added to project:

- [ ] Parsing and Objects for command outputs.

# license
--------------------

Copyright 2021-2023 WANdisco

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
