package com.github.fge.fs.dropbox;

import com.dropbox.core.v2.files.Metadata;
import com.github.fge.filesystem.driver.ExtendedFileSystemDriverBase.ExtendedFileAttributesFactory;

public final class DropBoxFileAttributesFactory
    extends ExtendedFileAttributesFactory
{
    public DropBoxFileAttributesFactory()
    {
        setMetadataClass(Metadata.class);
        addImplementation("basic", DropBoxBasicFileAttributesProvider.class);
    }
}
