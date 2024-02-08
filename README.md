[![Release](https://jitpack.io/v/umjammer/vavi-nio-file-dropbox.svg)](https://jitpack.io/#umjammer/vavi-nio-file-dropbox)
[![Actions Status](https://github.com/umjammer/vavi-nio-file-dropbox/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-nio-file-dropbox/actions)
[![CodeQL](https://github.com/umjammer/vavi-nio-file-dropbox/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-nio-file-dropbox/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)
[![Parent](https://img.shields.io/badge/Parent-vavi--apps--fuse-pink)](https://github.com/umjammer/vavi-apps-fuse)

## vavi-nio-file-dropbox

Java filesystem SPI ([JSR-203](https://jcp.org/en/jsr/detail?id=203)) over [DropBox](https://dropbox.com) API.

[original](https://github.com/fge/java7-fs-dropbox)

## Install

### jars

 * https://jitpack.io/#umjammer/vavi-nio-file-dropbox

### ~~selenium chrome driver~~ (obsoleted, use default browser)

 * Download the [chromedriver executable](https://chromedriver.chromium.org/downloads) and locate it into some directory.
   * Don't forget to run jvm with jvm argument `-Dwebdriver.chrome.driver=/usr/local/bin/chromedriver`.

## Usage

First of all, you MUST have a Dropbox account. Then you need to [register an
application](https://www.dropbox.com/developers/apps). This application must have **full access**,
and handle any types of files.

Next, prepare 2 property files.

 * application credential

```shell
$ cat ${HOME}/.vavifuse/dropbox.properties
dropbox.applicationName=your_application_name
dropbox.clientId=your_client_id
dropbox.clientSecret=your_client_secret
dropbox.redirectUrl=http://localhost:30000
dropbox.scopes=files.content.write
```

 * user credential

```shell
$ cat ${HOME}/.vavifuse/credentials.properties
dropbox.password.xxx@yyy.zzz=your_password
```

Then write your code! Here is a short example (imports omitted for brevity):

```java
public class Main {

    public static void main(String[] args) throws IOException {
        String email = "xxx@yyy.zzz";

        URI uri = URI.create("dropbox:///?id=" + email);

        FileSystem fs = FileSystems.newFileSystem(uri, env);
            :
    }
}
```

### See also

https://github.com/umjammer/vavi-apps-fuse/blob/master/vavi-nio-file-gathered/src/test/java/vavi/nio/file/dropbox/Main.java

## TODO

 * ~~project name to vavi-nio-file-dropbox~~
