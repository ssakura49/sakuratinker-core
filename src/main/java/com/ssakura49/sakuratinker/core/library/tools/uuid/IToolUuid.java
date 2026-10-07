package com.ssakura49.sakuratinker.core.library.tools.uuid;

import java.util.Optional;
import java.util.UUID;

public interface IToolUuid {
    Optional<UUID> getUuid();
    Optional<UUID> getOrCreateUuid();
    Optional<UUID> resetUuid();
    boolean isUuidSupported();
}
