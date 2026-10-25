/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.fs.dropbox;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderGetLatestCursorResult;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.github.fge.fs.dropbox.webhook.websocket.DropBoxNotification;
import vavi.nio.file.watch.webhook.WebHookBaseWatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;


/**
 * DropBoxWatchService.
 * <p>
 * notification source is none (dropbox always send notifications for all accounts of the app),
 * changes are retrieved by the list folder cursor.
 * <p>
 * system properties
 * <ul>
 * <li> vavi.nio.file.watch.webhook.NotificationProvider.dropbox
 * </ul>
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/07/22 umjammer initial version <br>
 * @see "https://github.com/aishraj/dropbox-webhooks-java-example/blob/master/app/controllers/Application.java"
 * @see "https://www.dropbox.com/developers/reference/webhooks"
 */
public class DropBoxWatchService extends WebHookBaseWatchService<DropBoxNotification> {

    private static final Logger logger = System.getLogger(DropBoxWatchService.class.getName());

    private static final String WEBHOOK_NOTIFICATION_PROVIDER =
            System.getProperty("vavi.nio.file.watch.webhook.NotificationProvider.dropbox", ".dropbox.webhook.websocket");

    /**
     * a notification is delivered only to the first instance's callback (see {@link WebHookBaseWatchService#setupNotification}),
     * so dispatch it to the instances by account id.
     */
    private static final Set<DropBoxWatchService> services = new CopyOnWriteArraySet<>();

    private final DbxClientV2 client;

    /** the account to be watched */
    private final String accountId;

    private String cursor;

    /** */
    public DropBoxWatchService(DbxClientV2 client) throws IOException {
        this.client = client;

        setupNotification(this, WEBHOOK_NOTIFICATION_PROVIDER);

        try {
            accountId = client.users().getCurrentAccount().getAccountId();
logger.log(Level.TRACE, "DROPBOX: account: " + accountId);

            cursor = getLatestCursor();
logger.log(Level.TRACE, "DROPBOX: cursor: " + cursor);
        } catch (DbxException e) {
            throw new IOException(e);
        }

        services.add(this);
    }

    /** */
    private String getLatestCursor() throws DbxException {
        ListFolderGetLatestCursorResult result = client.files()
                .listFolderGetLatestCursorBuilder("")
                .withIncludeDeleted(true)
                .withIncludeMediaInfo(false)
                .withRecursive(true)
                .start();
        return result.getCursor();
    }

    @Override
    protected void onNotifyMessage(DropBoxNotification notification) throws IOException {
logger.log(Level.TRACE, ">> notification: " + notification);

        if (notification == null || notification.listFolder == null || notification.listFolder.accounts == null) {
logger.log(Level.TRACE, ">> notification: unknown format");
            return;
        }

        for (DropBoxWatchService service : services) {
            if (notification.listFolder.accounts.contains(service.accountId)) {
                service.processChanges();
            } else {
logger.log(Level.TRACE, ">> notification is not for this account: " + service.accountId);
            }
        }

logger.log(Level.TRACE, ">> notification: done");
    }

    /** retrieves changes since the last saved cursor */
    private synchronized void processChanges() throws IOException {
        if (!isOpen()) {
            return;
        }
        try {
            boolean hasMore = true;
            while (hasMore) {
                ListFolderResult result;
                try {
                    result = client.files().listFolderContinue(cursor);
                } catch (ListFolderContinueErrorException e) {
                    if (e.errorValue.isReset()) {
                        // the cursor is invalidated, changes during that are lost
logger.log(Level.WARNING, "DROPBOX: cursor is reset");
                        cursor = getLatestCursor();
                        return;
                    }
                    throw e;
                }
                for (Metadata metadata : result.getEntries()) {
logger.log(Level.TRACE, ">> " + metadata.getClass().getName() + ": " + metadata);
                    if (metadata instanceof FileMetadata || metadata instanceof FolderMetadata) {
                        listener.accept(metadata.getPathDisplay(), ENTRY_MODIFY);
                    } else if (metadata instanceof DeletedMetadata) {
                        listener.accept(metadata.getPathDisplay(), ENTRY_DELETE);
                    }
                }

                cursor = result.getCursor();
                hasMore = result.getHasMore();
            }
        } catch (DbxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            super.close();

            services.remove(this);
        }
    }
}
