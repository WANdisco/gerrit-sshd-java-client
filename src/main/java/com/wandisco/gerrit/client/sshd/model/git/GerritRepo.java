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

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.SubmoduleAddCommand;
import org.eclipse.jgit.api.SubmoduleUpdateCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;

/**
 * Extended {@link Git} Object Point is to ensure transactions that communicate with server use the {@link GerritSshdSessionFactory} for the
 * site that initiated the clone. This is to avoid issues when multiple instances of Clones or clients point to different servers.
 */
public class GerritRepo extends Git implements AutoCloseable {

    private GerritSshdSessionFactory sshFactory;
    private GerritTransportConfigCallback gerritTransportConfigCallback;

    public static GerritRepo open(File dir) throws IOException {
        return open(dir, FS.DETECTED);
    }

    public static GerritRepo open(File dir, FS fs) throws IOException {
        RepositoryCache.FileKey key = FileKey.lenient(dir, fs);
        Repository db = ((RepositoryBuilder) ((RepositoryBuilder) ((RepositoryBuilder) (new RepositoryBuilder()).setFS(fs)).setGitDir(
            key.getFile())).setMustExist(true)).build();
        return new GerritRepo(db);
    }

    public static GerritRepo wrap(Repository repo) {
        return new GerritRepo(repo);
    }

    public GerritRepo(Repository repo) {
        super(repo);
    }

    public GerritRepo(Repository repo, GerritSshdSessionFactory sshFactory) {
        super(repo);
        this.sshFactory = sshFactory;
        gerritTransportConfigCallback = new GerritTransportConfigCallback();
    }

    public void setSshFactory(GerritSshdSessionFactory sshFactory) {
        this.sshFactory = sshFactory;
        gerritTransportConfigCallback = new GerritTransportConfigCallback();
    }

    /**
     * @return {@link PullCommand} with our transportConfigCallBack set.
     */
    public PullCommand pull() {
        if (getSshFactory() == null) {
            return super.pull();
        } else {
            return super.pull().setTransportConfigCallback(gerritTransportConfigCallback);
        }
    }

    /**
     * @return {@link FetchCommand} with our transportConfigCallBack set.
     */
    public FetchCommand fetch() {
        if (getSshFactory() == null) {
            return super.fetch();
        } else {
            return super.fetch().setTransportConfigCallback(gerritTransportConfigCallback);
        }
    }

    /**
     * @return {@link PushCommand} with our transportConfigCallBack set.
     */
    public PushCommand push() {
        if (getSshFactory() == null) {
            return super.push();
        } else {
            return super.push().setTransportConfigCallback(gerritTransportConfigCallback);
        }
    }

    /**
     * @return {@link LsRemoteCommand} with our transportConfigCallBack set.
     */
    public LsRemoteCommand lsRemote() {
        if (getSshFactory() == null) {
            return super.lsRemote();
        } else {
            return super.lsRemote().setTransportConfigCallback(gerritTransportConfigCallback);
        }
    }

    /**
     * @return {@link SubmoduleUpdateCommand} with our transportConfigCallBack set.
     */
    public SubmoduleAddCommand submoduleAdd() {
        if (getSshFactory() == null) {
            return super.submoduleAdd();
        } else {
            return super.submoduleAdd().setTransportConfigCallback(gerritTransportConfigCallback);
        }
    }

    /**
     * @return {@link SubmoduleUpdateCommand} with our transportConfigCallBack set.
     */
    public SubmoduleUpdateCommand submoduleUpdate() {
        if (getSshFactory() == null) {
            return super.submoduleUpdate();
        } else {
            return super.submoduleUpdate().setTransportConfigCallback(gerritTransportConfigCallback);
        }
    }

    protected GerritSshdSessionFactory getSshFactory() {
        return sshFactory;
    }

    /**
     * Class to use for {@link TransportConfigCallback} overrides set on {@link TransportConfigCallback#configure(Transport)} this ensures
     * that the sshFactory is always the one for this instance.
     */
    private class GerritTransportConfigCallback implements TransportConfigCallback {
        @Override
        public void configure(Transport transport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshFactory);
        }
    }
}
