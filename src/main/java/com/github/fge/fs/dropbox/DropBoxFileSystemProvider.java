package com.github.fge.fs.dropbox;

import com.github.fge.filesystem.driver.DoubleCachedFileSystemDriver;
import com.github.fge.filesystem.provider.FileSystemProviderBase;

public final class DropBoxFileSystemProvider
    extends FileSystemProviderBase
{
    public static final String PARAM_ID = "id";

    public static final String ENV_USER_CREDENTIAL = "user_credential";

    public static final String ENV_APP_CREDENTIAL = "app_credential";

    public static final String ENV_IGNORE_APPLE_DOUBLE = "ignoreAppleDouble";

    public static final String ENV_USE_SYSTEM_WATCHER = "use_system_watcher";

    public static final String ENV_DISABLED_FILE_CACHE = DoubleCachedFileSystemDriver.ENV_DISABLED_FILE_CACHE;

    public DropBoxFileSystemProvider()
    {
        super(new DropBoxFileSystemRepository());
    }
}
