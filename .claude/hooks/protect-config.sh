#!/bin/bash
# Hook: Protects config files from being modified by Claude
# Runs on PreToolUse for Write and Edit
# Exit 0 = allowed, Exit 2 = blocked

INPUT=$(cat)

# Extract file path from tool input
FILE_PATH=$(echo "$INPUT" | grep -o '"file_path"[[:space:]]*:[[:space:]]*"[^"]*"' | head -1 | sed 's/.*"file_path"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/')

# List of protected files (relative names)
FILENAME=$(basename "$FILE_PATH")

case "$FILENAME" in
    application.properties)
        echo "=== BLOCKED ==="
        echo "The file '$FILENAME' is protected and must not be modified by Claude."
        echo "Config and build files require manual changes only."
        exit 2
        ;;
esac

exit 0
