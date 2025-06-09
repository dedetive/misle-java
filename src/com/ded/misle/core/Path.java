package com.ded.misle.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Path {
    /**
     * Returns get the root path of this project.
     *
     * @return the root path in a Path format
     */
    public static java.nio.file.Path getPath() {
        return getPath(GetPathTag.DEFAULT_TAG);
    }

    /**
     * Returns get the path of the given tag.
     *
     * @return the given tag path
     */
    public static java.nio.file.Path getPath(GetPathTag tag) {
        java.nio.file.Path workingDir = Paths.get(System.getProperty("user.dir"));

        switch (tag) {
            case ROOT -> {
                return workingDir;
            }
            case RESOURCES -> {
                return attemptToFindPath(workingDir, "resources");
            }
            case CONFIG -> {
                java.nio.file.Path configPath = attemptToFindPath(workingDir, "resources/settings.config", false);
                try {
                    if (configPath == null) Files.createFile(
                        getPath(GetPathTag.RESOURCES).resolve("settings.config"));
                } catch (IOException e) {
                    throw new RuntimeException("Could not create settings file", e);
                }
                return attemptToFindPath(workingDir, "resources/settings.config", true);
            }
            case GAME -> {
                return attemptToFindPath(workingDir, "com/ded/misle");
            }
        }
        throw new RuntimeException(tag.name() + " tag not found when getting path");
    }

    private static java.nio.file.Path attemptToFindPath(java.nio.file.Path workingDir, String path) {
        return attemptToFindPath(workingDir, path, true);
    }

    private static java.nio.file.Path attemptToFindPath(java.nio.file.Path workingDir, String path, boolean throwExceptionIfNotFound) {
        java.nio.file.Path srcBranch = workingDir.resolve("src/" + path);
        java.nio.file.Path outBranch = workingDir.resolve("out/" + path);
        java.nio.file.Path noneBranch = workingDir.resolve(path);

        if (Files.exists(outBranch)) {
            return outBranch;
        } else if (Files.exists(srcBranch)) {
            return srcBranch;
        } else if (Files.exists(noneBranch)) {
            return noneBranch;
        } else if (throwExceptionIfNotFound) {
            throw new RuntimeException(outBranch + " structure not found or incorrect");
        }
        return null;
    }

    public enum GetPathTag {
        GAME,
        RESOURCES,
        ROOT,
        CONFIG;

        public static final GetPathTag DEFAULT_TAG = ROOT;
    }
}
